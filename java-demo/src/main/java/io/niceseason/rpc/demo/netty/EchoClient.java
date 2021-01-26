package io.niceseason.rpc.demo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class EchoClient {
    public static void main(String[] args) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new EchoClientHandler());
            ChannelFuture future = b.connect("127.0.0.1", 7).sync();
            Channel channel = future.channel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(32);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String std;
                while ((std = reader.readLine()) != null) {
                    byteBuffer.put(std.getBytes());
                    byteBuffer.flip();
//                    byteBuffer.rewind();

//                    ByteBuf buf = Unpooled.copiedBuffer(byteBuffer);
                    ByteBuf buf = Unpooled.copiedBuffer(std.getBytes());
                    channel.writeAndFlush(buf);
                    byteBuffer.clear();
                }
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
        }
    }
}
