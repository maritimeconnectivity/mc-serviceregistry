package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.Collection;
import java.util.List;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Converter<F,T> {

    public T convert(F from);

    public Collection<T> convert(Collection<F> from);
    
    public List<T> convert(List<F> from); 

}
