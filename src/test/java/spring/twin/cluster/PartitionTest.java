package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Partition}.
 */
class PartitionTest {

    @Test
    @DisplayName("Constructor: each node in its own community")
    void testConstructor_eachNodeInOwnCommunity() {
        Set<String> nodes = Set.of("A", "B", "C");

        Partition partition = new Partition(nodes);

        assertEquals(3, partition.communityCount());
        Set<Integer> communities = partition.communities();
        assertEquals(3, communities.size());
        assertTrue(communities.contains(0));
        assertTrue(communities.contains(1));
        assertTrue(communities.contains(2));
    }

    @Test
    @DisplayName("Constructor: empty nodes creates empty partition")
    void testConstructor_emptyNodes_createsEmptyPartition() {
        Set<String> nodes = Set.of();

        Partition partition = new Partition(nodes);

        assertTrue(partition.isEmpty());
        assertEquals(0, partition.communityCount());
        assertTrue(partition.communities().isEmpty());
        assertTrue(partition.nodes().isEmpty());
    }

    @Test
    @DisplayName("communityOf returns correct community")
    void testCommunityOf_returnsCorrectCommunity() {
        Set<String> nodes = Set.of("A", "B", "C");
        Partition partition = new Partition(nodes);

        int communityA = partition.communityOf("A");
        int communityB = partition.communityOf("B");
        int communityC = partition.communityOf("C");

        assertTrue(partition.communities().contains(communityA));
        assertTrue(partition.communities().contains(communityB));
        assertTrue(partition.communities().contains(communityC));
    }

    @Test
    @DisplayName("moveNode changes community")
    void testMoveNode_changesCommunity() {
        Set<String> nodes = Set.of("A", "B");
        Partition partition = new Partition(nodes);

        int oldCommunity = partition.communityOf("A");
        int newCommunity = partition.communityOf("B");
        assertNotEquals(oldCommunity, newCommunity);

        partition.moveNode("A", newCommunity);

        assertEquals(newCommunity, partition.communityOf("A"));
    }

    @Test
    @DisplayName("moveNode to new community updates community count")
    void testMoveNode_updatesCommunityCount() {
        Set<String> nodes = Set.of("A", "B", "C");
        Partition partition = new Partition(nodes);
        int initialCount = partition.communityCount();

        int communityA = partition.communityOf("A");
        int communityB = partition.communityOf("B");
        int newCommunity = 100; // New community id

        partition.moveNode("A", newCommunity);

        assertEquals(initialCount + 1, partition.communityCount());
        assertEquals(newCommunity, partition.communityOf("A"));
        assertTrue(partition.communities().contains(newCommunity));
    }

    @Test
    @DisplayName("nodesInCommunity returns correct nodes")
    void testNodesInCommunity_returnsCorrectNodes() {
        Set<String> nodes = Set.of("A", "B", "C");
        Partition partition = new Partition(nodes);

        int communityA = partition.communityOf("A");
        Set<String> nodesInCommunity = partition.nodesInCommunity(communityA);

        assertEquals(1, nodesInCommunity.size());
        assertTrue(nodesInCommunity.contains("A"));
    }

    @Test
    @DisplayName("nodesInCommunity for empty community returns empty set")
    void testNodesInCommunity_emptyCommunity_returnsEmptySet() {
        Set<String> nodes = Set.of("A", "B");
        Partition partition = new Partition(nodes);

        Set<String> emptyCommunity = partition.nodesInCommunity(999);

        assertTrue(emptyCommunity.isEmpty());
    }

    @Test
    @DisplayName("communities returns all community ids")
    void testCommunities_returnsAllCommunityIds() {
        Set<String> nodes = Set.of("A", "B", "C", "D");
        Partition partition = new Partition(nodes);

        Set<Integer> communities = partition.communities();

        assertEquals(4, communities.size());
        assertTrue(communities.contains(0));
        assertTrue(communities.contains(1));
        assertTrue(communities.contains(2));
        assertTrue(communities.contains(3));
    }

    @Test
    @DisplayName("nodes returns all nodes")
    void testNodes_returnsAllNodes() {
        Set<String> nodes = Set.of("A", "B", "C");

        Partition partition = new Partition(nodes);

        Set<String> returnedNodes = partition.nodes();
        assertEquals(3, returnedNodes.size());
        assertTrue(returnedNodes.contains("A"));
        assertTrue(returnedNodes.contains("B"));
        assertTrue(returnedNodes.contains("C"));
    }

    @Test
    @DisplayName("communityCount returns correct count")
    void testCommunityCount_returnsCorrectCount() {
        Set<String> nodes = Set.of("A", "B", "C", "D", "E");
        Partition partition = new Partition(nodes);

        assertEquals(5, partition.communityCount());
    }

    @Test
    @DisplayName("isEmpty for empty partition returns true")
    void testIsEmpty_emptyPartition_returnsTrue() {
        Partition partition = new Partition(Set.of());

        assertTrue(partition.isEmpty());
    }

    @Test
    @DisplayName("isEmpty for non-empty partition returns false")
    void testIsEmpty_nonEmptyPartition_returnsFalse() {
        Partition partition = new Partition(Set.of("A"));

        assertFalse(partition.isEmpty());
    }

    @Test
    @DisplayName("copy creates deep copy independent from original")
    void testCopy_createsDeepCopy() {
        Set<String> nodes = Set.of("A", "B", "C");
        Partition original = new Partition(nodes);

        Partition copy = original.copy();

        // Copy should have same data
        assertEquals(original.communityCount(), copy.communityCount());
        assertEquals(original.nodes(), copy.nodes());

        // Modifying original should not affect copy
        int oldCommunityA = original.communityOf("A");
        int communityB = original.communityOf("B");
        original.moveNode("A", communityB);

        assertEquals(oldCommunityA, copy.communityOf("A"));
        assertEquals(3, copy.communityCount());
    }

    @Test
    @DisplayName("toCommunityMap returns correct mapping")
    void testToCommunityMap_returnsCorrectMapping() {
        Set<String> nodes = Set.of("A", "B", "C");
        Partition partition = new Partition(nodes);

        // Move A and B to same community
        int communityB = partition.communityOf("B");
        partition.moveNode("A", communityB);

        Map<Integer, Set<String>> communityMap = partition.toCommunityMap();

        assertEquals(2, communityMap.size());
        // One community should have A and B, another should have C
        boolean foundAB = false;
        boolean foundC = false;
        for (Set<String> communityNodes : communityMap.values()) {
            if (communityNodes.contains("A") && communityNodes.contains("B")) {
                foundAB = true;
                assertEquals(2, communityNodes.size());
            }
            if (communityNodes.contains("C")) {
                foundC = true;
                assertEquals(1, communityNodes.size());
            }
        }
        assertTrue(foundAB, "Should have community with A and B");
        assertTrue(foundC, "Should have community with C");
    }
}