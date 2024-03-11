package my.group;

import java.util.LinkedList;

public class LeadersInArray {

    public int[] findLeaders(int[] source) {

        var result = new LinkedList<Integer>();

        int maxFromRight = source[source.length - 1];
        result.addLast(maxFromRight);

        for (int i = source.length - 2; i >= 0; i = i - 1) {
            if (maxFromRight < source[i]) {
                maxFromRight = source[i];
                result.addFirst(maxFromRight);
            }
        }

        return result.stream().mapToInt(v -> v).toArray();
    }
}
