package com.negociofechado.comum.security;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public String gerarToken(Long usuarioId) {
        return gerarToken(usuarioId, expiration);
    }

    public String gerarRefreshToken(Long usuarioId) {
        return gerarToken(usuarioId, refreshExpiration);
    }

    private String gerarToken(Long usuarioId, Long tempoExpiracao) {
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + tempoExpiracao);

        return Jwts.builder()
                .subject(usuarioId.toString())
                .issuedAt(agora)
                .expiration(dataExpiracao)
                .signWith(getChave())
                .compact();
    }

    public Long getUsuarioIdDoToken(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public boolean isTokenValido(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiracao = claims.getExpiration();
            return expiracao.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
