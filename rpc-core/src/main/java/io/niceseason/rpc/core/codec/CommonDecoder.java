package io.niceseason.rpc.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.enumeration.PackageType;
import io.niceseason.rpc.common.enumeration.RpcError;
import io.niceseason.rpc.common.exception.RpcException;
import io.niceseason.rpc.core.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);

    private static final int MAGIC_NUMBER=0xCAFEBABE;




    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        if (magicNum != MAGIC_NUMBER) {
            logger.error("不识别的协议包:{}",RpcError.UNKNOWN_PROTOCOL.getMessage());
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        Class<?> clazz;

        int packageType = in.readInt();
        if (packageType==PackageType.REQUEST_PACK.getCode())
            clazz = RpcRequest.class;
        else
            clazz = RpcResponse.class;

        int serializeNum = in.readInt();
        CommonSerializer commonSerializer = CommonSerializer.getByCode(serializeNum);
        if(commonSerializer == null) {
            logger.error("不识别的反序列化器: {}", commonSerializer);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        Object deserialize = commonSerializer.deserialize(bytes, clazz);
        out.add(deserialize);
    }
}
