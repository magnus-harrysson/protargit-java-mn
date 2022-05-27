package com.harrys_it.ots.ports;

import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.mcu.McuBroadcastMessage;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.facade.GpioFacade;
import com.harrys_it.ots.facade.OsFacade;
import com.harrys_it.ots.facade.PcbFacade;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@MicronautTest
class SocketResourceTest {
    private static SocketResource socketResource;
    private static SocketClient socketClient;
    private static final int PORT = 51234;

    @BeforeAll
    static void setup() throws IOException {
        socketClient = new SocketClient();
        new Thread(() -> {
            socketResource = new SocketResource(mock(BroadcasterService.class), mock(PcbFacade.class), mock(OsFacade.class), mock(GpioFacade.class), true, 51111);
            try {
                socketResource.connectToSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        await().until(() -> socketResource != null && socketResource.isWaitingForConnection());
        socketClient.startConnection("127.0.0.1", PORT);
    }

    @Test
    void sendAudio_mp3File() {
        sendCommandAndReadResponse("CAudio,myAudioFile.mp3;", "RESCAudio,CAudio,myAudioFile.mp3;ACK;");
    }

    @Test
    void sendAudio_wavFile() {
        sendCommandAndReadResponse("CAudio,myAudioFile.wav;", "RESCAudio,CAudio,myAudioFile.wav;ACK;");
    }

    @Test
    void sendAudio_incorrectFile() {
        sendCommandAndReadResponse("CAudio,myAudioFile.flac;", "RESCAudio,CAudio,myAudioFile.flac;NACK;");
    }

    @Test
    void sendMcuEvent_FlipMotorSetAngle() {
        var mcuEvent = new McuEvent(McuCommand.FLIP_ANGLE,80);
        when(socketResource.sendToMcu(mcuEvent)).thenReturn(1);
        sendCommandAndReadResponse("C6,D80;", "RES6,ACK;");
    }

    @Test
    void sendMcuEvent_FlipMotorCurrentAngle() {
        var mcuEvent = new McuEvent(McuCommand.FLIP_CURRENT_ANGLE, McuDataLimit.ANY_DATA.getValue());
        when(socketResource.sendToMcu(mcuEvent)).thenReturn(110);
        sendCommandAndReadResponse("C7,D0;", "RES7,110;");
    }

    @Test
    void sendOs_Reboot() {
        sendCommandAndReadResponse("CReboot;", "RESOs,CReboot;ACK;");
    }

    @Test
    void sendOs_Shutdown() {
        sendCommandAndReadResponse("CShutdown;", "RESOs,CShutdown;ACK;");
    }

    @Test
    void sendOs_WifiOn() {
        sendCommandAndReadResponse("CWifion;", "RESOs,CWifion;ACK;");
    }

    @Test
    void sendOs_WifiOff() {
        sendCommandAndReadResponse("CWifioff;", "RESOs,CWifioff;ACK;");
    }

    @Test
    void sendGpio4_High300MsCommand() {
        sendCommandAndReadResponse("CGpio4,300;", "RESCGpio,CGpio4;ACK;");
    }

    @Test
    void sendGpio3_HighCommand() {
        sendCommandAndReadResponse("CGpio3,1;", "RESCGpio,CGpio3;ACK;");
    }

    @Test
    void sendGpio2_LowCommand() {
        sendCommandAndReadResponse("CGpio2,0;", "RESCGpio,CGpio2;ACK;");
    }

    @Test
    void sendGpio1_Low300MsCommand() {
        sendCommandAndReadResponse("CGpio1,-300;", "RESCGpio,CGpio1;ACK;");
    }

    @Test
    void send_Alive() throws InterruptedException {
        assertFalse(socketResource.isClientAlive());
        socketResource.incomingCommand("CAlive;");
        await().atLeast(100, TimeUnit.MILLISECONDS).until(() -> true);
        assertTrue(socketResource.isClientAlive());
    }

    @Test
    void propertyChanged_hit() throws IOException {
        socketResource.propertyChange(new PropertyChangeEvent("any source", BroadcastEvent.HIT.getValue(), null,new McuBroadcastMessage(2,1)));
        await().atLeast(100, TimeUnit.MILLISECONDS).until(() -> true);

        var data = socketClient.getIn().readLine();

        var expectedStart = "RES" + 2 + "," + 1;
        var expectedEnd = ",0";
        assertEquals(expectedStart, data.substring(0,6));
        assertThat(data.substring(7,19)).isNotBlank();
        assertEquals(expectedEnd, data.substring(data.length()-2));
    }

    @Test
    void propertyChanged_static() throws IOException {
        socketResource.propertyChange(new PropertyChangeEvent("any source", BroadcastEvent.STATIC_HIT.getValue(), null,new McuBroadcastMessage(2,1)));
        await().atLeast(100, TimeUnit.MILLISECONDS).until(() -> true);

        var data = socketClient.getIn().readLine();

        var expectedStart = "RES" + 2 + "," + 1;
        var expectedEnd = ",0";
        assertEquals(expectedStart, data.substring(0,6));
        assertThat(data.substring(7,19)).isNotBlank();
        assertEquals(expectedEnd, data.substring(data.length()-2));
    }

    @Test
    void propertyChanged_other() throws IOException {
        socketResource.propertyChange(new PropertyChangeEvent("any source", BroadcastEvent.OTHER.getValue(), null,new McuBroadcastMessage(2,1)));
        await().atLeast(100, TimeUnit.MILLISECONDS).until(() -> true);

        var data = socketClient.getIn().readLine();

        var expectedStart = "RES" + 2 + "," + 1;
        var expectedEnd = ",0";
        assertEquals(expectedStart, data.substring(0,6));
        assertThat(data.substring(7,19)).isNotBlank();
        assertEquals(expectedEnd, data.substring(data.length()-2));
    }

    @DisplayName("sendOS_Exit. Closes the connection. Must be tested last")
    @AfterAll
    static void sendOs_Exit() throws InterruptedException, IOException {
        assertTrue(socketResource.isClientConnected());
        socketResource.incomingCommand("CExit;");
        assertFalse(socketResource.isClientConnected());
        socketClient.closeConnection();
    }

    private void sendCommandAndReadResponse(String request, String expectedResponse) {
        try {
            socketResource.incomingCommand(request);
            await().atLeast(100, TimeUnit.MILLISECONDS).until(() -> true);
            var data = socketClient.getIn().readLine();
            assertEquals(expectedResponse, data);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static class SocketClient {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public void startConnection(String ip, int port) throws IOException {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public void closeConnection() throws IOException {
            if (in != null) {
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(clientSocket != null) {
                clientSocket.close();
            }
        }

        public BufferedReader getIn() {
            return in;
        }
    }
}
