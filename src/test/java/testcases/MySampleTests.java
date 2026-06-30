package testcases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import utils.RetryExtension;

@ExtendWith(RetryExtension.class)
public class MySampleTests {

    private static int Count = 0;

    @Test
    public void testAlwaysPasses() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testFlakeButEventuallyPasses() {
        Count++;
        if (Count < 2) {
            // Fails on first attempt, will pass on the first retry
            Assertions.fail("Simulated failure!");
        }
        Assertions.assertTrue(true);
    }


}