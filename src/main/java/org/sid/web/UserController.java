package org.sid.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sid.bean.RefreshToken;
import org.sid.bean.RegistrationForm;
import org.sid.entities.AppUser;
import org.sid.security.SecurityConstants;
import org.sid.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(value = "http://localhost:4200", allowCredentials = "true")
public class UserController {
	@Autowired
	private AccountService accservice;

	@PostMapping("/users")
	public AppUser signUp(@RequestBody RegistrationForm data) {

		String username = data.getUsername();
		AppUser u = accservice.findByUsername(username);

		if (u != null)
			throw new RuntimeException("Cet utilisateur existe déjà, essayez avec un autre nom d'utilisateur");

		String password = data.getPassword();
		String repassword = data.getRepassword();

		if (!password.equals(repassword))
			throw new RuntimeException("Les Mots de passe sont different");

		AppUser user = new AppUser();
		user.setPassword(data.getPassword());
		user.setUsername(data.getUsername());

		accservice.saveUser(user);
		return user;

	}

	@PostMapping("/refreshToken")
	public void refreshToken(@RequestBody RefreshToken token, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			String jwt = token.getRefreshtoken();
			Algorithm algorithm = Algorithm.HMAC512(SecurityConstants.SECRET);
			JWTVerifier jwtverifier = JWT.require(algorithm).build();
			DecodedJWT decodejwt = jwtverifier.verify(jwt);
			String username = decodejwt.getSubject();

			AppUser springUser = accservice.findByUsername(username);
			System.out.println(springUser.getUsername());

			String JwtaccessToken = JWT.create().withSubject(springUser.getUsername())
					.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
					.withIssuer(request.getRequestURL().toString())
					.withClaim("roles",
							springUser.getRoles().stream().map(r -> r.getRole()).collect(Collectors.toList()))
					.sign(Algorithm.HMAC512(SecurityConstants.SECRET));
			response.setHeader(SecurityConstants.HEADER_STRING, JwtaccessToken);

			Map<String, String> idToken = new HashMap<String, String>();
			idToken.put("Acces-Token", SecurityConstants.TOKEN_PREFIX + JwtaccessToken);
			idToken.put("Refresh-Token", jwt);
			response.setContentType("application/json");
			new ObjectMapper().writeValue(response.getOutputStream(), idToken);

		} catch (Exception e) {
			response.setHeader("error-message", e.getMessage());

		}

	}

}
