package de.raphaelmuesseler.financer.client.local;

import de.raphaelmuesseler.financer.shared.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LocalStorage {

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
    public static final File REQUESTS_FILE = new File(LOCATION + "/tmp/requests.fnc");


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
        return writeObject(SETTINGS_FILE, settings);
    }

    public static boolean writeObject(File file, Object object) {
        file.getParentFile().mkdirs();
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

    public static boolean writeObjects(File file, Iterable<Object> objects) {
        boolean result = true;
        for (Object object : objects) {
            if (!writeObject(file, object)) {
                result = false;
            }
        }
        return result;
    }

    public static Iterable<Object> readObject(File file) {
        List<Object> result = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            while (fileInputStream.available() > 0) {
                result.add(inputStream.readObject());
            }
            return result;
        } catch (IOException | ClassNotFoundException ignored) { }

        return null;
    }
}
