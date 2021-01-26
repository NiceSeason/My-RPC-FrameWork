package io.niceseason.rpc.demo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.niceseason.rpc.api.HelloObject;



public class ObjectClient {
    public static void main(String[] args) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline
                                    .addLast(new ObjectEncoder())
                                    .addLast(new ObjectDecoder())
                                    .addLast(new ObjectClientHandler());
                        }
                    });
            ChannelFuture future = b.connect("127.0.0.1", 7).sync();
            Channel channel = future.channel();
            HelloObject helloObject = new HelloObject(23, "this is a test message");
            channel.writeAndFlush(helloObject);
            //等待任务执行完成
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
        }
    }
}
