package io.niceseason.rpc.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.enumeration.PackageType;
import io.niceseason.rpc.core.serializer.CommonSerializer;

public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER=0xCAFEBABE;

    private CommonSerializer commonSerializer;

    public CommonEncoder(CommonSerializer serializer) {
        commonSerializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //1. 魔数
        out.writeInt(MAGIC_NUMBER);

        //2. 包类型
        if (msg instanceof RpcRequest)
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        else
            out.writeInt(PackageType.RESPONSE_PACK.getCode());

        //3.序列化器编号
        out.writeInt(commonSerializer.getCode());

        //4.数据长度
        byte[] bytes = commonSerializer.serialize(msg);
        out.writeInt(bytes.length);

        //5.数据
        out.writeBytes(bytes);
    }
}
