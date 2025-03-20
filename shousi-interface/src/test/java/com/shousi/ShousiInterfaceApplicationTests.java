package com.shousi;

import com.shousi.clinet.ShouSiApiClient;
import com.shousi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ShousiInterfaceApplicationTests {

    @Autowired
    private ShouSiApiClient shouSiApiClient;

    @Test
    void testApiClient() {
        User user = new User();
        user.setName("寿司");
//        String result1 = shouSiApiClient.getUserNameByPost(user);
//        System.out.println(result1);
    }

}
