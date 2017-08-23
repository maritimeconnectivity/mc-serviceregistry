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

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

public final class SoapTestUtils {

    private SoapTestUtils() {
        // Nothing
    }

    /**
     * Add HTTP Basic authentication with user admin and password admin
     * @param client
     */
    public static void addHttpBasicSecurity(Object client) {
        // Add HTTP Basic authentication
        HTTPConduit cp = (HTTPConduit) ClientProxy.getClient(client).getConduit();
        AuthorizationPolicy ap = new AuthorizationPolicy();
        ap.setUserName("admin");
        ap.setPassword("admin");
        cp.setAuthorization(ap);
    }

    /**
     * Add HTTP Basic authentication with user {@code user} and password {@code password}
     * @param client
     */
    public static void addHttpBasicSecurity(Object client, String user, String password) {
        // Add HTTP Basic authentication
        HTTPConduit cp = (HTTPConduit) ClientProxy.getClient(client).getConduit();
        AuthorizationPolicy ap = new AuthorizationPolicy();
        ap.setUserName("admin");
        ap.setPassword("admin");
        cp.setAuthorization(ap);
    }

}
