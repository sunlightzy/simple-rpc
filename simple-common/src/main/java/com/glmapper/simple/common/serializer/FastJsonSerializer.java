package com.glmapper.simple.common.serializer;

import com.alibaba.fastjson.JSON;

/**
 * FastJsonSerializer
 *
 * @author Jerry
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

    private FastJsonSerializer() {
    }

    /**
     * serialize
     *
     * @param obj
     * @return
     */
    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    /**
     * deserialize
     *
     * @param data
     * @param clazz
     * @return
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz);
    }
}
