package my.group;

import java.util.HashMap;
import java.util.Map;

public class Anagrams {

    public boolean isAnagrams(String w1, String w2) {
        return isAnagrams(w1, w2, false);
    }

    public boolean isAnagrams(String w1, String w2, boolean ignoreCase) {

        Map<Character, Integer> frequency = new HashMap<>(Math.max(w1.length(), w2.length()));

        if (ignoreCase) {
            w1 = w1.toLowerCase();
            w2 = w2.toLowerCase();
        }

        for (char c : w1.toCharArray()) {
            if (frequency.containsKey(c)) {
                frequency.put(c, frequency.get(c) + 1);
            } else {
                frequency.put(c, 1);
            }
        }

        for (char c: w2.toCharArray()) {
            if (frequency.containsKey(c)) {
                var nF = frequency.get(c) - 1;
                if (nF == 0) {
                    frequency.remove(c);
                } else {
                    frequency.put(c, nF);
                }
            } else {
                return false;
            }
        }

        return frequency.isEmpty();
    }

}
