package fr.nduheron.poc.springrestapi.tools.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nduheron.poc.springrestapi.tools.security.domain.TokenRequest;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class TokenRequestConverter extends AbstractHttpMessageConverter<TokenRequest> {

    // no need to reinvent the wheel for parsing the query string
    private static final FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

    private static final ObjectMapper mapper = new ObjectMapper();

    public TokenRequestConverter() {
        super(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return (TokenRequest.class == clazz);
    }

    @Override
    protected TokenRequest readInternal(Class<? extends TokenRequest> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Map<String, String> vals = formHttpMessageConverter.read(null, inputMessage).toSingleValueMap();

        return mapper.convertValue(vals, TokenRequest.class);
    }

    @Override
    protected void writeInternal(TokenRequest tokenRequest, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        throw new HttpMessageNotWritableException("tokenRequest is not serializable");
    }
}

