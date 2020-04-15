package org.financer.client.local;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a local storage for Financer clients
 *
 * @author Raphael Müßeler
 */
public interface LocalStorage {

    /**
     * Reads an object from the local storage with the given key.
     *
     * @param key key
     * @param <T> deserialized object to return
     * @return object
     */
    <T extends Serializable> T readObject(String key);

    /**
     * Reads a list from the local storage
     *
     * @param key key
     * @param <T> generic type of the list
     * @return list
     */
    @SuppressWarnings("unchecked")
    default <T> List<T> readList(String key) {
        return (List<T>) this.readObject(key);
    }

    /**
     * Writes an object to the local storage with given key
     *
     * @param key    key
     * @param object object to write
     * @return true if operation was successful, false otherwise
     */
    boolean writeObject(String key, Serializable object);

    /**
     * Checks whether the local storage contains the given key
     *
     * @param key key
     * @return true if the local storage contains the given key, false otherwise
     */
    boolean contains(String key);

    /**
     * Deletes an object from the local storage.
     *
     * @param key key that will be deleted
     * @return true if operation was successful, false otherwise
     */
    boolean deleteObject(String key);

    /**
     * Deletes all data inside the local storage
     *
     * @return true if operation was successful, false otherwise
     */
    boolean deleteAllData();
}
