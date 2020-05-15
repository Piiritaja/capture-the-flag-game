import Game.player.Flag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlagTest {

    private Flag flag;

    @BeforeEach
    void setUp() {
        flag = new Flag(0, 0, 10, 10, Flag.flagColor.RED);
    }

    @Test
    void getColor() {
        assertEquals(Flag.flagColor.RED, flag.getColor());
    }

    @Test
    void pickUp() {
        flag.pickUp();
        assertTrue(flag.isPickedUp());
    }

    @Test
    void drop() {
        flag.drop();
        assertFalse(flag.isPickedUp());
    }
}
