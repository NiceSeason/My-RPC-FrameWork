package io.niceseason.rpc.core.remoting.transport.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.factory.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                Channel channel = ctx.channel();
                logger.info("[{}]准备向服务端心跳包服务端地址:{}", DateFormatter.format(new Date()), channel.remoteAddress());
                sendHeart(channel);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void sendHeart(Channel channel) {
        RpcRequest request = new RpcRequest();
        request.setHeart(true);
        channel.writeAndFlush(request);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try{
            logger.info("客户端收到返回信息:{}",msg);
            unprocessedRequests.complete(msg);
        }finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("NettyClientHandler处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
