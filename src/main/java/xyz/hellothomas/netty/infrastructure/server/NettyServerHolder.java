package xyz.hellothomas.netty.infrastructure.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executor;

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

    private synchronized static void initNettyServer(int port, Executor executor) {
        if (nettyServer == null) {
            nettyServer = new NettyServer(port);
            nettyServer.setExecutor(executor);
            nettyServer.start();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        Executor executor =
                applicationStartedEvent.getApplicationContext().getBean(Executor.class);
        initNettyServer(port, executor);
    }

    @PreDestroy
    public void destroy() {
        if (nettyServer != null) {
            nettyServer.shutdown();
        }
    }
}
