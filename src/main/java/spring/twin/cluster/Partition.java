package spring.twin.cluster;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Data model for partitioning a graph into communities (clusters).
 * Stores node membership in communities and provides manipulation methods.
 */
public class Partition {
    
    private Map<String, Integer> nodeCommunity;
    private int communityCount;
    
    /**
     * Creates a partition where each node is in its own community.
     * Initializes nodeCommunity so that each node gets a unique community number (0, 1, 2, ...).
     * communityCount is set to nodes.size().
     *
     * @param nodes set of nodes
     */
    public Partition(Set<String> nodes) {
        this.nodeCommunity = new HashMap<>();
        int id = 0;
        for (String node : nodes) {
            this.nodeCommunity.put(node, id++);
        }
        this.communityCount = nodes.size();
    }
    
    /**
     * Creates a partition from an existing mapping.
     * Copies the Map via new HashMap<>().
     *
     * @param nodeCommunity mapping of node → community
     * @param communityCount number of communities
     */
    public Partition(Map<String, Integer> nodeCommunity, int communityCount) {
        this.nodeCommunity = new HashMap<>(nodeCommunity);
        this.communityCount = communityCount;
    }
    
    /**
     * Returns the community number for a node.
     *
     * @param node node name
     * @return community number
     */
    public int communityOf(String node) {
        return nodeCommunity.get(node);
    }
    
    /**
     * Moves a node to a different community.
     * Updates nodeCommunity.put(node, newCommunity).
     * If newCommunity >= communityCount, updates communityCount = newCommunity + 1.
     *
     * @param node node name
     * @param newCommunity new community number
     */
    public void moveNode(String node, int newCommunity) {
        int oldCommunity = nodeCommunity.get(node);
        nodeCommunity.put(node, newCommunity);
        if (newCommunity >= communityCount) {
            communityCount = communityCount + 1;
        }
    }
    
    /**
     * Returns all nodes in the given community.
     * Filters nodeCommunity by community value, returns set of nodes.
     *
     * @param community community number
     * @return set of nodes
     */
    public Set<String> nodesInCommunity(int community) {
        Set<String> nodes = new HashSet<>();
        for (Map.Entry<String, Integer> entry : nodeCommunity.entrySet()) {
            if (entry.getValue() == community) {
                nodes.add(entry.getKey());
            }
        }
        return nodes;
    }
    
    /**
     * Returns a set of all community numbers.
     *
     * @return set of community numbers
     */
    public Set<Integer> communities() {
        return new HashSet<>(nodeCommunity.values());
    }
    
    /**
     * Returns a set of all nodes.
     *
     * @return set of nodes
     */
    public Set<String> nodes() {
        return nodeCommunity.keySet();
    }
    
    /**
     * Returns the number of communities.
     *
     * @return number of communities
     */
    public int communityCount() {
        return communityCount;
    }
    
    /**
     * Checks if the partition is empty.
     *
     * @return true if partition is empty
     */
    public boolean isEmpty() {
        return nodeCommunity.isEmpty();
    }
    
    /**
     * Creates a deep copy of the partition.
     * Creates a new Partition with a copy of nodeCommunity and the same communityCount.
     *
     * @return copy of the partition
     */
    public Partition copy() {
        return new Partition(this.nodeCommunity, this.communityCount);
    }
    
    /**
     * Returns a mapping: community number → set of nodes.
     * Groups nodes by community number in Map<Integer, Set<String>>.
     *
     * @return mapping of communities to nodes
     */
    public Map<Integer, Set<String>> toCommunityMap() {
        Map<Integer, Set<String>> communityMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : nodeCommunity.entrySet()) {
            communityMap.computeIfAbsent(entry.getValue(), k -> new HashSet<>()).add(entry.getKey());
        }
        return communityMap;
    }
}