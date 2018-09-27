package de.raphaelmuesseler.financer.client.javafx.local;

import de.raphaelmuesseler.financer.client.local.AbstractLocalStorage;
import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.shared.model.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LocalStorageImpl extends AbstractLocalStorage {
    private static LocalStorageImpl INSTANCE = null;

    public static final String LOCATION;

    static {
        if (System.getenv("APPDATA") != null) {
            LOCATION = System.getenv("APPDATA") + "/Financer";
        } else {
            LOCATION = System.getProperty("user.home") + "/Financer";
        }
    }

    public static final File USERDATA_FILE = new File(LOCATION + "/usr/usr.fnc");
    public static final File SETTINGS_FILE = new File(LOCATION + "/usr/settings.fnc");
    public static final File PROFILE_FILE = new File(LOCATION + "/data/profile.fnc");
    public static final File TRANSACTIONS_FILE = new File(LOCATION + "/data/transactions.fnc");

    public static LocalStorageImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalStorageImpl();
        }
        return INSTANCE;
    }

    public User getLoggedInUser() {
        return (User) readObject(USERDATA_FILE, "user");
    }

    public boolean writeUser(User user) {
        USERDATA_FILE.getParentFile().mkdirs();
        return writeObject(USERDATA_FILE, "user", user);
    }

    public boolean logUserOut() {
        return USERDATA_FILE.delete();
    }

    public Settings getSettings() {
        return (Settings) readObject(SETTINGS_FILE, "settings");
    }

    public boolean writeSettings(Settings settings) {
        return writeObject(SETTINGS_FILE, "settings", settings);
    }

    public boolean writeObject(File file, String key, Object object) {
        file.getParentFile().mkdirs();
        boolean result = false;
        Map<String, Object> map = readFile(file);
        if (map == null) {
            map = new HashMap<>();
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            map.put(key, object);
            outputStream.writeObject(map);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Object readObject(File file, String key) {
        return readFile(file) == null ? null : readFile(file).get(key);
    }

    protected Map<String, Object> readFile(File file) {
        Map<String, Object> result = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            result =  (Map<String, Object>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) { }

        return result;
    }
}
