package xyz.hellothomas.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.hellothomas.netty.infrastructure.server.NettyServer;

import java.net.InetSocketAddress;

/**
 * @author Thomas
 * @date 2021/5/23 10:54
 * @description
 * @version 1.0
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //启动服务端
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(new InetSocketAddress("127.0.0.1", 8090));
    }
}
