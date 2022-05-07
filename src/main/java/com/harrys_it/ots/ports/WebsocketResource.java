package com.harrys_it.ots.ports;

import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.ports.utils.BluetoothAndWebsocketKonverter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

import static com.harrys_it.ots.ports.utils.LogBuilderBluetoothAndWebsocket.buildLogIn;

public class WebsocketResource extends WebSocketClient implements PropertyChangeListener {

    private boolean connected = false;

    private final BluetoothAndWebsocketKonverter protocol;

    private static final Logger log = LoggerFactory.getLogger(WebsocketResource.class);

    public WebsocketResource(URI uri, BroadcasterService broadcasterService, BluetoothAndWebsocketKonverter protocol) {
        super(uri);
        this.protocol = protocol;
        broadcasterService.addPropertyChangeListener(this); // Listen to hits
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var response = protocol.propChanged(evt);
        sendToProxy(response);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        connected = true;
        log.debug("Connect to proxy server");
        this.propertyChange(new PropertyChangeEvent(BroadcasterService.class, BroadcastEvent.UPDATE_TARGET_STATUS.getValue(), "old-update", "new-update"));
    }

    @Override
    public void onMessage(String message) {
        log.debug("received: {}", message);
        var response = protocol.handleDataFromMaster(message.getBytes());
        sendToProxy(response);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        connected = false;
        log.debug("code = {} reason = {} remote = {}", code, reason , remote);
    }

    private void sendToProxy(byte[] response) {
        try {
            if(response.length > 0) {
                send(response);
                if(log.isDebugEnabled()) {
                    log.debug(buildLogIn(response, this.getClass().getName()));
                }
            }
        } catch (WebsocketNotConnectedException e) {
            log.debug("Lost connection to proxy server.");
        }
    }

    @Override
    public void onError(Exception ex) {
        /* Do nothing */
    }
}

