package de.raphaelmuesseler.financer.client.app.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.User;

public class LocalStorageImpl implements LocalStorage {
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

    private LocalStorageImpl() {
        sharedPreferences = CONTEXT.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void setContext(Context CONTEXT) {
        LocalStorageImpl.CONTEXT = CONTEXT;
    }


    @Override
    public Object readObject(String key) {
        if (Objects.requireNonNull(this.sharedPreferences.getString(USER_STORAGE_NAME, "")).isEmpty()) {
            return null;
        } else {
            return new GsonBuilder().create().fromJson(this.sharedPreferences.getString(key, ""), User.class);
        }
    }

    @Override
    public boolean writeObject(String key, Object object) {
        Gson gson = new GsonBuilder().create();
        return this.sharedPreferences.edit().putString(key, gson.toJson(object)).commit();
    }

    @Override
    public boolean deleteObject(String key) {
        return this.sharedPreferences.edit().remove(key).commit();
    }

    @Override
    public boolean deleteAllData() {
        return this.sharedPreferences.edit().clear().commit();
    }
}
