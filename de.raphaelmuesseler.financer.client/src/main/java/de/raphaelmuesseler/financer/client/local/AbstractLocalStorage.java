package de.raphaelmuesseler.financer.client.local;

import de.raphaelmuesseler.financer.shared.model.User;

import java.io.File;
import java.util.Map;

public abstract class AbstractLocalStorage implements LocalStorage {
    public abstract User getLoggedInUser();

    public abstract boolean writeUser(User user);

    public abstract boolean logUserOut();

    public abstract Settings getSettings();

    public abstract boolean writeSettings(Settings settings);

    public abstract boolean writeObject(File file, String key, Object object);

    public abstract Object readObject(File file, String key);

    protected abstract Map<String, Object> readFile(File file);

}
