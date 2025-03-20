package com.shousi.shousiinterface.controller;

import com.shousi.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getName(String name){
        return "GET 你的名字" + name;
    }

    @PostMapping("/post")
    public String postName(@RequestParam String name){
        return "POST 你的名字" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){
        return "发送POST请求 JSON中你的名字是：" + user.getName();
    }
}
