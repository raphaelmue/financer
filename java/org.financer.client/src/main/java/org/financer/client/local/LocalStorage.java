package org.financer.client.local;

import java.io.Serializable;
import java.util.List;

public interface LocalStorage {
    Serializable readObject(String key);

    @SuppressWarnings("unchecked")
    default <T> List<T> readList(String key) {
        return (List<T>) this.readObject(key);
    }

    boolean writeObject(String key, Serializable object);

    boolean deleteObject(String key);

    boolean deleteAllData();
}
