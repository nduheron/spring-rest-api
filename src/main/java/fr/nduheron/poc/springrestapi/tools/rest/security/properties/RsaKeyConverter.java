package fr.nduheron.poc.springrestapi.tools.rest.security.properties;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
@ConfigurationPropertiesBinding
public class RsaKeyConverter implements Converter<String, RSAKey> {
    private static final ResourceLoader resourceLoader = new DefaultResourceLoader(Thread.currentThread().getContextClassLoader());

    @Override
    public RSAKey convert(String source) {
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(resourceLoader.getResource(source).getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            JWK jwk = JWK.parseFromPEMEncodedObjects(bufferedReader.lines().parallel().collect(Collectors.joining(System.lineSeparator())));
            return jwk.toRSAKey();
        } catch (IOException | JOSEException e) {
            throw new IllegalStateException(String.format("An error occurred reading key %s", source), e);
        }
    }

}
