package com.glmapper.simple.common.serializer;

/**
 * serializer interface
 *
 * @author: Jerry
 */
public interface Serializer {

    /**
     * serialize
     *
     * @param obj
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * deserialize
     *
     * @param data
     * @param clazz
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
