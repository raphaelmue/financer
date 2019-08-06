package de.raphaelmuesseler.financer.client.javafx.local;

import de.raphaelmuesseler.financer.client.local.LocalStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalStorageImpl implements LocalStorage {
    private static LocalStorageImpl instance = null;

    private final Logger logger = Logger.getLogger("FinancerApplication");

    public enum LocalStorageFile {
        USERDATA("/usr/usr.fnc", "user"),
        SETTINGS("/usr/localSettings.fnc", "localSettings", "requests"),
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

    public static synchronized LocalStorage getInstance() {
        if (instance == null) {
            instance = new LocalStorageImpl();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private synchronized Map<String, Serializable> readFile(File file) {
        if (!file.exists()) {
            try {
                return file.getParentFile().mkdirs() && file.createNewFile() ? null : null;
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        Map<String, Serializable> result = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            result = (Map<String, Serializable>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }

    private synchronized boolean writeFile(File file, Map<String, Serializable> data) {
        boolean result = false;
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(data);
            result = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    @Override
    public synchronized Serializable readObject(String key) {
        return (this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key))) == null) ?
                null : Objects.requireNonNull(this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)))).get(key);
    }

    @Override
    public synchronized boolean writeObject(String key, Serializable object) {
        Map<String, Serializable> map = this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)));
        if (map == null) {
            map = new HashMap<>();
        }

        map.put(key, object);
        return this.writeFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)), map);
    }

    @Override
    public synchronized boolean deleteObject(String key) {
        if (LocalStorageFile.getFileByKey(key) == null) {
            return false;
        }
        Map<String, Serializable> map = this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)));
        if (map == null) {
            map = new HashMap<>();
        }

        map.remove(key);
        return this.writeFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)), map);
    }

    @Override
    public synchronized boolean deleteAllData() {
        for (LocalStorageFile localStorageFile : LocalStorageFile.values()) {
            if (!this.writeFile(localStorageFile.getFile(), null)) {
                return false;
            }
        }
        return true;
    }
}
