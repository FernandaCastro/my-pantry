package com.fcastro.security.core.jwt;

import com.fcastro.security.core.config.SecurityPropertiesConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTHandler {

    private static final long TOKEN_VALIDITY = 86400000L; //24hours
    private static final long TOKEN_VALIDITY_REMEMBER = 2592000000L;//30days
    private final Key key;

    private final SecurityPropertiesConfig securityConfigData;

    public JWTHandler(SecurityPropertiesConfig propertiesConfig) {
        this.securityConfigData = propertiesConfig;

        var baseSecret = Hex.decode(propertiesConfig.getSecret());
        this.key = Keys.hmacShaKeyFor(baseSecret);
    }

    public String createToken(String email, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity = rememberMe ? new Date(now + TOKEN_VALIDITY_REMEMBER) : new Date(now + TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createSysToken(String email, String role) {
        long now = (new Date()).getTime();
        Map<String, Object> claims = new HashMap<>();
        if (role != null && role.length() > 0) {
            claims.put("role", role);
        }

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication verifyAndGetAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            List<GrantedAuthority> authorities;
            var roles = claims.get("role", String.class);
            authorities = (roles != null && roles.length() > 0) ?
                    AuthorityUtils.commaSeparatedStringToAuthorityList(roles) :
                    AuthorityUtils.NO_AUTHORITIES;
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
        } catch (JwtException | IllegalArgumentException ignored) {
            return null;
        }
    }
}