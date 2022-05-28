package com.harrys_it.ots.ports;

import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.mcu.*;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.facade.GpioFacade;
import com.harrys_it.ots.facade.OsFacade;
import com.harrys_it.ots.facade.PcbFacade;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class SocketResource implements PropertyChangeListener {
	private final PcbFacade pcbFacade;
	private final OsFacade osFacade;
	private final GpioFacade gpioFacade;
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private BufferedReader input;
	private PrintWriter output;
	private volatile boolean clientAlive;
	private volatile boolean clientConnected;
	private volatile boolean waitingForConnection;
	private Thread isClientAliveThread;
	private Thread sendAndReceiveThread;
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

	private static final String NOT_VALID_EXEC_COMMAND = "NOT_VALID_EXEC_COMMAND;";
	private static final Logger log = LoggerFactory.getLogger(SocketResource.class);

	public SocketResource(BroadcasterService broadcasterService,
                          PcbFacade pcbFacade,
                          OsFacade osFacade,
                          GpioFacade gpioFacade,
						  @Value("${socket.enable}") boolean startService,
						  @Value("${socket.port}") int port){
		this.pcbFacade = pcbFacade;
		this.osFacade = osFacade;
		this.gpioFacade = gpioFacade;
		if(startService) {
			log.debug("Started");
			broadcasterService.addPropertyChangeListener(this);
			run(port);
		}
	}

	public boolean isClientAlive() {
		return clientAlive;
	}

	public boolean isClientConnected() {
		return clientConnected;
	}

	public boolean isWaitingForConnection() {
		return waitingForConnection;
	}


	private void run(int port) {
		new Thread(() -> {
			while (true) {
				try {
					connectToSocket(port);
					listenForHeartbeatsThread();
					listenForIncomingStringThread();
					joinAllThreads();
					log.debug("Client disconnected from server / Connection lost");
					cleanUpSocketAndInOutput();
				} catch (IOException e) {
					cleanUpSocketAndInOutput();
					log.debug("startListeningForClient(): ", e);
				}
			}
		}).start();
	}

	public void connectToSocket(int inPort) throws IOException {
		serverSocket = new ServerSocket(inPort);
		serverSocket.setReuseAddress(true);
		clientConnected = false;

		log.debug("Waiting for client to connect on port: {}", inPort);
		waitingForConnection = true;
		socket = serverSocket.accept();
		log.debug("Client Connected");
		clientConnected = true;

		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(), true);
	}

	private void listenForHeartbeatsThread() {
		isClientAliveThread = new Thread(() -> {
			while (clientConnected) {
				threadSleep();
				if (!clientAlive) {
					clientConnected = false;
					try {
						socket.close();
					} catch (IOException e) {
						log.debug("checkIfClientIsStillConnected: socket could´t close", e);
					}
				}
			}
		});
		isClientAliveThread.start();
	}

	private void listenForIncomingStringThread() {
		sendAndReceiveThread = new Thread(() -> {
			while (clientConnected) {
				String inputString;
				try {
					inputString = input.readLine();
				} catch (IOException e1) {
					log.debug("sendReceiveToClient(): interrupt()");
					clientConnected = false;
					isClientAliveThread.interrupt();
					return;
				}

				log.debug("From client: {}", inputString);
				new Thread(() -> {
					try {
						incomingCommand(inputString);
					} catch (InterruptedException e2) {
						log.debug("From client exception", e2);
						Thread.currentThread().interrupt();
					}
				}).start();
			}
		});
		sendAndReceiveThread.start();
	}

	public void incomingCommand(String inputString) throws InterruptedException {
		if (isMcuString(inputString)) {
			Integer[] cmdAndData = McuUtils.convertStringToCmdIntAndDataInt(inputString);
			Integer cmd = cmdAndData[0];
			Integer data = cmdAndData[1];
			log.debug("mcuCommand: cmd, data: {}, {}", cmd, data);
			if(cmd!=null && data!=null) {
				var res = sendToMcuController(cmd, data);
				output.println(res);
			}
		} else if (inputString.equals("CExit;")) {
			clientConnected = false;
			return;
		} else if (inputString.equals("CAlive;")) {
			clientAlive = true;
		} else if (inputString.equals("CReboot;")) {
			osFacade.restart();
			output.println("RESOs,CReboot;ACK;");
		} else if (inputString.equals("CShutdown;")) {
			osFacade.shutdown();
			output.println("RESOs,CShutdown;ACK;");
		} else if (inputString.equals("CWifion;")) {
			osFacade.setPhysicalWifiEnable(true);
			output.println("RESOs,CWifion;ACK;");
		} else if (inputString.equals("CWifioff;")) {
			osFacade.setPhysicalWifiEnable(false);
			output.println("RESOs,CWifioff;ACK;");
		} else if(inputString.startsWith("CGpio")) {
			String res = executeGpio(inputString);
			output.println("RESCGpio," + res);
		} else if(inputString.startsWith("CAudio,")) {
			String res = executeAudio(inputString);
			output.println("RESCAudio," + inputString + res);
		} else {
			output.println(McuError.NOT_VALID);
		}
		output.flush();
	}

	private boolean isMcuString(String input) {
		return input.length() >= 6 && input.charAt(0) == 'C' && input.contains(",") && input.contains("D") && input.contains(";");
	}

	private String executeAudio(String command) {
		String format;
		String filename;
		try {
			format = command.substring(command.length() - 4, command.length() - 1);
			filename = command.substring(7, command.length() - 1);
		} catch (IndexOutOfBoundsException e) {
			log.debug("Invalid format or filename", e);
			return "NACK;";
		}
		if (format.equals("wav")) {
			osFacade.playWav(filename);
		} else if (format.equals("mp3")) {
			osFacade.playMp3(filename);
		} else {
			return "NACK;";
		}
		return "ACK;";
	}

	private String sendToMcuController(int cmd, int data) {
		output.flush();
		var resFromMcu = sendToMcu(new McuEvent(McuCommand.fromInt(cmd), data));
		var ackOrNackOrInt = McuUtils.returnValueAsACKorNACKorInt(cmd, resFromMcu);
		return "RES" + cmd + "," + ackOrNackOrInt + ";";
	}

	public int sendToMcu(McuEvent mcuEvent) {
		return pcbFacade.sendToMcu(mcuEvent);
	}

	private String executeGpio(String inputString) {
		String[] cmdAndData = McuUtils.convertStringToCmdStringAndDataInt(inputString);
		String cmd = cmdAndData[0];
		int data = Integer.parseInt(cmdAndData[1]);

		switch (cmd) {
			case "CGpio1":
				gpioFacade.setGpio(1, data);
				break;
			case "CGpio2":
				gpioFacade.setGpio(2, data);
				break;
			case "CGpio3":
				gpioFacade.setGpio(3, data);
				break;
			case "CGpio4":
				gpioFacade.setGpio(4, data);
				break;
			default:
				return NOT_VALID_EXEC_COMMAND;
		}
		return cmd + ";ACK;";
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (clientConnected && readBroadcastValue(evt)) {
			var mcuBroadcastMessage = (McuBroadcastMessage) evt.getNewValue();
			output.println("RES" + mcuBroadcastMessage.getCommand() + "," + mcuBroadcastMessage.getData() + "," + getCurrentTime() + "," + 0);
			output.flush();
		}
	}

	private String getCurrentTime() {
		return TIME_FORMATTER.format(LocalDateTime.now());
	}

	private boolean readBroadcastValue(PropertyChangeEvent evt) {
		return	evt.getPropertyName().equals(BroadcastEvent.HIT.getValue()) ||
				evt.getPropertyName().equals(BroadcastEvent.STATIC_HIT.getValue()) ||
				evt.getPropertyName().equals(BroadcastEvent.OTHER.getValue());
	}

	private void threadSleep() {
		try {
			clientAlive = false;
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			clientConnected = false;
			clientAlive = false;
			log.debug("Interrupted in the 'heart beat' thread");
			Thread.currentThread().interrupt();
		}
	}

	private void cleanUpSocketAndInOutput(){
		try {
			if (serverSocket != null)
				serverSocket.close();
			if (socket != null)
				socket.close();
			if (input != null)
				input.close();
			if (output != null)
				output.close();
		} catch (IOException e1) {
			e1.getStackTrace();
		}
	}

	private void joinAllThreads() {
		try {
			if(isClientAliveThread!=null){
				isClientAliveThread.join();
			}
			if(sendAndReceiveThread!=null) {
				sendAndReceiveThread.join();
			}
			log.debug("Connection shutdown or restarted");
		} catch (InterruptedException e) {
			log.debug("Fail to restarted connection... threads could not join", e);
			Thread.currentThread().interrupt();
		}
	}
}
