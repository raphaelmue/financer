package org.financer.shared.model.user;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Settings extends Serializable {


    void setValueByProperty(Property property, String value);

    String getValueByProperty(Property property);

}
