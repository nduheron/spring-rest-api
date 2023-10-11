package fr.nduheron.poc.springrestapi.tools.rest.csv;

import com.opencsv.ICSVWriter;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@ConditionalOnClass(ICSVWriter.class)
public class CsvMessageConverter<T> extends AbstractHttpMessageConverter<List<T>> {
    public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", StandardCharsets.UTF_8);

    public CsvMessageConverter() {
        super(MEDIA_TYPE);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    protected void writeInternal(List<T> reponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        try (OutputStreamWriter outputStream = new OutputStreamWriter(outputMessage.getBody())) {
            MappingStrategy<T> strategy = new ColumnPositionWithHeaderMappingStrategy<>();
            if (!reponse.isEmpty()) {
                strategy.setType((Class<T>) reponse.get(0).getClass());
                strategy.generateHeader(reponse.get(0));
            }

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(outputStream)
                    .withQuotechar(ICSVWriter.NO_QUOTE_CHARACTER).withOrderedResults(true).withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(reponse);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new HttpMessageNotWritableException("Erreur lors de la création du CSV", e);
        }
    }

    @Override
    protected List<T> readInternal(Class<? extends List<T>> clazz, HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {
        throw new UnsupportedOperationException("CSV reader not yet implemented!!!");
    }

}
