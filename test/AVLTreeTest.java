import static org.junit.Assert.*;


import org.junit.*;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class AVLTreeTest {
    @Test
    public void testSimple() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(1);
        tree.add(2);
        tree.add(3);
        assertEquals(0, tree.root.balanceFactor());
        tree.add(4);
        tree.add(5);
        tree.add(6);
        assertEquals(0, tree.root.balanceFactor());
        for (int i = 1; i <= 6; i++) {
            assertTrue(tree.contains(i));
        }
    }

    @Test
    public void testRemoveRoot() {
        AVLTree<Integer> tree = new AVLTree<>();
        for (int i = 1; i <= 6; i++) {
            tree.add(i);
        }
        tree.remove(4);
        assertEquals(-1, tree.root.balanceFactor());
        tree.remove(5);
        assertEquals(1, tree.root.balanceFactor());
    }

    @Test
    public void testIterator() {
        AVLTree<Integer> tree = new AVLTree<>();
        TreeSet<Integer> set = new TreeSet<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Integer x = random.nextInt();
            assertEquals(tree.add(x), set.add(x));
        }
        assertArrayEquals(tree.toArray(), set.toArray());
    }

    @Test
    public void testSubSets() {
        AVLTree<Integer> set = new AVLTree<>();
        for (int i = 0; i < 1000; i++) {
            set.add(i);
        }
        SortedSet<Integer> tailSet = set.tailSet(999);
        assertEquals(1, tailSet.size());
        set.add(1000);
        assertTrue(tailSet.contains(1000));
        tailSet.add(1001);
        assertTrue(set.contains(1001));

        SortedSet<Integer> headSet = set.headSet(1);
        assertEquals(1, headSet.size());
        set.add(-1);
        assertTrue(headSet.contains(-1));
        headSet.add(-2);
        assertTrue(set.contains(-2));

        SortedSet<Integer> subSet = set.subSet(100, 200);
        for (int i = 100; i < 200; i++) {
            set.remove(i);
        }
        assertEquals(0, subSet.size());
        set.add(150);
        assertTrue(subSet.contains(150));
        subSet.add(160);
        assertTrue(set.contains(160));
    }
}
