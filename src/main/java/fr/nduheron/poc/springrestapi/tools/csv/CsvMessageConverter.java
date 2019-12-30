package fr.nduheron.poc.springrestapi.tools.csv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class CsvMessageConverter<T> extends AbstractHttpMessageConverter<T> {
    public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charset.forName("utf-8"));

    private final ObjectMapper objectMapper;
    private final CsvMapper csvMapper = new CsvMapper();

    public CsvMessageConverter(ObjectMapper objectMapper) {
        super(MEDIA_TYPE);
        this.objectMapper = objectMapper;
        csvMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        csvMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected void writeInternal(T object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        try {
            ObjectWriter objectWriter = getCsvWriter(object);
            try (PrintWriter outputWriter = new PrintWriter(outputMessage.getBody())) {
                outputWriter.write(objectWriter.writeValueAsString(object));
            }
        } catch (Exception e) {
            throw new HttpMessageNotWritableException("Erreur lors de la création du CSV", e);
        }
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {
        throw new UnsupportedOperationException("the converter can only write CSV!!!");
    }

    private ObjectWriter getCsvWriter(T object) {
        Set<String> fields = getUniqueFieldNames(object);
        CsvSchema.Builder schemaBuilder = CsvSchema.builder().setUseHeader(true);
        for (String field : fields) {
            schemaBuilder.addColumn(field);
        }
        return csvMapper.writerFor(List.class).with(schemaBuilder.build());
    }

    private Set<String> getUniqueFieldNames(T object) {
        try {
            JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(object));
            Set<String> uniqueFieldNames = new LinkedHashSet<>();
            root.forEach(element -> {
                Iterator<String> it = element.fieldNames();
                while (it.hasNext()) {
                    String field = it.next();
                    uniqueFieldNames.add(field);
                }
            });
            return uniqueFieldNames;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
