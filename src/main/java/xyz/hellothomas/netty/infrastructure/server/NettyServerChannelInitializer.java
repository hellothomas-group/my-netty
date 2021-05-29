package xyz.hellothomas.netty.infrastructure.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import xyz.hellothomas.netty.common.Constants;

import java.util.concurrent.Executor;

/**
 * @author Thomas
 * @date 2021/5/23 11:21
 * @description
 * @version 1.0
 */
public class NettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private Executor executor;

    public NettyServerChannelInitializer() {
    }

    public NettyServerChannelInitializer(Executor executor) {
        this.executor = executor;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //添加编解码
        socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, Constants.SERVER_ALL_IDLE_TIME_SECONDS));
        socketChannel.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast(new NettyServerHandler(executor));
    }
}
