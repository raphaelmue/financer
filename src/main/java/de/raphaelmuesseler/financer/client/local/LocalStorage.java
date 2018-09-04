package de.raphaelmuesseler.financer.client.local;

import de.raphaelmuesseler.financer.shared.model.User;

import java.io.*;
import java.util.Set;

public class LocalStorage {

    private static final String LOCATION = System.getenv("APPDATA") + "/Financer";
    private static final File USERDATA_FILE = new File(LOCATION + "/usr/usr.fnc");
    private static final File SETTINGS_FILE = new File(LocalStorage.LOCATION + "/usr/settings.fnc");

    public static User getLoggedInUser() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(USERDATA_FILE))) {
            return (User) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) { }

        return null;
    }

    public static boolean writeUser(User user) {
        USERDATA_FILE.getParentFile().mkdirs();
        return writeObject(USERDATA_FILE, user);
    }

    public static boolean logUserOut() {
        return USERDATA_FILE.delete();
    }

    public static Settings getSettings() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(SETTINGS_FILE))) {
            return (Settings) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) { }

        return null;
    }

    public static boolean writeSettings(Settings settings) {
        SETTINGS_FILE.getParentFile().mkdirs();
        return writeObject(SETTINGS_FILE, settings);
    }

    private static boolean writeObject(File file, Object object) {
        boolean result = false;
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(object);
            result = true;
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
