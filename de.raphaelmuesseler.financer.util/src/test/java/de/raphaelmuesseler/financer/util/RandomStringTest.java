package de.raphaelmuesseler.financer.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
@Tag("unit")
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
        final RandomString randomString = new RandomString(length, RandomString.DIGITS);

        final String string = randomString.nextString();

        for (int i = 0; i < RandomString.LOWER.length(); i++){
            char c = RandomString.LOWER.charAt(i);
            Assertions.assertFalse(string.indexOf(Character.toString(c)) > 0);
        }
    }
}
