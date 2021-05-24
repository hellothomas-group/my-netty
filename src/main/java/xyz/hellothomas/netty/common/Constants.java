package xyz.hellothomas.netty.common;

/**
 * @author 80234613 唐圆
 * @date 2021/5/24 15:02
 * @descripton
 * @version 1.0
 */
public class Constants {
    public static final int SERVER_ALL_IDLE_TIME_SECONDS = 200;
    public static final int CLIENT_READER_IDLE_TIME_SECONDS = 60;
    public static final int CLIENT_RECONNECT_DELAY_TIME_SECONDS = 10;
    public static final int CLIENT_HEARTBEAT_FAILED_MAX_TIMES = 3;

    private Constants() {
        throw new IllegalStateException("Constant Class");
    }
}
