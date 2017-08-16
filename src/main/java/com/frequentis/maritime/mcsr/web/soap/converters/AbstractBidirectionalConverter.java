package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractBidirectionalConverter<F, T> extends AbstractConverter<F, T> implements ConverterBidirectional<F, T> {

    public Collection<F> convertReverse(Collection<T> from) {
    	return convertCollectionReverse(from);
    }
    
    public List<F> convertReverse(List<T> from) {
    	return convertCollectionReverse(from);
    }
    
    private ArrayList<F> convertCollectionReverse(Collection<T> from) {
    	if(from == null) {
    		return null;
    	}
        ArrayList<F> o = new ArrayList<>();
        for(T f : from) {
            o.add(convertReverse(f));
        }
        return o;    	
    }

}
