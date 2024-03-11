package my.group;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnagramsTest {
    @Test
    void isAnagrams_withEmptyStrings_returnsTrue() {
        Anagrams service = new Anagrams();
        assertTrue(service.isAnagrams("", ""));
    }

    @Test
    void isAnagrams_withIdenticalStrings_returnsTrue() {
        Anagrams service = new Anagrams();
        assertTrue(service.isAnagrams("word", "word"));
    }

    @Test
    void isAnagrams_withAnagrams_returnsTrue() {
        Anagrams service = new Anagrams();
        assertTrue(service.isAnagrams("listen", "silent"));
    }

    @Test
    void isAnagrams_withNonAnagrams_returnsFalse() {
        Anagrams service = new Anagrams();
        assertFalse(service.isAnagrams("hello", "world"));
    }

    @Test
    void isAnagrams_withDifferentCaseAnagrams_returnsTrue() {
        Anagrams service = new Anagrams();
        assertTrue(service.isAnagrams("Listen", "Silent", true));
    }

    @Test
    void isAnagrams_withDifferentLengthStrings_returnsFalse() {
        Anagrams service = new Anagrams();
        assertFalse(service.isAnagrams("short", "longer"));
    }
}