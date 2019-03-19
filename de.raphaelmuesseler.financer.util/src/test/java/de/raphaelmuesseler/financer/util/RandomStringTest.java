package de.raphaelmuesseler.financer.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
public class RandomStringTest {
    @Test
    public void testRandomStringLength() {
        final int length = 32;
        final RandomString randomString = new RandomString(length);

        Assertions.assertEquals(randomString.nextString().length(), length);
    }

    @Test
    public void testRandomStringAlphabet() {
        final int length = 32;
        final RandomString randomString = new RandomString(length, RandomString.digits);

        final String string = randomString.nextString();

        for (int i = 0; i < RandomString.lower.length(); i++){
            char c = RandomString.lower.charAt(i);
            Assertions.assertFalse(string.indexOf(Character.toString(c)) > 0);
        }
    }
}
