package org.sid.security;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TraceInterceptor implements HandlerInterceptor {

	@Autowired
	private TraceRepository tracerepository;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		String user = "Visitor";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String date = simpleDateFormat.format(new Date());

		Trace t = new Trace(null, user, date, request.getRequestURI(), request.getRequestURL().toString(),
				request.getRemoteAddr(), request.getMethod(), response.getStatus());

		if (request.getUserPrincipal() == null) {
			tracerepository.save(t);
		} else {
			t.setUsername(request.getUserPrincipal().getName());
			tracerepository.save(t);
		}

	}

}
