package com.frequentis.maritime.mcsr.web.soap.converters.xml;

import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractBidirectionalConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDTO;

@Component
public class XsdDTOConverter extends AbstractBidirectionalConverter<Xsd, XsdDTO> {


    @Override
    public XsdDTO convert(Xsd f) {
        if(f == null) {
            return null;
        }
        XsdDTO d = new XsdDTO();
        mapGeterWithSameName(f, d);

        return d;
    }

	@Override
	public Xsd convertReverse(XsdDTO f) {
		if(f == null) {
			return null;
		}
		Xsd d = new Xsd();
		d.setComment(f.comment);
		d.setId(f.id);
		d.setContent(f.content);
		d.setContentContentType(f.contentContentType);
		d.setName(f.name);

		return d;
	}




}
