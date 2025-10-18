package com.ey.foodEase.util;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.foodEase.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private final long jwtExpirationMs = 24 * 60 * 60 * 1000;

	public String generateToken(String username, Long id, Role activeRole, Set<Role> allRoles) {
	    return Jwts.builder()
	        .setSubject(username)
	        .claim("id", id)
	        .claim("activeRole", activeRole.name())
	        .claim("roles", allRoles.stream().map(Enum::name).collect(Collectors.toList()))
	        .setIssuedAt(new Date())
	        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
	        .signWith(key)
	        .compact();
	}


	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
	
	public Claims getAllClaimsFromToken(String token) {
	    return Jwts.parserBuilder()
	        .setSigningKey(key)
	        .build()
	        .parseClaimsJws(token)
	        .getBody();
	}
	
	public Long extractUserId(String token) {
	    return ((Number) getAllClaimsFromToken(token).get("id")).longValue();
	}
}