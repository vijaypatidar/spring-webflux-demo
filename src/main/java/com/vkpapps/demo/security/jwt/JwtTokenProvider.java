package com.vkpapps.demo.security.jwt;

import static java.util.stream.Collectors.joining;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final String AUTHORITIES_KEY = "roles";

  private final JwtProperties jwtProperties;

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    var secret = Base64.getEncoder().encodeToString(this.jwtProperties.getSecretKey().getBytes());
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String createToken(Authentication authentication) {

    String username = authentication.getName();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    var claims = Jwts.claims().setSubject(username);
    if (!authorities.isEmpty()) {
      claims.put(AUTHORITIES_KEY,
          authorities.stream().map(GrantedAuthority::getAuthority).collect(joining(",")));
    }

    var now = new Date();
    var validity = new Date(now.getTime() + this.jwtProperties.getValidityInMs());

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(this.secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String createToken(com.vkpapps.demo.models.User user) {
    var now = new Date();
    var validity = new Date(now.getTime() + this.jwtProperties.getValidityInMs());
    var claims = Jwts.claims().setSubject(user.getUsername());
    claims.put(AUTHORITIES_KEY, String.join(",", user.getRoles()));
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(this.secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    var claims =
        Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();

    Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

    Collection<? extends GrantedAuthority> authorities =
        authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
            : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

    var principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts
          .parserBuilder().setSigningKey(this.secretKey).build()
          .parseClaimsJws(token);
      //  parseClaimsJws will check expiration date. No need do here.
      log.trace("expiration date: {}", claims.getBody().getExpiration());
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.trace("Invalid JWT token: {}", e.getMessage());
    }
    return false;
  }

}
