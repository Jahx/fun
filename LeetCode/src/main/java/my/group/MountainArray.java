package my.group;

public class MountainArray {

    public boolean isMountainArray(int[] ints) {

        int left = 0;
        int n = ints.length;
        int right = n - 1;

        while (left < n - 1 && ints[left] < ints[left + 1]) {
            left++;
        }
        while (right > 0 && ints[right] < ints[right - 1]) {
            right--;
        }
        return left > 0 && right == left && right < n - 1;
    }
}
