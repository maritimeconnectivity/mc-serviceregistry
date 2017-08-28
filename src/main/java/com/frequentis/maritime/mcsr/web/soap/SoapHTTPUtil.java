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

package com.frequentis.maritime.mcsr.web.soap;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

public final class SoapHTTPUtil {

    private SoapHTTPUtil() {
        // Nothing
    }
    
    public static HttpServletRequest currentHttpRequest() {
        Message message = PhaseInterceptorChain.getCurrentMessage();
        if(message == null) {
            return null;
        }
        // get the HTTP request
        HttpServletRequest httpRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

        return httpRequest;
    }
    
    public static String currentBearerToken() {
        HttpServletRequest httpRequest = currentHttpRequest();
        if(httpRequest == null) {
            return null;
        }
        String authorization = httpRequest.getHeader("Authorization");
        if(authorization == null || !authorization.contains("Bearer")) {
            return null;
        }
        authorization = authorization.replaceFirst("[bB]earer ", "");
        
        return authorization;
    }
}
