package xyz.hellothomas.netty.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Thomas
 * @date 2021/5/23 11:10
 * @description
 * @version 1.0
 */
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello(String name) {
        return "hello " + name;
    }
}
