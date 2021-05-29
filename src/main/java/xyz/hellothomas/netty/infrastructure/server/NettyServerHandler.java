package xyz.hellothomas.netty.infrastructure.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import xyz.hellothomas.netty.common.Constants;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author Thomas
 * @date 2021/5/23 11:21
 * @description
 * @version 1.0
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>();

    private Executor executor;

    public NettyServerHandler() {
    }

    public NettyServerHandler(Executor executor) {
        this.executor = executor;
    }

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
        if (executor != null) {
            executor.execute(() -> {
                log.info("业务异步处理...");
                run(ctx);
            });
        } else {
            run(ctx);
        }
        log.info("channelRead exit");
    }

    @SneakyThrows
    private void run(ChannelHandlerContext ctx) {
        Thread.sleep(10000);
        log.info("服务器处理完成");
        ctx.writeAndFlush("你也好哦").addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("服务器返回成功");
            } else {
                log.error("服务器返回失败:{}", channelFuture.cause());
            }
        });
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
            log.info("channel:{}读写超时{}s,此时连接通道数量为:{}", ctx.channel().id(), Constants.SERVER_ALL_IDLE_TIME_SECONDS,
                    channelMap.size());
            ctx.channel().close();
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
