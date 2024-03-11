package my.group;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeadersInArrayTest {

    @Test
    void findLeaders() {
        LeadersInArray service = new LeadersInArray();

        int[] source = new int[] {1, 2, 3, 5, 10, 6};
        var expected = List.of(10, 6);
        int[] result = service.findLeaders(source);

        assertEquals(expected.size(), result.length);

        Arrays.stream(result).forEach(v -> {
            assertTrue(expected.contains(v));
        });
    }
}