package com.frequentis.maritime.mcsr.web.soap.converters.design;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractBidirectionalConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XmlDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;

@Component
public class DesignConverter extends AbstractBidirectionalConverter<Design, DesignDTO> {

	@Inject
	private DocDTOConverter docConverter;

	@Inject
	private XmlDTOConverter xmlConverter;

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

	@Override
	public Design convertReverse(DesignDTO f) {
		if(f == null) {
			return null;
		}
		Design d = new Design();
		d.setId(f.id);
		d.setName(f.name);
		d.setComment(f.comment);
		d.setDesignAsDoc(docConverter.convertReverse(f.designAsDoc));
		d.setDesignAsXml(xmlConverter.convertReverse(f.designAsXml));
		d.setDesignId(f.designId);
		d.setStatus(f.status);
		d.setVersion(f.version);
		return d;
	}



}
