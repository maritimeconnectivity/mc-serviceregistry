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

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.security.SecurityUtils;
import com.frequentis.maritime.mcsr.service.ElasticsearchIndexService;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@WebService(endpointInterface = "com.frequentis.maritime.mcsr.web.soap.ElasticsearchIndexResource")
public class ElasticsearchIndexResourceImpl implements ElasticsearchIndexResource {
	private final static Logger log = LoggerFactory.getLogger(ElasticsearchIndexResourceImpl.class);
	
	@Autowired
	ElasticsearchIndexService elasticsearchIndexService;
	
	@Override
	public void reindexAll() throws ProcessingException {
        log.info("REST request to reindex Elasticsearch by user : {}", SecurityUtils.getCurrentUserLogin());
        try {
        elasticsearchIndexService.reindexAll();
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        	throw new ProcessingException(e.getMessage(), e);
        }
	}

}
