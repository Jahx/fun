package my.group;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MergeSortedArrayTest {
    @Test
    void mergeSorted_withNonEmptyArrays_returnsMergedArray() {
        MergeSortedArray service = new MergeSortedArray();
        int[] nums1 = {1, 2, 3, 0, 0, 0};
        int[] nums2 = {2, 5, 6};
        service.mergeSorted(nums1, nums2, 3, 3);
        assertArrayEquals(new int[]{1, 2, 2, 3, 5, 6}, nums1);
    }

    @Test
    void mergeSorted_withEmptySecondArray_returnsFirstArray() {
        MergeSortedArray service = new MergeSortedArray();
        int[] nums1 = {1, 0};
        int[] nums2 = {};
        service.mergeSorted(nums1, nums2, 1, 0);
        assertArrayEquals(new int[]{1, 0}, nums1);
    }

    @Test
    void mergeSorted_withEmptyFirstArray_returnsSecondArray() {
        MergeSortedArray service = new MergeSortedArray();
        int[] nums1 = {0};
        int[] nums2 = {1};
        service.mergeSorted(nums1, nums2, 0, 1);
        assertArrayEquals(new int[]{1}, nums1);
    }

    @Test
    void mergeSorted_withNegativeNumbers_returnsMergedArray() {
        MergeSortedArray service = new MergeSortedArray();
        int[] nums1 = {-3, -2, 0, 0, 0};
        int[] nums2 = {-1, 0, 1};
        service.mergeSorted(nums1, nums2, 2, 3);
        assertArrayEquals(new int[]{-3, -2, -1, 0, 1}, nums1);
    }
}