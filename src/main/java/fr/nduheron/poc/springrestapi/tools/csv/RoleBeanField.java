package fr.nduheron.poc.springrestapi.tools.csv;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import fr.nduheron.poc.springrestapi.user.model.Role;

public class RoleBeanField extends AbstractBeanField<Role> {

	@Override
	protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		return StringUtils.isBlank(value) ? null : Role.valueOf(value);
	}

}
