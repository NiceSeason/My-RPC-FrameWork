package io.niceseason.rpc.core.transport.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.factory.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try{
            logger.info("客户端收到返回信息:{}",msg);
            unprocessedRequests.Complete(msg);
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
