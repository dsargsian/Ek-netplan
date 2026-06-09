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

    @Test
    @DisplayName("TC04 [ÄK4] – Einzelner Knoten ist Start- und Endknoten")
    void tc04_einzelnerKnotenIstStartUndEndknoten() {
        Knoten a = new Knoten(1, "A", 7);

        Netzplan netzplan = createNetzplan(a);

        assertDoesNotThrow(netzplan::calcPath);

        List<Knoten> pfad = netzplan.criticalPath();

        assertEquals(1, pfad.size());
        assertSame(a, pfad.get(0));
        assertEquals(7L, netzplan.getDuration());
    }
    @Test
    @DisplayName("TC05 [ÄK5] – Leerer Netzplan löst IllegalArgumentException aus")
    void tc05_leererNetzplanWirftException() {
        Netzplan netzplan = new Netzplan();

        assertThrows(IllegalArgumentException.class, netzplan::calcPath);
    }

    @Test
    @DisplayName("TC06 [ÄK6] – Knoten mit mehreren Vorgängern wird korrekt berechnet")
    void tc06_knotenMitMehrerenVorgaengern() {
        Knoten start = new Knoten(0, "Start", 0);
        Knoten a = new Knoten(1, "A", 3);
        Knoten b = new Knoten(2, "B", 5);
        Knoten c = new Knoten(3, "C", 2);

        a.addPredecessor(start);
        b.addPredecessor(start);
        c.addPredecessor(a);
        c.addPredecessor(b);

        Netzplan netzplan = createNetzplan(start, a, b, c);

        assertDoesNotThrow(netzplan::calcPath);

        assertEquals(2, c.getPredecessors().size());
        assertEquals(5L, c.getFaz());
    }


    @Test
    @DisplayName("TC07 [ÄK7] – Knoten mit mehreren Nachfolgern wird korrekt berechnet")
    void tc07_knotenMitMehrerenNachfolgern() {
        Knoten a = new Knoten(1, "A", 4);
        Knoten b = new Knoten(2, "B", 3);
        Knoten c = new Knoten(3, "C", 6);
        Knoten ende = new Knoten(4, "Ende", 0);

        b.addPredecessor(a);
        c.addPredecessor(a);
        ende.addPredecessor(b);
        ende.addPredecessor(c);

        Netzplan netzplan = createNetzplan(a, b, c, ende);

        assertDoesNotThrow(netzplan::calcPath);

        assertEquals(2, a.getSuccessors().size());
        assertEquals(10L, netzplan.getDuration());
    }

    @Test
    @DisplayName("TC08 [ÄK8] – Direkter Zirkelbezug löst IllegalArgumentException aus")
    void tc08_direkterZirkelbezugWirftException() {
        Knoten a = new Knoten(1, "A", 5);
        a.addPredecessor(a);

        Netzplan netzplan = createNetzplan(a);

        assertThrows(IllegalArgumentException.class, netzplan::calcPath);
    }

    @Test
    @DisplayName("TC09 [ÄK9] – Indirekter Zirkelbezug löst IllegalArgumentException aus")
    void tc09_indirekterZirkelbezugWirftException() {
        Knoten a = new Knoten(1, "A", 2);
        Knoten b = new Knoten(2, "B", 3);
        Knoten c = new Knoten(3, "C", 4);

        b.addPredecessor(a);
        c.addPredecessor(b);
        a.addPredecessor(c);

        Netzplan netzplan = createNetzplan(a, b, c);

        assertThrows(IllegalArgumentException.class, netzplan::calcPath);
    }

    @Test
    @DisplayName("TC10 [ÄK10] – Reihenfolge der Knoteneingabe beeinflusst das Ergebnis nicht")
    void tc10_eingabereihenfolgeOhneEinflussAufErgebnis() {
        linearerNetzplan.calcPath();

        Netzplan rueckwaerts = linearerNetzplanUmgekehrt();
        rueckwaerts.calcPath();

        assertEquals(
                linearerNetzplan.getDuration(),
                rueckwaerts.getDuration()
        );

        List<Knoten> pfad1 = linearerNetzplan.criticalPath();
        List<Knoten> pfad2 = rueckwaerts.criticalPath();

        assertEquals(pfad1.size(), pfad2.size());

        for (int i = 0; i < pfad1.size(); i++) {
            assertEquals(pfad1.get(i).getName(), pfad2.get(i).getName());
            assertEquals(pfad1.get(i).getFaz(), pfad2.get(i).getFaz());
            assertEquals(pfad1.get(i).getFez(), pfad2.get(i).getFez());
            assertEquals(pfad1.get(i).getSaz(), pfad2.get(i).getSaz());
            assertEquals(pfad1.get(i).getSez(), pfad2.get(i).getSez());
        }
    }


}