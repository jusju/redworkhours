package TeamRed.TimeManagementBE.service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {
	static final long EXPIRATIONTIME = 1000*60*60*24;
	static final String PREFIX = "Bearer";
	static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //Key must be changed in production environment
	
	//Generates a signed JWT token
	public String getToken(String username, Map<String, Object> userDetails) {
		String token = Jwts.builder()
				.setSubject(username)
				.addClaims(userDetails)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(key)
				.compact();
		return token;
	}
		
	//Gets a token from request Authorization header, verifies a token, gets username
	public String getAuthUser(HttpServletRequest request) {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		try {
			if (token != null) {
				String user = Jwts.parserBuilder()
						.setSigningKey(key)
						.build()
						.parseClaimsJws(token.replace(PREFIX, ""))
						.getBody()
						.getSubject();
				if (user != null) {
					return user;
				}
			}
			return null;
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid token");
		}
	}
}
