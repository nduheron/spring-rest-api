package fr.nduheron.poc.springrestapi.tools.csv;

import java.util.ResourceBundle;

import com.google.common.base.CaseFormat;
import com.opencsv.ICSVParser;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class ColumnPositionWithHeaderMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

	@Override
	public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {

        if(type == null) {
            throw new IllegalStateException(ResourceBundle
                    .getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale)
                    .getString("type.before.header"));
        }
        
        // Always take what's been given or previously determined first.
        if(headerIndex.isEmpty()) {
            String[] headers = getFieldMap().generateHeader(bean);
            for (int i = 0; i< headers.length; i++) {
            	headers[i] =  CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, headers[i]);
			}
            headerIndex.initializeHeaderIndex(headers);
            return headers;
        }
        
        // Otherwise, put headers in the right places.
        return headerIndex.getHeaderIndex();
    
	}
}
