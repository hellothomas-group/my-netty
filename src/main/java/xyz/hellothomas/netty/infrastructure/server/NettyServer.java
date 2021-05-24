package xyz.hellothomas.netty.infrastructure.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.InetSocketAddress;

/**
 * @author Thomas
 * @date 2021/5/23 11:20
 * @description
 * @version 1.0
 */
@Slf4j
public class NettyServer {
    /**
     * boss 线程组用于处理连接工作
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    /**
     * work 线程组用于数据处理
     */
    private EventLoopGroup workGroup = new NioEventLoopGroup(200);

    /**
     * channel 服务端
     */
    private Channel serverChannel;

    /**
     * 端口
     */
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyServerChannelInitializer())
                .localAddress(new InetSocketAddress(port))
                //设置队列大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
        //绑定端口,开始接收进来的连接
        try {
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                log.info("服务器启动开始监听端口: {}", this.port);
                serverChannel = future.channel();
            } else {
                log.info("服务器启动失败监听端口: {}", this.port);
            }
        } catch (InterruptedException e) {
            log.error("服务器启动异常为:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    public void shutdown() {
        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
            log.info("NettyServer已关闭");
        } catch (InterruptedException e) {
            log.error("服务器关闭异常为:{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
