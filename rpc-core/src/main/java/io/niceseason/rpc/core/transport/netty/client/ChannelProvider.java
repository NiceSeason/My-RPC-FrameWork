package io.niceseason.rpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.niceseason.rpc.common.enumeration.RpcError;
import io.niceseason.rpc.common.exception.RpcException;
import io.niceseason.rpc.core.codec.CommonDecoder;
import io.niceseason.rpc.core.codec.CommonEncoder;
import io.niceseason.rpc.core.serializer.CommonSerializer;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private static final int MAX_RETRY_COUNT = 5;

    public static Channel getChannel(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {
        String key = inetSocketAddress.toString() + serializer.getCode();
        Channel ch = CHANNEL_MAP.get(key);
        if (ch!=null&&ch.isActive()) return ch;
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                ch.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CompletableFuture<Channel> channelCompletableFuture = new CompletableFuture<>();
        connect(bootstrap, inetSocketAddress,channelCompletableFuture);
        try {
            Channel channel = channelCompletableFuture.get();
            CHANNEL_MAP.put(key, channel);
            return channel;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("获取channel时有错误产生{}", e.getMessage());
            throw new RpcException(RpcError.FAILED_TO_GET_CHANNEL);
        }
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CompletableFuture<Channel> channelCompletableFuture) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT,channelCompletableFuture);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CompletableFuture<Channel> channelCompletableFuture) {
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                Channel channel = future.channel();
                channelCompletableFuture.complete(channel);
                return;
            }
            if (retry == 0) {
                logger.error("客户端连接失败:重试次数已用完，放弃连接！");
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            // 第几次重连
            int order = (MAX_RETRY_COUNT - retry) + 1;
            // 本次重连的间隔
            int delay = 1 << order;
            logger.error("{}: 连接失败，第 {} 次重连……", new Date(), order);
            bootstrap.config().group().schedule(() -> connect(bootstrap, inetSocketAddress, retry - 1, channelCompletableFuture), delay, TimeUnit
                    .SECONDS);
        });
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

}

