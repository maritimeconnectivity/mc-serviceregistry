/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2016 Frequentis AG
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
package com.frequentis.maritime.mcsr.dataload;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class CSRFRestTemplate extends RestTemplate {

    private String host;

    public class BasicAuthorizationInterceptor  implements ClientHttpRequestInterceptor {

        private final String username;
        private final String password;

        public BasicAuthorizationInterceptor (String password, String username) {
            this.password = password;
            this.username = username;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            byte[] token = Base64.getEncoder().encode((this.username + ":" + this.password).getBytes());
            request.getHeaders().add("Authorization", "Basic " + new String(token));
            return execution.execute(request, body);
        }
    }

    public class CSRFInterceptor  implements ClientHttpRequestInterceptor {

        private String csrfToken = null;
        private String jSessionId = null;

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            if (csrfToken != null && jSessionId != null) {
                request.getHeaders().add("Cookie", "JSESSIONID=" + jSessionId + "; CSRF-TOKEN=" + csrfToken);
                request.getHeaders().add("X-CSRF-TOKEN", csrfToken);
            }

            ClientHttpResponse response = execution.execute(request, body);

            if (response != null && response.getHeaders() != null && response.getHeaders().get("Set-Cookie") != null) {
                response.getHeaders().get("Set-Cookie").forEach(s -> {
                    if (s.contains("CSRF-TOKEN")) {
                        csrfToken = s.substring(11,s.indexOf(";"));
                    } else if (s.contains("JSESSIONID")) {
                        jSessionId = s.substring(11,s.indexOf(";"));
                    }
                });
            }
            return response;
        }
    }


    public CSRFRestTemplate(String host) {
        this.host = host;
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new CSRFInterceptor());
        setInterceptors(interceptors);
    }

    public CSRFRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    public CSRFRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    public void login() {
        // get first csrf token and session id
        execute(host, HttpMethod.GET, null, null);

        // now authenticate
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("j_username", "admin");
        form.add("j_password", "admin");
        form.add("remember-me","true");
        form.add("submit","Login");
        URI location = postForLocation(host + "/api/authentication?cacheBuster=" + System.currentTimeMillis(), form);

        // now authorize.. and retrieve the final csrf token
        URI account = execute(host + "/api/account?cacheBuster=" + System.currentTimeMillis(), HttpMethod.GET, null, null);
    }
}
