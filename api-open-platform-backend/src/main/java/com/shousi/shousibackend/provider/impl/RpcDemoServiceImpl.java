package com.shousi.shousibackend.provider.impl;

import org.apache.dubbo.springboot.demo.DemoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

@DubboService
public class RpcDemoServiceImpl implements DemoService {
	@Override
	public String sayHello(String name) {
		System.out.println("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
		return "Hello " + name;
	}
}