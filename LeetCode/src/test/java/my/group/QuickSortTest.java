package my.group;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class QuickSortTest {

    @Test
    void quickSort() {
        QuickSort service = new QuickSort();
        int[] arr = { 12, 11, 13, 5, 6, 7 };
        service.quickSort(arr, 0, arr.length - 1);
        int[] expected = { 5, 6, 7, 11, 12, 13 };
        assertArrayEquals(expected, arr);
    }

    @Test
    void quickSort_emptyInput() {
        QuickSort service = new QuickSort();
        int[] arr = { };
        service.quickSort(arr, 0, arr.length - 1);
        int[] expected = { };
        assertArrayEquals(expected, arr);
    }

}