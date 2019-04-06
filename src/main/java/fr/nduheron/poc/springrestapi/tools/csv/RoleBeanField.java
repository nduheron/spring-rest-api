package fr.nduheron.poc.springrestapi.tools.csv;

import com.opencsv.bean.AbstractBeanField;
import fr.nduheron.poc.springrestapi.user.model.Role;
import org.apache.commons.lang3.StringUtils;

public class RoleBeanField extends AbstractBeanField<Role> {

    @Override
    protected Object convert(String value) {
        return StringUtils.isBlank(value) ? null : Role.valueOf(value);
    }

}
