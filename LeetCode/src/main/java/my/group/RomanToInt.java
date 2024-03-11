package my.group;

public class RomanToInt {

    public int romanToInt(String roman) {
        char[] romans = roman.toCharArray();
        int res = 0;
        for (int i = 0; i < romans.length; i++) {
            int v1 = toInt(romans[i]);
            if (i + 1 < romans.length) {
                int v2 = toInt(romans[i + 1]);
                if (v1 >= v2) {
                    res += v1;
                } else {
                    res = res + v2 - v1;
                    i++;
                }
            } else {
                res += v1;
            }
        }
        return res;
    }

    private int toInt(char c) {
        return switch (c) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            case 'D' -> 500;
            case 'M' -> 1000;
            default -> throw new IllegalArgumentException();
        };
    }
}
