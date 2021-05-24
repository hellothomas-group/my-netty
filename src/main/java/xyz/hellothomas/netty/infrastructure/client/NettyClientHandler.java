package xyz.hellothomas.netty.infrastructure.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import xyz.hellothomas.netty.common.Constants;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas
 * @date 2021/5/23 11:32
 * @description
 * @version 1.0
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private NettyClient nettyClient;
    private AtomicInteger heartBeatFailedTimes = new AtomicInteger();

    public NettyClientHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("客户端Active ..... id:{}, server ip:{}", ctx.channel().id(), inetSocketAddress.getAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("客户端收到消息: {}", msg.toString());
        heartBeatFailedTimes.set(0);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //如果运行过程中服务端挂了,执行重连机制
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("客户端Inactive ..... id:{}, server ip:{}, 尝试重新连接", ctx.channel().id(), inetSocketAddress.getAddress());
        ctx.channel().eventLoop().schedule(() -> nettyClient.start(), Constants.CLIENT_RECONNECT_DELAY_TIME_SECONDS,
                TimeUnit.SECONDS);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            sendHeartBeat(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常为:{}", ExceptionUtils.getStackTrace(cause));
        ctx.close();
    }

    private void sendHeartBeat(ChannelHandlerContext ctx) {
        //发送心跳消息，并在连续发送失败时关闭该连接
        ctx.writeAndFlush("heartbeat").addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                heartBeatFailedTimes.set(0);
            } else {
                if (heartBeatFailedTimes.incrementAndGet() >= Constants.CLIENT_HEARTBEAT_FAILED_MAX_TIMES) {
                    log.info("连续{}次心跳无返回,关闭该连接", Constants.CLIENT_HEARTBEAT_FAILED_MAX_TIMES);
                    ctx.close();
                }
            }
        });
    }
}
