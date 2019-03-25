package de.raphaelmuesseler.financer.shared.model.user;

public interface Settings {

    void setValueByProperty(String property, String value);

    String getValueByProperty(String property);

}
