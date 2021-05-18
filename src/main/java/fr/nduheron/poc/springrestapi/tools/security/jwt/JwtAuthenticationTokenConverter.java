package fr.nduheron.poc.springrestapi.tools.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtAuthenticationTokenConverter extends Converter<Jwt, JwtAuthenticationToken> {
}
