package com.shousi.shousibackend.service.impl;

import com.shousi.shousibackend.service.UserInterfaceInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserInterfaceInfoServiceImplTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    void invokeCount() {
        boolean result = userInterfaceInfoService.invokeCount(1L, 1L);
        assertTrue(result);
    }
}