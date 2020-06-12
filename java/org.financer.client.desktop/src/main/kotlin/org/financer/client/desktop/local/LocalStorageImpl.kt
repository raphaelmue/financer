package org.financer.client.desktop.local

import org.financer.client.local.LocalStorage
import java.io.*
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class LocalStorageImpl : LocalStorage {
    private val logger = Logger.getLogger("FinancerApplication")

    enum class LocalStorageFile(path: String, vararg keys: String) {
        USERDATA("/usr/usr.fnc", "user"),
        SETTINGS("/usr/localSettings.fnc", "host"),
        TRANSACTIONS("/data/transactions.fnc", "transactions", "fixedTransactions"),
        CATEGORIES("/data/categories.fnc", "categories");

        private val path: String
        val file: File
        val keys: Array<String>

        init {
            if (System.getenv("APPDATA") != null) {
                this.path = System.getenv("APPDATA") + "/Financer" + path
            } else {
                this.path = System.getProperty("user.home") + "/Financer" + path
            }
            this.file = File(this.path)
            this.keys = keys as Array<String>
        }

        companion object {
            fun getFileByKey(key: String): File {
                for (localStorageFile in values()) {
                    for (_key in localStorageFile.keys) {
                        if (_key == key && (localStorageFile.file.parentFile.exists() || localStorageFile.file.parentFile.mkdirs())) {
                            return localStorageFile.file
                        }
                    }
                }
                throw IllegalArgumentException("Key not found!")
            }
        }
    }

    @Synchronized
    private fun readFile(file: File): Map<String, Serializable> {
        if (!file.exists()) {
            writeFile(file, emptyMap())
            return emptyMap()
        }
        var result: Map<String, Serializable>? = null
        try {
            ObjectInputStream(FileInputStream(file)).use { inputStream -> result = inputStream.readObject() as Map<String, Serializable> }
        } catch (e: IOException) {
            logger.log(Level.SEVERE, e.message, e)
        } catch (e: ClassNotFoundException) {
            logger.log(Level.SEVERE, e.message, e)
        }
        return result ?: emptyMap()
    }

    @Synchronized
    private fun writeFile(file: File, data: Map<String, Serializable>): Boolean {
        var result = false
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        try {
            ObjectOutputStream(FileOutputStream(file)).use { outputStream ->
                outputStream.writeObject(data)
                result = true
            }
        } catch (e: IOException) {
            logger.log(Level.SEVERE, e.message, e)
        }
        return result
    }

    override fun <T : Serializable> readObject(key: String): T? {
        val result = readFile(LocalStorageFile.getFileByKey(key))[key]
        if (result != null) {
            return result as T
        }
        return null
    }

    @Synchronized
    override fun writeObject(key: String, serializable: Serializable): Boolean {
        val map = this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)))
        val updatedMap = map.toMutableMap()
        updatedMap[key] = serializable
        return this.writeFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)), updatedMap)
    }

    @Synchronized
    override fun deleteObject(key: String): Boolean {
        val map = this.readFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)))
        val updatedMap = map.toMutableMap()
        updatedMap.remove(key)
        return this.writeFile(Objects.requireNonNull(LocalStorageFile.getFileByKey(key)), updatedMap)
    }

    @Synchronized
    override fun deleteAllData(): Boolean {
        for (localStorageFile in LocalStorageFile.values()) {
            localStorageFile.file.delete()
        }
        logger.log(Level.INFO, "Deleted all local data successfully.")
        return true
    }

    override fun contains(key: String): Boolean {
        return readObject<Serializable>(key) != null
    }

    companion object {
        private var instance: LocalStorageImpl? = null

        @Synchronized
        fun getInstance(): LocalStorage {
            if (instance == null) {
                instance = LocalStorageImpl()
            }
            return instance as LocalStorageImpl
        }
    }
}