package org.sid.security;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationmanager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationmanager) {
		super();
		this.authenticationmanager = authenticationmanager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		return authenticationmanager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		/*
		 * Le Token de connexion
		 */
		User springUser = (User) authResult.getPrincipal();

		String JwtToken = JWT.create().withSubject(springUser.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles",
						springUser.getAuthorities().stream().map(ga -> ga.getAuthority()).collect(Collectors.toList()))
				.sign(Algorithm.HMAC512(SecurityConstants.SECRET));
		response.setHeader(SecurityConstants.HEADER_STRING, JwtToken);

		/*
		 * Le Token de rafrechissement
		 */
		String JwtRefreshToken = JWT.create().withSubject(springUser.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME_REFRESH))
				.withClaim("roles",
						springUser.getAuthorities().stream().map(ga -> ga.getAuthority()).collect(Collectors.toList()))
				.sign(Algorithm.HMAC512(SecurityConstants.SECRET));

		Map<String, String> idToken = new HashMap<>();
		idToken.put("Acces-Token", SecurityConstants.TOKEN_PREFIX + JwtToken);
		idToken.put("Refresh-Token", JwtRefreshToken);
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getOutputStream(), idToken);

	}

}
