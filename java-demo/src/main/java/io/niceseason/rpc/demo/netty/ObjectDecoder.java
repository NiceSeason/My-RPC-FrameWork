package io.niceseason.rpc.demo.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.niceseason.rpc.api.HelloObject;

import java.util.List;

public class ObjectDecoder extends ReplayingDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("Decoder正在工作");

        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        ObjectMapper mapper = new ObjectMapper();
        HelloObject object = mapper.readValue(bytes, HelloObject.class);
        list.add(object);
    }
}
