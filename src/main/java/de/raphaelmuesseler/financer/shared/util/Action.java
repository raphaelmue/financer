package de.raphaelmuesseler.financer.shared.util;

import java.sql.SQLException;

public interface Action<T> {
    void action(T object);
}
