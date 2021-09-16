package xyz.hellothomas.netty.application;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.hellothomas.jedi.client.annotation.JediAsync;

/**
 * @author 80234613 唐圆
 * @date 2021/8/25 9:14
 * @descripton
 * @version 1.0
 */
@Slf4j
@Service
public class MessageService {
    @JediAsync
    public void run(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        log.info("业务异步处理...");
        Thread.sleep(1000);
        log.info("服务器处理完成");
        ctx.writeAndFlush("你也好哦").addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("服务器返回成功");
            } else {
                log.error("服务器返回失败", channelFuture.cause());
            }
        });
    }
}
