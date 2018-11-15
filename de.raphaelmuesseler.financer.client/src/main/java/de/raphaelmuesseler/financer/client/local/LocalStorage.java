package de.raphaelmuesseler.financer.client.local;

import java.util.List;

public interface LocalStorage {
    Object readObject(String key);

    @SuppressWarnings("unchecked")
    default <T> List<T> readList(String key) {
        return (List<T>) this.readObject(key);
    }

    boolean writeObject(String key, Object object);

    boolean deleteObject(String key);

    boolean deleteAllData();
}
