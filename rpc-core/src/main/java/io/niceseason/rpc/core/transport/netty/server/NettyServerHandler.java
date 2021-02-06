package io.niceseason.rpc.core.transport.netty.server;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.util.ThreadPoolFactoryUtils;
import io.niceseason.rpc.core.RequestHandler;
import io.niceseason.rpc.core.provider.DefaultServiceProvider;
import io.niceseason.rpc.core.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {


    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static final  String THREAD_POOL_PREFIX = "netty-server-handler";

    private final ExecutorService executorService;

    private  ServiceProvider serviceProvider;

    private  RequestHandler requestHandler;

    public NettyServerHandler() {
        this.executorService = ThreadPoolFactoryUtils.createDefaultThreadPoolIfAbsent(THREAD_POOL_PREFIX);
        this.serviceProvider = new DefaultServiceProvider();
        this.requestHandler = new RequestHandler();
    }




    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg){
        executorService.execute(()->{
            try{
                logger.info("服务端接收到请求{}",msg);
                Object service = serviceProvider.getProvider(msg.getInterfaceName());
                Object returnObject = requestHandler.handler(msg, service);
                Channel channel = ctx.channel();
                ChannelFuture channelFuture = channel.writeAndFlush(RpcResponse.success(msg.getRequestId(),returnObject));
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }finally {
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
