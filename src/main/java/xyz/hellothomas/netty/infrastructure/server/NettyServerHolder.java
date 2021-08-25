package xyz.hellothomas.netty.infrastructure.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import xyz.hellothomas.netty.application.MessageService;

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

    private final MessageService messageService;

    public NettyServerHolder(MessageService messageService) {
        this.messageService = messageService;
    }

    private synchronized static void initNettyServer(int port, MessageService messageService) {
        if (nettyServer == null) {
            nettyServer = new NettyServer(port);
            nettyServer.setMessageService(messageService);
            nettyServer.start();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        initNettyServer(port, messageService);
    }

    @PreDestroy
    public void destroy() {
        if (nettyServer != null) {
            nettyServer.shutdown();
        }
    }
}
