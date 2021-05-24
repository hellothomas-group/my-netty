package xyz.hellothomas.netty.infrastructure.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas
 * @date 2021/5/23 11:21
 * @description
 * @version 1.0
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>();

    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active......");
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        ChannelId channelId = ctx.channel().id();
        if (!channelMap.containsKey(channelId)) {
            channelMap.put(channelId, ctx);
            log.info("客户端id:{} ip:{} port:{} 连接netty服务器,连接通道数量为:{}", channelId, inetSocketAddress.getAddress(),
                    inetSocketAddress.getPort(), channelMap.size());
        }
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务器收到消息: {}", msg.toString());
        ctx.write("你也好哦");
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel inactive......");
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        ChannelId channelId = ctx.channel().id();
        if (channelMap.containsKey(channelId)) {
            channelMap.remove(channelId);
            log.info("客户端id:{} ip:{} port:{} 退出netty服务器,连接通道数量为:{}", channelId, inetSocketAddress.getAddress(),
                    inetSocketAddress.getPort(), channelMap.size());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            log.info("channel:{}读写超时,此时连接通道数量为:{}", ctx.channel().id(), channelMap.size());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel:{}发生错误,此时连接通道数量为:{},异常为:{}", ctx.channel().id(),
                channelMap.size(), ExceptionUtils.getStackTrace(cause));
        ctx.close();
    }
}
