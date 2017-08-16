package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.Collection;
import java.util.List;

public interface Converter<F,T> {

    public T convert(F from);

    public Collection<T> convert(Collection<F> from);
    
    public List<T> convert(List<F> from); 

}
