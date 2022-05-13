package org.sid.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String JwtToken = request.getHeader("Authorization");
		if (JwtToken != null && JwtToken.startsWith("Bearer ")) {
			try {

				String jwt = JwtToken.substring(7);
				Algorithm algorithm = Algorithm.HMAC512("MySecret@1234");
				JWTVerifier jwtverifier = JWT.require(algorithm).build();
				DecodedJWT decodejwt = jwtverifier.verify(jwt);
				String username = decodejwt.getSubject();
				String[] roles = decodejwt.getClaim("roles").asArray(String.class);
				Collection<GrantedAuthority> authorities = new ArrayList<>();
				for (String r : roles) {
					authorities.add(new SimpleGrantedAuthority(r));
				}

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						username, null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				filterChain.doFilter(request, response);

			} catch (Exception e) {
				response.setHeader("error-message", e.getMessage());
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}

		} else {
			filterChain.doFilter(request, response);
		}

	}

}
