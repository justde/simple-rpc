package org.justd.simple.common.serializer;

/**
 * @author Zhangjd
 * @title: Serializer
 * @description:
 * @date 2020/1/321:11
 */
public interface Serializer {

    /**
     * serialize
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * deserialize
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
