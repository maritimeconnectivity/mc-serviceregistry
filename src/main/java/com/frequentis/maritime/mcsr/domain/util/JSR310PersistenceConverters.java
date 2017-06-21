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
package com.frequentis.maritime.mcsr.domain.util;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.util.JSR310DateConverters.DateToZonedDateTimeConverter;
import com.frequentis.maritime.mcsr.domain.util.JSR310DateConverters.ZonedDateTimeToDateConverter;


public final class JSR310PersistenceConverters {
	

    private JSR310PersistenceConverters() {}

//    //@Converter(autoApply = true)
//    public static class LocalDateConverter implements AttributeConverter<LocalDate, java.sql.Date> {
//
//        @Override
//        public java.sql.Date convertToDatabaseColumn(LocalDate date) {
//            return date == null ? null : java.sql.Date.valueOf(date);
//        }
//
//        @Override
//        public LocalDate convertToEntityAttribute(java.sql.Date date) {
//            return date == null ? null : date.toLocalDate();
//        }
//    }
//
    @Converter(autoApply = true)
    public static class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Date> {
    	private final Logger log = LoggerFactory.getLogger(ZonedDateTimeConverter.class);

        @Override
        public Date convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        	log.debug("Convert from {}");
            return ZonedDateTimeToDateConverter.INSTANCE.convert(zonedDateTime);
        }

        @Override
        public ZonedDateTime convertToEntityAttribute(Date date) {
        	log.debug("Convert from {}");
            return DateToZonedDateTimeConverter.INSTANCE.convert(date);
        }
    }
//
//    //@Converter(autoApply = true)
//    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Date> {
//
//        @Override
//        public Date convertToDatabaseColumn(LocalDateTime localDateTime) {
//            return LocalDateTimeToDateConverter.INSTANCE.convert(localDateTime);
//        }
//
//        @Override
//        public LocalDateTime convertToEntityAttribute(Date date) {
//            return DateToLocalDateTimeConverter.INSTANCE.convert(date);
//        }
//    }
//    
//    @Converter(autoApply = true)
//    public static class ZonedDateTimeConverter implements AttributeConverter<java.time.ZonedDateTime, java.sql.Timestamp> {
//
//        @Override
//        public java.sql.Timestamp convertToDatabaseColumn(ZonedDateTime entityValue) {
//           return Timestamp.from(entityValue.toInstant());
//        }
//
//        @Override
//        public ZonedDateTime convertToEntityAttribute(java.sql.Timestamp databaseValue) {
//            LocalDateTime localDateTime = databaseValue.toLocalDateTime();
//            return localDateTime.atZone(ZoneId.systemDefault());
//        }
//
//    }
    
    
}
