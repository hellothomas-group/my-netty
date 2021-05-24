package xyz.hellothomas.netty.infrastructure.client;

import lombok.SneakyThrows;
import org.junit.Test;

import static org.junit.Assert.*;

public class NettyClientTest {

    @SneakyThrows
    @Test
    public void start() {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8082);
        nettyClient.start();

//        Thread.sleep(2000);
        nettyClient.sendMsg("你好啊");
//        Thread.sleep(2000);
//        nettyClient.shutdown();

        Thread.sleep(100000);
    }

    @Test
    public void start2() {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8090);
        nettyClient.start();
    }

    @Test
    public void start3() {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8090);
        nettyClient.start();
    }
}