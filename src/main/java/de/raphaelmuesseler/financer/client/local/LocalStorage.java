package de.raphaelmuesseler.financer.client.local;

import de.raphaelmuesseler.financer.shared.model.User;

import java.io.*;

public class LocalStorage {

    private static final String LOCATION = System.getenv("APPDATA") + "/Financer";
    private static final File USERDATA_FILE = new File(LOCATION + "/usr/usr.fnc");

    public static User getLoggedInUser() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(USERDATA_FILE))) {
            return (User) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) { }

        return null;
    }

    public static boolean writeUser(User user) {
        boolean result = false;

        USERDATA_FILE.getParentFile().mkdirs();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(USERDATA_FILE));
            outputStream.writeObject(user);
            result = true;
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean logUserOut() {
        return USERDATA_FILE.delete();
    }
}
