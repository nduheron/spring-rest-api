package fr.nduheron.poc.springrestapi.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nduheron.poc.springrestapi.api.AuthentificationApi;
import fr.nduheron.poc.springrestapi.dto.TokenDto;
import fr.nduheron.poc.springrestapi.tools.exception.BadRequestException;
import fr.nduheron.poc.springrestapi.tools.exception.TechnicalException;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.OffsetDateTime;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.length;

@RestController
@Transactional
public class AuthentificationController implements AuthentificationApi {
    private static final String USER_INFOS = "userInfos";

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repo;
    @Value("${security.token.secret}")
    private String tokenSecret;
    @Value("${security.token.ttl}")
    private String tokenTTL;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageSource messageSource;

    @Override
    public ResponseEntity<TokenDto> login(String username, String password, String grantType) {
        validate(username, password, grantType);

        Optional<User> user = repo.findById(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("L'utilisateur %s n'existe pas.", username));
        }

        if (!user.get().isEnabled()) {
            throw new DisabledException(String.format("L'utilisateur %s n'est pas actif.", username));
        }

        if (passwordEncoder.matches(password, user.get().getPassword())) {
            user.get().setDerniereConnexion(OffsetDateTime.now());
            return ResponseEntity.ok(createToken(userMapper.toDto(user.get())));
        }
        throw new BadCredentialsException("Login/mot de passe incorrect.");
    }

    private void validate(String username, String password, String grantType) {
        List<Error> errors = new ArrayList<>();
        if (length(username) < 2 || length(username) > 20) {
            errors.add(new Error(Error.INVALID_FORMAT, "size must be between 2 and 20",  "username"));
        }
        if (length(password) < 5 || length(password) > 20) {
            errors.add(new Error(Error.INVALID_FORMAT, "size must be between 5 and 20",  "password"));
        }

        if (!errors.isEmpty()) {
            throw  new BadRequestException(errors);
        }
    }

    private TokenDto createToken(final Object userInfos) {
        try {
            // The JWT signature algorithm we will be using to sign the token
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            // We will sign our JWT with our ApiKey secret
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(tokenSecret);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            // Let's set the JWT Claims
            JwtBuilder builder = Jwts.builder().setIssuedAt(now).signWith(signatureAlgorithm, signingKey);

            if (userInfos != null) {
                Map<String, Object> claims = new HashMap<>();
                claims.put(USER_INFOS, objectMapper.writeValueAsString(userInfos));
                builder.setClaims(claims);
            }
            // if it has been specified, let's add the expiration
            Long ttl = Long.parseLong(tokenTTL);
            long expMillis = nowMillis + ttl;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);

            return new TokenDto().accessToken(builder.compact()).expiresIn(ttl).tokenType("bearer");
        } catch (JsonProcessingException e) {
            throw new TechnicalException("Erreur lors de la creation du token JWT", e);
        }
    }
}
