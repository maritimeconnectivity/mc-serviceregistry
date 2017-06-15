/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2017 Frequentis AG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frequentis.maritime.mcsr.web.rest.util;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

public class ResourceResolver implements LSResourceResolver {

    public LSInput resolveResource(String type, String namespaceURI,
                                   String publicId, String systemId, String baseURI) {

        // note: XSD's are expected to be in the root of the classpath
        InputStream resourceAsStream = this.getClass().getClassLoader()
            .getResourceAsStream(systemId);
        return new XmlInput(publicId, systemId, resourceAsStream);
    }

}
