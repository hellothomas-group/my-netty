package xyz.hellothomas.netty.infrastructure.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import xyz.hellothomas.netty.common.Constants;

import java.util.concurrent.TimeUnit;

/**
 * @author Thomas
 * @date 2021/5/23 11:31
 * @description
 * @version 1.0
 */
@Slf4j
public class NettyClient {
    private String host;
    private int port;
    private EventLoopGroup group = new NioEventLoopGroup();
    private volatile Channel clientChannel;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .option(ChannelOption.SO_KEEPALIVE, true)
                //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyClientChannelInitializer(this));

        ChannelFuture future = bootstrap.connect();
        //客户端断线重连逻辑
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("连接Netty服务端成功");
                clientChannel = future.channel();
            } else {
                log.info("连接失败，进行断线重连");
                channelFuture.channel().eventLoop().schedule(() -> start(),
                        Constants.CLIENT_RECONNECT_DELAY_TIME_SECONDS, TimeUnit.SECONDS);
            }
        });
    }

    public void sendMsg(String message) {
        if (clientChannel != null) {
            clientChannel.writeAndFlush(message);
        } else {
            log.info("client channel is not ready");
        }
    }

    public void shutdown() {
        try {
            if (clientChannel != null) {
                clientChannel.close().sync();
            }
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error("客户端关闭异常为:{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
