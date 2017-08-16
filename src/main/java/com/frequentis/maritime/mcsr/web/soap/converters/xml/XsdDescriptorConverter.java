package com.frequentis.maritime.mcsr.web.soap.converters.xml;

import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractBidirectionalConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDescriptorDTO;

@Component
public class XsdDescriptorConverter extends AbstractBidirectionalConverter<Xsd, XsdDescriptorDTO> {


    @Override
    public XsdDescriptorDTO convert(Xsd f) {
        if(f == null) {
            return null;
        }
        XsdDescriptorDTO d = new XsdDescriptorDTO();
        mapGeterWithSameName(f, d);

        return d;
    }

	@Override
	public Xsd convertReverse(XsdDescriptorDTO f) {
		if(f == null) {
			return null;
		}
		Xsd d = new Xsd();
		d.setComment(f.comment);
		d.setId(f.id);
		d.setName(f.name);
		
		return d;
	}




}
