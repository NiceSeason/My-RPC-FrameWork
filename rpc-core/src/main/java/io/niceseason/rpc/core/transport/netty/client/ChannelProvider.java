package io.niceseason.rpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.niceseason.rpc.common.enumeration.RpcError;
import io.niceseason.rpc.common.exception.RpcException;
import io.niceseason.rpc.core.codec.CommonDecoder;
import io.niceseason.rpc.core.codec.CommonEncoder;
import io.niceseason.rpc.core.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static EventLoopGroup eventLoopGroup;

    private static final int MAX_RETRY_COUNT = 5;

    private static Channel channel = null;

    private static Bootstrap bootstrap=initBootStrap();


    public static Channel getChannel(InetSocketAddress address, CommonSerializer commonSerializer) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new CommonEncoder(commonSerializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap, address,countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取channel时有错误产生：", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress address, CountDownLatch countDownLatch) {
        connect(bootstrap, address, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress address, int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(address).addListener((ChannelFutureListener)future -> {
            if (future.isSuccess()) {
                logger.info("netty客服端连接成功");
                channel = future.channel();
                countDownLatch.countDown();
            }

            if (retry == 0) {
                logger.error("客户端连接失败:重试次数已用完，放弃连接！");
                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }

            // 第几次重连
            int order = (MAX_RETRY_COUNT - retry) + 1;
            // 本次重连的间隔
            int delay = 1 << order;
            logger.error("{}: 连接失败，第 {} 次重连……", new Date(), order);
            bootstrap.config().group().schedule(() -> connect(bootstrap, address, retry - 1, countDownLatch), delay, TimeUnit.SECONDS);
        });
    }

    private static Bootstrap initBootStrap() {
        Bootstrap bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                //开启Nagle算法，TCP尽可能发送大包
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }


}
