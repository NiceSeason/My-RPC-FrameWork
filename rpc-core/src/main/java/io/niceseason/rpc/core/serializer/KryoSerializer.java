package io.niceseason.rpc.core.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);



    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(
                new StdInstantiatorStrategy()));
        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try(ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            Output output=new Output(outputStream))
        {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            logger.error("kryo序列化过程发生错误:{}",e.getMessage());
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(ByteArrayInputStream inputStream=new ByteArrayInputStream(bytes);
            Input input=new Input(inputStream)
        ) {
            Kryo kryo = kryoThreadLocal.get();
            Object readObject = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return readObject;
        } catch (IOException e) {
            logger.error("kryo反序列化过程发生错误:{}",e.getMessage());
            return null;
        }
    }

    @Override
    public int getCode() {
        return 0;
    }
}
