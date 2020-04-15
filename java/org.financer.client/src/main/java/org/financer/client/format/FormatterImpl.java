package org.financer.client.format;

import org.financer.client.domain.model.user.User;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.Formattable;

public abstract class FormatterImpl implements Formatter {
    protected final User user;

    public FormatterImpl(LocalStorage localStorage) {
        this.user = localStorage.readObject("user");
    }

    public FormatterImpl(User user) {
        this.user = user;
    }

    @Override
    public String format(Formattable formattable) {
        return formattable.format(user);
    }
}
