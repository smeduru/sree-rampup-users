package com.sree.rampup.users.util;

import org.springframework.beans.BeanUtils;

/**
 * Clones a given object and copy the property values too.
 */
public class ObjectUtil {
    public static <T> T cloneObject(T object) throws IllegalAccessException, InstantiationException {
        if (object == null) {
            return null;
        }
        T newObject = (T) object.getClass().newInstance();
        BeanUtils.copyProperties(object, newObject);
        return newObject;
    }
}
