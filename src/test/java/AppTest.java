import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    public void test_fillParams() {
        System.setProperty("rootPath", "/home");
        System.setProperty("depth", "3");
        System.setProperty("mask", "*.doc*");
        assertTrue(App.fillParams());

        System.clearProperty("rootPath");
        assertFalse(App.fillParams());

        System.setProperty("rootPath", "/home");
        System.setProperty("depth", "-3");
        assertFalse(App.fillParams());

        System.setProperty("depth", "3");
        System.setProperty("mask", "");
        assertFalse(App.fillParams());
    }    
}
