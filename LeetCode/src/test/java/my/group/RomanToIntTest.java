package my.group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class RomanToIntTest {

    @ParameterizedTest
    @CsvSource({
            "I, 1",
            "II, 2",
            "III, 3",
            "IV, 4",
            "V, 5",
            "VI, 6",
            "VII, 7",
            "VIII, 8",
            "IX, 9",
            "X, 10",
            "L, 50",
            "C, 100",
            "D, 500",
            "M, 1000",
            "MCM, 1900",
            "MCMXLIX, 1949"
    })
    void romanToInt(String roman, int expected) {
        RomanToInt service = new RomanToInt();
        assertEquals(expected, service.romanToInt(roman));
    }

    @Test
    void romanToInt_withEmptyString_returnsZero() {
        RomanToInt service = new RomanToInt();
        assertEquals(0, service.romanToInt(""));
    }

    @Test
    void romanToInt_withInvalidCharacter_throwsException() {
        RomanToInt service = new RomanToInt();
        assertThrows(IllegalArgumentException.class, () -> service.romanToInt("Z"));
    }

    @Test
    void romanToInt_withLowercaseRomanNumerals_returnsCorrectValue() {
        RomanToInt service = new RomanToInt();
        assertEquals(1, service.romanToInt("I"));
    }

    @Test
    void romanToInt_withMixedCaseRomanNumerals_returnsCorrectValue() {
        RomanToInt service = new RomanToInt();
        assertEquals(4, service.romanToInt("IV"));
    }

    @Test
    void romanToInt_withMultipleSameRomanNumerals_returnsCorrectValue() {
        RomanToInt service = new RomanToInt();
        assertEquals(30, service.romanToInt("XXX"));
    }

    @Test
    void romanToInt_withComplexRomanNumerals_returnsCorrectValue() {
        RomanToInt service = new RomanToInt();
        assertEquals(1987, service.romanToInt("MCMLXXXVII"));
    }
}