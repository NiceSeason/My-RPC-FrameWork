package io.niceseason.rpc.core.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("序列化过程中有错误产生:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object o = objectMapper.readValue(bytes, clazz);
            if (o instanceof RpcRequest) handlerRequest(o);
            return o;
        } catch (IOException e) {
            logger.error("反序列化过程中有错误产生:{}", e.getMessage());
            return null;
        }
    }

    private void handlerRequest(Object o) throws IOException {
        RpcRequest request = (RpcRequest) o;
        for (int i = 0; i < request.getParameterType().length; i++) {
            Class<?> type = request.getParameterType()[i];
            if (!type.isAssignableFrom(request.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(request.getParameters()[i]);
                request.getParameters()[i] = objectMapper.readValue(bytes, type);
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }
}
