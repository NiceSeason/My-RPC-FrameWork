package io.niceseason.rpc.core.transport.netty.server;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.core.RequestHandler;
import io.niceseason.rpc.core.provider.DefaultServiceProvider;
import io.niceseason.rpc.core.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {


    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);


    private static ServiceProvider serviceProvider;

    private static RequestHandler requestHandler;

    static {
        serviceProvider = new DefaultServiceProvider();
        requestHandler = new RequestHandler();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try{
            logger.info("服务端接收到请求{}",msg);
            Object service = serviceProvider.getProvider(msg.getInterfaceName());
            Object returnObject = requestHandler.handler(msg, service);
            Channel channel = ctx.channel();
            ChannelFuture channelFuture = channel.writeAndFlush(RpcResponse.success(returnObject));
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
