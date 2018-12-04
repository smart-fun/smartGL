package fr.arnaudguyon.smartgl.tools;

public class Assert {

    public static void assertTrue(boolean condition) {
        assertTrue("", condition);
    }
    public static void assertTrue(String text, boolean condition) {
        if (!condition) {
            doAssert(text);
        }
    }
    public static void assertNotNull(Object object) {
        assertNotNull("", object);
    }
    public static void assertNotNull(String text, Object object) {
        if (object == null) {
            doAssert(text);
        }
    }

    public static void assertEquals(Object object1, Object object2) {
        if (object1 == null) {
            if (object2 != null) {
                doAssert("");
            }
        } else {
            if (object2 == null) {
                doAssert("");
            } else {
                if (!object1.equals(object2)) {
                    doAssert("");
                }
            }
        }
    }

    private static void doAssert(String text) {
        throw new RuntimeException(text);
    }
}
