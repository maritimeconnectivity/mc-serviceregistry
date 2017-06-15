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
package com.frequentis.maritime.mcsr.repository;

import com.frequentis.maritime.mcsr.config.audit.AuditEventConverter;
import com.frequentis.maritime.mcsr.domain.PersistentAuditEvent;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * An implementation of Spring Boot's AuditEventRepository.
 */
@Repository
public class CustomAuditEventRepository implements AuditEventRepository {

    private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

    private static final String ANONYMOUS_USER = "anonymoususer";

    @Inject
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Inject
    private AuditEventConverter auditEventConverter;

    @Override
    public List<AuditEvent> find(String principal, Date after) {
        Iterable<PersistentAuditEvent> persistentAuditEvents;
        if (principal == null && after == null) {
            persistentAuditEvents = persistenceAuditEventRepository.findAll();
        } else if (after == null) {
            persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal(principal);
        } else {
            persistentAuditEvents =
                persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfter(principal, LocalDateTime.from(after.toInstant()));
        }
        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void add(AuditEvent event) {
        if (!AUTHORIZATION_FAILURE.equals(event.getType()) &&
            !ANONYMOUS_USER.equals(event.getPrincipal().toString())) {

            PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
            persistentAuditEvent.setPrincipal(event.getPrincipal());
            persistentAuditEvent.setAuditEventType(event.getType());
            Instant instant = Instant.ofEpochMilli(event.getTimestamp().getTime());
            persistentAuditEvent.setAuditEventDate(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(event.getData()));
            persistenceAuditEventRepository.save(persistentAuditEvent);
        }
    }

	@Override
	public List<AuditEvent> find(Date after) {
		List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findByAuditEventDateAfter(LocalDateTime.from(after.toInstant()));
		return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
	}

	@Override
	public List<AuditEvent> find(String principal, Date after, String type) {
		List<PersistentAuditEvent> audits = persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfterAndAuditEventType(principal, LocalDateTime.from(after.toInstant()), type);
		return auditEventConverter.convertToAuditEvent(audits);
	}
}
