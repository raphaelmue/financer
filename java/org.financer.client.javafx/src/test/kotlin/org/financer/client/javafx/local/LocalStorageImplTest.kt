package org.financer.client.javafx.local

import org.financer.util.RandomString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*

@Tag("unit")
class LocalStorageImplTest {
    private val localStorage = LocalStorageImpl.getInstance() as LocalStorageImpl

    @BeforeEach
    fun setup() {
        localStorage.deleteAllData()
    }

    @Test
    fun writeAndReadObject() {
        val serializableObject = RandomString(64).nextString()
        val key = "user"
        localStorage.writeObject(key, serializableObject)
        Assertions.assertEquals(serializableObject, localStorage.readObject(key))
    }

    @Test
    fun deleteObject() {
        val serializableObject = RandomString(64).nextString()
        val key = "user"
        localStorage.writeObject(key, serializableObject)
        localStorage.deleteObject(key)
        Assertions.assertNull(localStorage.readObject(key))
    }

    @Test
    fun deleteAllData() {
        val serializableObject = RandomString(64).nextString()
        val key = "user"
        localStorage.writeObject(key, serializableObject)
        localStorage.deleteAllData()
        Assertions.assertNull(localStorage.readObject(key))
    }

    @Test
    fun testReadList() {
        val randomString = RandomString(64)
        val stringList = ArrayList<String>()
        val key = "user"
        for (i in 0..4) {
            stringList.add(randomString.nextString())
        }
        localStorage.writeObject(key, stringList)
        Assertions.assertEquals(stringList, localStorage.readList<Any>(key))
    }
}