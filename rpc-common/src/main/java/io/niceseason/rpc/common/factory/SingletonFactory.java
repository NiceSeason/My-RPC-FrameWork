package io.niceseason.rpc.common.factory;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SingletonFactory{

    private static volatile Map<String, Object> OBJECT_MAP = new HashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        String key = clazz.toString();
        Object instance = OBJECT_MAP.get(key);
        if (instance == null) {
            synchronized (SingletonFactory.class) {
                if (OBJECT_MAP.get(key) == null) {
                    try {
                        instance = clazz.getDeclaredConstructor().newInstance();
                        OBJECT_MAP.put(key, instance);
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        log.error("单例工厂创建实例过程发生错误:{}", e.getMessage());
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
