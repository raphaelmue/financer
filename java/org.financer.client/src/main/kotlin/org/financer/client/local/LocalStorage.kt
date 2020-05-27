package org.financer.client.local

import java.io.Serializable

/**
 * Represents a local storage for Financer clients
 *
 * @author Raphael Müßeler
 */
interface LocalStorage {
    /**
     * Reads an object from the local storage with the given key.
     *
     * @param key key
     * @param <T> deserialized object to return
     * @return object
    </T> */
    fun <T : Serializable> readObject(key: String): T?

    /**
     * Reads a list from the local storage
     *
     * @param key key
     * @param <T> generic type of the list
     * @return list
    </T> */
    fun <T> readList(key: String): List<T> {
        return readObject<ArrayList<T>>(key) ?: emptyList()
    }

    /**
     * Writes an object to the local storage with given key
     *
     * @param key key
     * @param serializable object to write
     * @return true if operation was successful, false otherwise
     */
    fun writeObject(key: String, serializable: Serializable): Boolean

    /**
     * Checks whether the local storage contains the given key
     *
     * @param key key
     * @return true if the local storage contains the given key, false otherwise
     */
    operator fun contains(key: String): Boolean

    /**
     * Deletes an object from the local storage.
     *
     * @param key key that will be deleted
     * @return true if operation was successful, false otherwise
     */
    fun deleteObject(key: String): Boolean

    /**
     * Deletes all data inside the local storage
     *
     * @return true if operation was successful, false otherwise
     */
    fun deleteAllData(): Boolean
}