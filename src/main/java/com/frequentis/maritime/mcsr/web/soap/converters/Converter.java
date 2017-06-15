package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.Collection;

public interface Converter<F,T> {
	
	public T convert(F from);
	
	public Collection<T> convert(Collection<F> from);
	
}
