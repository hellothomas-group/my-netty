package xyz.hellothomas.netty.infrastructure.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author Thomas
 * @date 2021/5/23 16:10
 * @description
 * @version 1.0
 */
@Component
public class NettyServerHolder implements ApplicationListener<ApplicationStartedEvent> {
    private static volatile NettyServer nettyServer;

    @Value("${my-netty.port}")
    private int port;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        initNettyServer(port);
    }

    private synchronized static void initNettyServer(int port) {
        if (nettyServer == null) {
            nettyServer = new NettyServer(port);
            nettyServer.start();
        }
    }

    @PreDestroy
    public void destroy() {
        if (nettyServer != null) {
            nettyServer.shutdown();
        }
    }
}
