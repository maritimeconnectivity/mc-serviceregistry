package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.XmlDTO;

@Component
public class DesignConverter extends AbstractConverter<Design, DesignDTO> {

	@Inject
	private Converter<Doc, DocDTO> docConverter;
	
	@Inject
	private Converter<Xml, XmlDTO> xmlConverter;

    @Override
    public DesignDTO convert(Design from) {
        if(from == null) {
            return null;
        }
        DesignDTO ddto = new DesignDTO();
        mapGeterWithSameName(from, ddto);
        ddto.designAsDoc = docConverter.convert(from.getDesignAsDoc());
        ddto.designAsXml = xmlConverter.convert(from.getDesignAsXml());

        return ddto;
    }




}
