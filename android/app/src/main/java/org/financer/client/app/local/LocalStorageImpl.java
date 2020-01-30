package org.financer.client.app.local;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;

import org.financer.client.local.LocalStorage;

public class LocalStorageImpl implements LocalStorage {
    private static final String FILE_NAME = "org.financer.localstorage";

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
    public synchronized Serializable readObject(String key) {
        if (!Objects.requireNonNull(this.sharedPreferences.getString(key, "")).isEmpty()) {
            String string = this.sharedPreferences.getString(key, "");
            byte[] data = Base64.getDecoder().decode(string);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
                return (Serializable) objectInputStream.readObject();
            } catch(IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public synchronized boolean writeObject(String key, Serializable object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
            out.writeObject(object);
            return this.sharedPreferences.edit().putString(key,  Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public synchronized boolean deleteObject(String key) {
        return this.sharedPreferences.edit().remove(key).commit();
    }

    @Override
    public synchronized boolean deleteAllData() {
        return this.sharedPreferences.edit().clear().commit();
    }
}
