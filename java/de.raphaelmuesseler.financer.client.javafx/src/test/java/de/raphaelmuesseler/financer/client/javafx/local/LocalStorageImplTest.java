package de.raphaelmuesseler.financer.client.javafx.local;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.util.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
@Tag("unit")
public class LocalStorageImplTest {

    private final LocalStorage localStorage = LocalStorageImpl.getInstance();

    @BeforeEach
    public void setup() {
        localStorage.deleteAllData();
    }


    @Test
    public void writeAndReadObject() {
        final String serializableObject = new RandomString(64).nextString();
        final String key = "user";

        localStorage.writeObject(key, serializableObject);

        Assertions.assertEquals(serializableObject, localStorage.readObject(key));
    }

    @Test
    public void deleteObject() {
        final String serializableObject = new RandomString(64).nextString();
        final String key = "user";

        localStorage.writeObject(key, serializableObject);
        localStorage.deleteObject(key);
        Assertions.assertNull(localStorage.readObject(key));
    }

    @Test
    public void deleteAllData() {
        final String serializableObject = new RandomString(64).nextString();
        final String key = "user";

        localStorage.writeObject(key, serializableObject);
        localStorage.deleteAllData();
        Assertions.assertNull(localStorage.readObject(key));
    }

    @Test
    public void testReadList() {
        final RandomString randomString = new RandomString(64);
        final ArrayList<String> stringList = new ArrayList<>();
        final String key = "user";

        for (int i = 0; i < 5; i++) {
            stringList.add(randomString.nextString());
        }

        localStorage.writeObject(key, stringList);

        Assertions.assertEquals(stringList, localStorage.readList(key));
    }
}