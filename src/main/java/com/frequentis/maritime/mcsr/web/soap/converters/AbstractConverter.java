package com.frequentis.maritime.mcsr.web.soap.converters;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public abstract class AbstractConverter<F, T> implements Converter<F, T> {

    @Override
    public Collection<T> convert(Collection<F> from) {
    	return convertCollection(from);
    }
    
    @Override
    public List<T> convert(List<F> from) {
    	return convertCollection(from);
    }
    
    private ArrayList<T> convertCollection(Collection<F> from) {
        ArrayList<T> o = new ArrayList<>();
        for(F f : from) {
            o.add(convert(f));
        }
        return o;    	
    }
    
    static <F, T> void mapGeterWithSameName(F f, T t) {
    	Class tClass = t.getClass();
    	Method[] methods = f.getClass().getMethods();
    	for(Method m : methods) {
    		if(m.getParameterCount() != 0) {
    			continue;
    		}
    		String fieldName = m.getName().substring(3);
    		// First letter to lower case
    		fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
    		try {
    			Field field = tClass.getField(fieldName);
				ReflectionUtils.setField(field, t, m.invoke(f));
			} catch (IllegalAccessException | 
					IllegalArgumentException | 
					InvocationTargetException |
					NoSuchFieldException |
					SecurityException e) {
				// Nothing
				continue;
			}
    	}
    }

}
