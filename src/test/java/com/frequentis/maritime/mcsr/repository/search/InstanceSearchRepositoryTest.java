/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2018 Frequentis AG
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

package com.frequentis.maritime.mcsr.repository.search;

import static org.junit.Assert.*;

import org.elasticsearch.action.index.IndexRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.frequentis.maritime.mcsr.domain.Instance;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
public class InstanceSearchRepositoryTest {
    
    @Autowired
    public InstanceSearchRepository isr;
    
    @Autowired
    public ElasticsearchTemplate est;
    
    
    @Before
    public void beforeTest() {
        
        
    }

    @Test
    public void testFindOneByInstanceIdAndVersion() {
        //fail("Not yet implemented");
    }

    @Test
    public void testFindByKeywords() {
       //fail("Not yet implemented");
    }

    @Test
    public void testFindByKeywordsAndCompliantTrue() {
        //fail("Not yet implemented");
    }

    @Test
    public void testFindByUnlocode() {
        //fail("Not yet implemented");
    }

    @Test
    public void testFindByUnlocodeAndCompliantTrue() {
        //fail("Not yet implemented");
    }

}
