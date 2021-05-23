package xyz.hellothomas.netty.infrastructure.client;

import org.junit.Test;

import static org.junit.Assert.*;

public class NettyClientTest {

    @Test
    public void start() {
        NettyClient nettyClient = new NettyClient();
        nettyClient.start();
    }
}