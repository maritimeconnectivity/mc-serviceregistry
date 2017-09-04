package com.frequentis.maritime.mcsr.web.util;

import org.slf4j.Logger;

import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;

public final class WebUtils {

	private WebUtils() {
		// Nothing
	}

	/**
	 * Extract organization id from token.
	 *
	 * @param token
	 * @return OrganizationId if can be extracted or empty String.
	 */
	public static String extractOrganizationIdFromToken(String token) {
		return extractOrganizationIdFromToken(token, null);
	}

	/**
	 * Extract organization id from token.
	 *
	 * @param token
	 * @return OrganizationId if can be extracted or empty String.
	 */
	public static String extractOrganizationIdFromToken(String token, Logger logger) {
		try {
			return HeaderUtil.extractOrganizationIdFromToken(token);
		} catch (Exception e) {
			if(logger != null) {
				logger.warn("No organizationId could be parsed from the bearer token");
			}
			return "";
		}
	}



}
