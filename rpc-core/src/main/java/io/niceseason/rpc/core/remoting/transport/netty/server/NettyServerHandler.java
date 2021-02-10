package io.niceseason.rpc.core.remoting.transport.netty.server;

import io.netty.channel.*;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.factory.SingletonFactory;
import io.niceseason.rpc.core.remoting.handler.RequestHandler;
import io.niceseason.rpc.core.provider.DefaultServiceProvider;
import io.niceseason.rpc.core.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {


    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);



    private  ServiceProvider serviceProvider;

    private  RequestHandler requestHandler;


    public NettyServerHandler() {
        this.serviceProvider = new DefaultServiceProvider();
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.info("[{}]长时间未接受到心跳包，服务断开", DateFormatter.format(new Date()));
                ctx.channel().close().sync();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg){
        Channel channel = ctx.channel();
        if (msg.isHeart()) {
            logger.info("[{}]收到心跳包,来自客户端:{}", DateFormatter.format(new Date()), channel.remoteAddress());
            return;
        }
        try{
            logger.info("服务端接收到请求{}",msg);
            Object service = serviceProvider.getProvider(msg.getInterfaceName());
            Object returnObject = requestHandler.handler(msg, service);
            ChannelFuture channelFuture = channel.writeAndFlush(RpcResponse.success(msg.getRequestId(),returnObject));
            channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
