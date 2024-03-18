package my.group;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoveElementsTest {
    @Test
    void removeElement_withNoMatchingElements_returnsArrayLength() {
        RemoveElements service = new RemoveElements();
        int[] nums = {1, 2, 3, 4, 5};
        int result = service.removeElement(nums, 6);
        assertEquals(5, result);
    }

    @Test
    void removeElement_withAllMatchingElements_returnsZero() {
        RemoveElements service = new RemoveElements();
        int[] nums = {3, 3, 3, 3, 3};
        int result = service.removeElement(nums, 3);
        assertEquals(0, result);
    }

    @Test
    void removeElement_withSomeMatchingElements_returnsCorrectCount() {
        RemoveElements service = new RemoveElements();
        int[] nums = {1, 2, 3, 2, 1};
        int result = service.removeElement(nums, 2);
        assertEquals(3, result);
    }

    @Test
    void removeElement_withEmptyArray_returnsZero() {
        RemoveElements service = new RemoveElements();
        int[] nums = {};
        int result = service.removeElement(nums, 1);
        assertEquals(0, result);
    }
}