package com.harrys_it.ots.ports;

import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.ports.utils.BluetoothAndWebsocketKonverter;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public class WebsocketResourceStarter {

    private final BroadcasterService broadcasterService;
    private final BluetoothAndWebsocketKonverter protocol;
    private static final Logger log = LoggerFactory.getLogger(WebsocketResourceStarter.class);

    public WebsocketResourceStarter(BroadcasterService broadcasterService,
                                    BluetoothAndWebsocketKonverter protocol,
                                    @Value("${websocket.enable}") boolean startService,
                                    @Value("${websocket.url}") String url,
                                    @Value("${websocket.mock}") boolean mockData) throws URISyntaxException {
        this.broadcasterService = broadcasterService;
        this.protocol = protocol;
        if(startService) {
            log.debug("Started");
            run(new URI(url));
        }
        if(mockData) {
            mockData();
        }
    }

    private void run(URI uri) {
        WebsocketResource websocketResource = new WebsocketResource(uri, broadcasterService, protocol);
        websocketResource.connect();
        reconnectIfDown(websocketResource, uri);
    }

    private void reconnectIfDown(WebsocketResource websocketResource, URI uri) {
        new Thread(() -> {
            while(true) {
                sleep();
                if(!websocketResource.isConnected()) {
                    run(uri);
                    break;
                }
            }
        }).start();
    }

    private void mockData() {
        new Thread(() -> {
            while(true) {
                sleep();
                broadcasterService.updateValues();
            }
        }).start();
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

