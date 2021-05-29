package xyz.hellothomas.netty.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.hellothomas.jedi.core.utils.SleepUtil;

import java.util.concurrent.Callable;

/**
 * @author Thomas
 * @date 2021/5/23 11:10
 * @description
 * @version 1.0
 */
@Slf4j
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello(String name) {
        return "hello " + name;
    }

    @GetMapping("/sync-callable")
    public Callable syncCallabel() {
        return () -> {
            log.info("{}>>>开始处理...", Thread.currentThread());
            SleepUtil.sleep(10000);
            log.info("{}>>>处理完成...", Thread.currentThread());
            return "someView";
        };
    }
}
