package org.sid.security;

public class SecurityConstants {

	public static final String SECRET = "MySecret@1234";
	public static final long EXPIRATION_TIME = 864_000_000; // 10 JOURS
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final long EXPIRATION_TIME_REFRESH = 864_000_000 * 3 * 12;

}
