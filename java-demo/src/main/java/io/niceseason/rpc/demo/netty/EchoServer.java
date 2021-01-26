package io.niceseason.rpc.demo.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            //
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new EchoServerHandler())
                    //服务端接受连接的队列长度
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
                    // 默认的心跳间隔是7200s即2小时。
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(7).sync();
            System.out.println("服务器已启动，端口："+7);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
