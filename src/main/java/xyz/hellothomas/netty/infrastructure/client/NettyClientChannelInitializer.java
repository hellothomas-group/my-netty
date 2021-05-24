package xyz.hellothomas.netty.infrastructure.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import xyz.hellothomas.netty.common.Constants;

/**
 * @author Thomas
 * @date 2021/5/23 11:32
 * @description
 * @version 1.0
 */
public class NettyClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private NettyClient nettyClient;

    public NettyClientChannelInitializer(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new IdleStateHandler(Constants.CLIENT_READER_IDLE_TIME_SECONDS, 0, 0));
        socketChannel.pipeline().addLast("decoder", new StringDecoder());
        socketChannel.pipeline().addLast("encoder", new StringEncoder());
        socketChannel.pipeline().addLast(new NettyClientHandler(nettyClient));
    }
}
