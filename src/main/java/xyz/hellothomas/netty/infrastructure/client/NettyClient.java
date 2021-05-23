package xyz.hellothomas.netty.infrastructure.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author Thomas
 * @date 2021/5/23 11:31
 * @description
 * @version 1.0
 */
@Slf4j
public class NettyClient {
    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientChannelInitializer());

        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8090).sync();
            log.info("客户端成功....");
            //发送消息
            future.channel().writeAndFlush("你好啊");
            // 等待连接被关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("异常为:{}", ExceptionUtils.getStackTrace(e));
        } finally {
            group.shutdownGracefully();
        }
    }
}
