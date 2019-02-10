package classTest;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class PersonTest {

    @Test
    public void testAge() {
        Person p = new Person("Someone", 13);
        assertFalse(p.isAdult());
    }
}
