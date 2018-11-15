package de.raphaelmuesseler.financer.client.javafx.local;

import de.raphaelmuesseler.financer.client.local.LocalStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocalStorageImpl implements LocalStorage {
    private static LocalStorageImpl INSTANCE = null;

    public enum LocalStorageFile {
        USERDATA("/usr/usr.fnc", "user"),
        SETTINGS("/usr/settings.fnc", "settings"),
        TRANSACTIONS("/data/transactions.fnc", "transactions", "fixedTransactions"),
        CATEGORIES("/data/categories.fnc", "categories");

        private final String path;
        private final File file;
        private final String[] keys;

        LocalStorageFile(String path, String... keys) {
            if (System.getenv("APPDATA") != null) {
                this.path = System.getenv("APPDATA") + "/Financer" + path;
            } else {
                this.path = System.getProperty("user.home") + "/Financer" + path;
            }
            this.file = new File(this.path);
            this.keys = keys;
        }

        public File getFile() {
            return this.file;
        }

        public String[] getKeys() {
            return keys;
        }

        public static File getFileByKey(String key) {
            for (LocalStorageFile localStorageFile : values()) {
                for (String _key : localStorageFile.getKeys()) {
                    if (_key.equals(key) && (localStorageFile.getFile().getParentFile().exists() || localStorageFile.getFile().getParentFile().mkdirs())) {
                        return localStorageFile.getFile();
                    }
                }
            }
            return null;
        }
    }

    public static LocalStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalStorageImpl();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                return null;
            } catch (IOException ignored) {}
        }

        Map<String, Object> result = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            result = (Map<String, Object>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }

        return result;
    }

    private boolean writeFile(File file, Map<String, Object> data) {
        boolean result = false;
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(data);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Object readObject(String key) {
        return (this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key))) == null) ?
                null : Objects.requireNonNull(this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)))).get(key);
    }

    @Override
    public boolean writeObject(String key, Object object) {
        Map<String, Object> map = this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)));
        if (map == null) {
            map = new HashMap<>();
        }

        map.put(key, object);
        return this.writeFile(LocalStorageFile.getFileByKey(key), map);
    }

    @Override
    public boolean deleteObject(String key) {
        if (!Objects.requireNonNull(LocalStorageFile.getFileByKey(key)).getParentFile().mkdirs()) {
            return false;
        }
        Map<String, Object> map = this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)));
        if (map == null) {
            map = new HashMap<>();
        }

        map.remove(key);
        return this.writeFile(LocalStorageFile.getFileByKey(key), map);
    }

    @Override
    public boolean deleteAllData() {
        for (LocalStorageFile localStorageFile : LocalStorageFile.values()) {
            if (!this.writeFile(localStorageFile.getFile(), null)) {
                return false;
            }
        }
        return true;
    }
}
