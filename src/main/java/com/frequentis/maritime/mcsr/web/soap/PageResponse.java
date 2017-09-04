package com.frequentis.maritime.mcsr.web.soap;

import java.util.ArrayList;

import org.springframework.data.domain.Page;

import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;

public class PageResponse {

    public static <A,B> PageDTO<B> buildFromPage(Page<A> page, Converter<A, B> converter) {
        PageDTO<B> dto = new PageDTO<>();
        if(page == null) {
            dto.page = 0;
            dto.pageCount = 0;
            dto.itemTotalCount = 0;
            dto.content = new ArrayList<B>();
        } else {
            dto.page = page.getNumber();
            dto.pageCount = page.getTotalPages();
            dto.itemTotalCount = page.getTotalElements();
            dto.content = new ArrayList<B>(converter.convert(page.getContent()));
        }

        return dto;
    }


}
