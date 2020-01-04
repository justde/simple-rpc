package org.justd.simple.common.serializer;

import com.alibaba.fastjson.JSON;

/**
 * @author Zhangjd
 * @title: FastJsonSerializer
 * @description:
 * @date 2020/1/321:09
 */
public class FastJsonSerializer implements Serializer {

    private volatile static FastJsonSerializer serializer;

    public static FastJsonSerializer getInstance() {
        if (serializer != null) {
            return serializer;
        }
        synchronized (FastJsonSerializer.class) {
            if (serializer == null) {
                serializer = new FastJsonSerializer();
            }
        }
        return serializer;
    }


    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz);
    }
}
