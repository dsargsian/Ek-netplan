import org.junit.jupiter.api.*;
import tgm.itp.netplan.Knoten;
import tgm.itp.netplan.Netzplan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Netzplan-Tests")
class NetzplanTest {

    private Knoten a;
    private Knoten b;
    private Knoten c;
    private Netzplan linearerNetzplan;

    @BeforeEach
    void setUp() {
        a = new Knoten(1, "A", 5);
        b = new Knoten(2, "B", 3);
        c = new Knoten(3, "C", 2);

        b.addPredecessor(a);
        c.addPredecessor(b);

        linearerNetzplan = createNetzplan(a, b, c);
    }

    private Netzplan createNetzplan(Knoten... knoten) {
        Netzplan netzplan = new Netzplan();
        for (Knoten k : knoten) {
            netzplan.addNode(k);
        }
        return netzplan;
    }

    private Netzplan linearerNetzplanUmgekehrt() {
        Knoten a = new Knoten(1, "A", 5);
        Knoten b = new Knoten(2, "B", 3);
        Knoten c = new Knoten(3, "C", 2);

        b.addPredecessor(a);
        c.addPredecessor(b);

        return createNetzplan(c, b, a);
    }

    @Test
    @DisplayName("TC01 [ÄK1] – Linearer Netzplan hat genau einen Start- und einen Endknoten")
    void tc01_linearerNetzplanHatGenauEinenStartUndEndknoten() {
        linearerNetzplan.calcPath();

        List<Knoten> pfad = linearerNetzplan.criticalPath();

        assertNotNull(pfad);
        assertFalse(pfad.isEmpty());

        assertTrue(pfad.get(0).getPredecessors().isEmpty());

        Knoten letzter = pfad.get(pfad.size() - 1);
        assertTrue(letzter.getSuccessors().isEmpty());
    }
    @Test
    @DisplayName("TC01 [ÄK1] – Gesamtdauer eines linearen Netzplans beträgt 10")
    void tc01_gesamtdauerLinearerNetzplan() {
        linearerNetzplan.calcPath();

        assertEquals(10L, linearerNetzplan.getDuration());
    }

    @Test
    @DisplayName("TC02 [ÄK2] – Zwei Startknoten lösen IllegalArgumentException aus")
    void tc02_zweiStartknotenWerfenException() {
        Knoten a = new Knoten(1, "A", 3);
        Knoten b = new Knoten(2, "B", 4);
        Knoten c = new Knoten(3, "C", 2);

        c.addPredecessor(a);
        c.addPredecessor(b);

        Netzplan netzplan = createNetzplan(a, b, c);

        assertThrows(IllegalArgumentException.class, netzplan::calcPath);
    }
    @Test
    @DisplayName("TC03 [ÄK3] – Zwei Endknoten lösen IllegalArgumentException aus")
    void tc03_zweiEndknotenWerfenException() {
        Knoten a = new Knoten(1, "A", 3);
        Knoten b = new Knoten(2, "B", 2);
        Knoten c = new Knoten(3, "C", 4);

        b.addPredecessor(a);
        c.addPredecessor(a);

        Netzplan netzplan = createNetzplan(a, b, c);

        assertThrows(IllegalArgumentException.class, netzplan::calcPath);
    }


}