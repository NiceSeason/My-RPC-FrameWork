package io.niceseason.rpc.demo.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.niceseason.rpc.api.HelloObject;

public class ObjectServerHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("服务器接收到对象："+o);
        HelloObject helloObject = (HelloObject) o;
        helloObject.setId(100);
        helloObject.setMessage("after alter message");
        channelHandlerContext.writeAndFlush(helloObject);
    }
}
