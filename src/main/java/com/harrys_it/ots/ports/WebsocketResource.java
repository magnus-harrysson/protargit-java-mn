/*package com.harrys_it.ots.ports;

import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.ports.utils.BluetoothAndWebsocketProtocol;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;

@Singleton
public class WebsocketResource extends StompSessionHandlerAdapter implements PropertyChangeListener {

    private final BroadcasterService broadcasterService;
    private final BluetoothAndWebsocketProtocol protocol;

    private final StompSessionHandler sessionHandler;
    private final WebSocketStompClient stompClient;

    private final String url;

    private StompSession stompSession;
    private Thread webSocketThread = null;
    private boolean busy = false;
    private boolean isConnected = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketResource.class);
    private static final String ENDPOINT_SEND = "/app/websocket/slaveIn";
    private static final String ENDPOINT_RECEIVE = "/topic/websocket/slaveOut";

    public WebsocketResource(BroadcasterService broadcasterService,
                             BluetoothAndWebsocketProtocol protocol,
                             @Value("${websocket.url:ws://localhost:8080/proxy}") String url) {
        this.broadcasterService = broadcasterService;
        this.protocol = protocol;
        this.url = url;

        this.sessionHandler = this;
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    }

    @Value("${start.websocket}")
    private void run(boolean startWebsocket) {
        LOGGER.debug("{}", startWebsocket);
        if(startWebsocket) {
            broadcasterService.addPropertyChangeListener(this); // Listen to hits
            LOGGER.debug("Try to connect to: {}", url);
            startWebsocketThread();
        }
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        new Thread(() -> {
            var in = (byte[]) payload;

            var response = protocol.handleDataFromMaster(in, this.getClass().getName());
            if(response.length > 0) {
                sendToMaster(response);
            }
        }).start();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var res = protocol.propChanged(evt);
        if(res.length > 0) {
            sendToMaster(res);
        }
    }

    private void sendToMaster(byte[] data) {
        if (stompSession != null && isConnected && !busy) {
            busy = true;

            stompSession.send(ENDPOINT_SEND, data);
            busy = false;
        }
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        stompSession = session;
        session.subscribe(ENDPOINT_RECEIVE, this);
        isConnected = true;
        LOGGER.debug("New session established : {}", session.getSessionId());
        this.propertyChange(new PropertyChangeEvent(BroadcasterService.class, BroadcastEvent.UPDATE_TARGET_STATUS.getValue(), "old-update", "new-update"));
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        LOGGER.debug("Couldn't connect to Proxy websocket. Retrying in 5 sec (session: {})", session.getSessionId());
        isConnected = false;
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        LOGGER.debug("Got an exception in handleException()", exception);
    }

    private void startWebsocketThread() {
        webSocketThread = new Thread(this::tryToConnect);
        webSocketThread.start();
    }

    private void tryToConnect() {
        stompClient.setMessageConverter(new ByteArrayMessageConverter());
        stompClient.connect(url, sessionHandler);

        new Thread(() -> {
            while(true){
                threadSleep();
                if(!isConnected) {
                    stopThread();
                    break;
                }
            }
        }).start();
    }

    private void threadSleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.debug("Thread interrupt ");
            Thread.currentThread().interrupt();
        }
    }

    private void stopThread() {
        if(webSocketThread!=null){
            webSocketThread.interrupt();
            try { webSocketThread.join(); }
            catch (InterruptedException e) {
                LOGGER.debug("stop running thread: ", e);
                Thread.currentThread().interrupt();
            }
        }
        startWebsocketThread();
    }
}
*/
