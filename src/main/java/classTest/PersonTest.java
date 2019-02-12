package classTest;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PersonTest {

    @Test
    public void testAge() {
        Person p = new Person("Someone", 13);
        assertFalse(p.isAdult());
    }
}
