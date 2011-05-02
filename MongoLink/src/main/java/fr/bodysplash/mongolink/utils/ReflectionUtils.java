package fr.bodysplash.mongolink.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

    private ReflectionUtils() {

    }

    public static Field findPrivateField(final Class<?> clazz, final String fieldName)
			throws NoSuchFieldException {
		Field result;
		try {
			result = clazz.getDeclaredField(fieldName);
		} catch (final NoSuchFieldException e) {
			if (clazz.getSuperclass() != null) {
				return findPrivateField(clazz.getSuperclass(), fieldName);
			}
			throw e;
		}
		return result;
	}
}
