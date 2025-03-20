package com.shousi;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.springboot.demo.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShousiGatewayApplicationTests {

    @DubboReference
    private DemoService demoService;

    @Test
    void testRpc() {
        System.out.println(demoService.sayHello("world"));
    }

}
