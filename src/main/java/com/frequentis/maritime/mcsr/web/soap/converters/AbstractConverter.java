package com.frequentis.maritime.mcsr.web.soap.converters;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

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
    	if(from == null) {
    		return null;
    	}
        ArrayList<T> o = new ArrayList<>();
        for(F f : from) {
            o.add(convert(f));
        }
        return o;    	
    }
    
    /**
     * Mapping from <em>f</em> public getter to the <em>t</em> public fields.
     * @param f
     * @param t
     */
    protected static <F, T> void mapGeterWithSameName(F f, T t) {
    	Class<?> tClass = t.getClass();
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
    
    /**
     * Mapping from <em>f</em> public values to the <em>t</em> public setters.
     * @param f
     * @param t
     */
    protected static <F, T> void mapSetterWithSameName(F f, T t) {
    	if(f == null || t == null) {
    		return;
    	}
    	Field[] fFields = f.getClass().getFields();
    	Class<?> tClass = t.getClass();
    	for(Field field : fFields) {
    		String fieldName = field.getName();
    		String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    		try {
    			Method m = tClass.getMethod(setterName, field.getType());
    			m.invoke(t, field.get(f));
    		} catch (NoSuchMethodException | SecurityException | NullPointerException | 
    				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
    			// Nothing
    		}
    	}
    	
    }
    
    protected static <T> List<T> castToList(Collection<T> col) {
    	if(col == null) {
    		return null;
    	}
    	if(col instanceof List) {
    		return (List) col;
    	}
    	return new ArrayList<T>(col);
    }
    
    protected static <T> Set<T> castToSet(Collection<T> col) {
    	if(col == null) {
    		return null;
    	}
    	if(col instanceof Set) {
    		return (Set) col;
    	}
    	return new HashSet<T>(col);
    }
    

}
