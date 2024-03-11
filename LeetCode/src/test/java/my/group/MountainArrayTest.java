package my.group;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MountainArrayTest {

    @Test
    void testMountainArray() {
        MountainArray service = new MountainArray();
        assertTrue(service.isMountainArray(new int[] {1, 2, 4, 3, 2}));
        assertFalse(service.isMountainArray(new int[] {1, 2, 4, 3, 7}));
    }
}