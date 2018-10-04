package de.raphaelmuesseler.financer.client.app.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.Map;

import de.raphaelmuesseler.financer.client.local.AbstractLocalStorage;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.shared.model.User;

public class LocalStorageImpl extends AbstractLocalStorage {
    private static final String FILE_NAME = "de.raphaelmuesseler.financer.localstorage";
    private static final String USER_STORAGE_NAME = "user";

    private static LocalStorageImpl INSTANCE;
    private static Context CONTEXT = null;

    private final SharedPreferences sharedPreferences;

    public static LocalStorageImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalStorageImpl();
        }
        return INSTANCE;
    }

    LocalStorageImpl() {
        sharedPreferences = CONTEXT.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void setContext(Context CONTEXT) {
        LocalStorageImpl.CONTEXT = CONTEXT;
    }

    @Override
    public User getLoggedInUser() {
        if (this.sharedPreferences.getString(USER_STORAGE_NAME, "").isEmpty()) {
            return null;
        } else {
            return new GsonBuilder().create().fromJson(this.sharedPreferences.getString(USER_STORAGE_NAME, ""), User.class);
        }
    }

    @Override
    public boolean writeUser(User user) {
        Gson gson = new GsonBuilder().create();
        return this.sharedPreferences.edit().putString(USER_STORAGE_NAME, gson.toJson(user)).commit();
    }

    @Override
    public boolean logUserOut() {
        return false;
    }

    @Override
    public Settings getSettings() {
        return null;
    }

    @Override
    public boolean writeSettings(Settings settings) {
        return false;
    }

    @Override
    public Object readObject(File file, String s) {
        return null;
    }

    @Override
    protected Map<String, Object> readFile(File file) {
        return null;
    }

    @Override
    public boolean writeObject(File file, String s, Object o) {
        return false;
    }
}
