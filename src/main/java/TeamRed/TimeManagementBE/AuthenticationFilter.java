package TeamRed.TimeManagementBE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import TeamRed.TimeManagementBE.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthEntryPoint exceptionHandler;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
		String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (jws != null) {
			try {
				String user = jwtService.getAuthUser(request);
				Authentication auth = new UsernamePasswordAuthenticationToken(user, null, java.util.Collections.emptyList());
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (AuthenticationException authException) {
				exceptionHandler.commence(request, response, authException);
			}
		}
		filterChain.doFilter(request, response);
	}

}
