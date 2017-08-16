package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.Collection;
import java.util.List;

public interface ConverterBidirectional<F, T> extends Converter<F, T> {
    
    public F convertReverse(T from);

    public Collection<F> convertReverse(Collection<T> from);
    
    public List<F> convertReverse(List<T> from); 

}
