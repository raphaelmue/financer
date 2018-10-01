import de.raphaelmuesseler.financer.util.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RandomStringTest {
    @Test
    void testRandomStringLength() {
        final int length = 32;
        final RandomString randomString = new RandomString(length);

        Assertions.assertEquals(randomString.nextString().length(), length);
    }

    @Test
    void testRandomStringAlphabet() {
        final int length = 32;
        final RandomString randomString = new RandomString(length, RandomString.digits);

        final String string = randomString.nextString();

        for (int i = 0; i < RandomString.lower.length(); i++){
            char c = RandomString.lower.charAt(i);
            Assertions.assertFalse(string.indexOf(Character.toString(c)) > 0);
        }
    }
}
