package io.niceseason.rpc.core.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.core.RpcClient;
import io.niceseason.rpc.core.codec.CommonDecoder;
import io.niceseason.rpc.core.codec.CommonEncoder;
import io.niceseason.rpc.core.serializer.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Handler;

public class NettyClient implements RpcClient {

    private String host;
    private int port;

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private static final Bootstrap bootstrap;

    static {
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new JsonSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });

    }

    @Override
    public Object sendRequest(RpcRequest request) {
        try{
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            if (channel != null) {
                ChannelFuture future1 = channel.writeAndFlush(request);
                future1.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess())
                            logger.info("客户端发送消息:{}",request.toString());
                        else
                            logger.error("发送消息时有错误产生:{}", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse response = channel.attr(key).get();
                return response;
            }
        } catch (InterruptedException e) {
            logger.error("客户端连接时发生错误:{}",e.getMessage());
        }
        return null;
    }
}
