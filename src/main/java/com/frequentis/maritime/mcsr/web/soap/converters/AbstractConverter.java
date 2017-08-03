package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractConverter<F, T> implements Converter<F, T> {

    @Override
    public Collection<T> convert(Collection<F> from) {
        ArrayList<T> o = new ArrayList<>();
        for(F f : from) {
            o.add(convert(f));
        }
        return o;
    }


}
