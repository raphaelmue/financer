package de.raphaelmuesseler.financer.client.local;

import java.io.File;

public interface LocalStorage {
    Settings getSettings();
    Object readObject(File file, String key);
    boolean writeObject(File file, String key, Object object);
}
