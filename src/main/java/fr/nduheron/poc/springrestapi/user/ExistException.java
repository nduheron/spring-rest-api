package fr.nduheron.poc.springrestapi.user;

import fr.nduheron.poc.springrestapi.tools.exception.FunctionalException;

public class ExistException extends FunctionalException {

	private static final long serialVersionUID = 1L;

	public ExistException(String i18nKey, String... args) {
		super(i18nKey, args);
	}

	@Override
	public String getCode() {
		return "ExistException";
	}

}
