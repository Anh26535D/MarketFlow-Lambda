package edu.hust.marketflow.utils;

import java.lang.reflect.Field;

// TODO: Restrict to support only model of data source classes
public class DataSourceMapper {

    public static int getFieldCount(Class<?> clazz) {
        return clazz.getDeclaredFields().length;
    }

    public static <T> T fromArray(String[] data, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (int i = 0; i < fields.length && i < data.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = castValue(data[i], field.getType());
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping data to " + clazz.getSimpleName(), e);
        }
    }

    private static Object castValue(String value, Class<?> type) {
        if (value == null || value.isEmpty()) {
            if (type.isPrimitive()) {
                if (type == int.class) return 0;
                if (type == double.class) return 0.0;
                if (type == boolean.class) return false;
            }
            return null;
        }

        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == double.class || type == Double.class) return Double.parseDouble(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        return value;
    }

}
