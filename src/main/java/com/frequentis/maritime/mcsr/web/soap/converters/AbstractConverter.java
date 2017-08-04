package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;

import org.springframework.util.Assert;
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

}
