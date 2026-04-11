package spring.twin.scan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InnerClassMerger}.
 */
class InnerClassMergerTest {

    // ==================== isInnerClass tests ====================

    @Test
    @DisplayName("isInnerClass: with dollar sign returns true")
    void testIsInnerClass_withDollarSign_returnsTrue() {
        assertTrue(InnerClassMerger.isInnerClass("com.example.Outer$Inner"));
    }

    @Test
    @DisplayName("isInnerClass: without dollar sign returns false")
    void testIsInnerClass_withoutDollarSign_returnsFalse() {
        assertFalse(InnerClassMerger.isInnerClass("com.example.Outer"));
    }

    @Test
    @DisplayName("isInnerClass: multiple dollar signs returns true")
    void testIsInnerClass_multipleDollarSigns_returnsTrue() {
        assertTrue(InnerClassMerger.isInnerClass("com.example.Outer$Inner$Deep"));
    }

    @Test
    @DisplayName("isInnerClass: dollar at start returns false")
    void testIsInnerClass_dollarAtStart_returnsFalse() {
        assertFalse(InnerClassMerger.isInnerClass("$Proxy"));
    }

    @Test
    @DisplayName("isInnerClass: null input returns false")
    void testIsInnerClass_nullInput_returnsFalse() {
        assertFalse(InnerClassMerger.isInnerClass(null));
    }

    @Test
    @DisplayName("isInnerClass: empty string returns false")
    void testIsInnerClass_emptyString_returnsFalse() {
        assertFalse(InnerClassMerger.isInnerClass(""));
    }

    // ==================== getOuterClassName tests ====================

    @Test
    @DisplayName("getOuterClassName: inner class returns outer name")
    void testGetOuterClassName_innerClass_returnsOuterName() {
        assertEquals("com.example.Outer", InnerClassMerger.getOuterClassName("com.example.Outer$Inner"));
    }

    @Test
    @DisplayName("getOuterClassName: nested inner class returns top level outer")
    void testGetOuterClassName_nestedInnerClass_returnsTopLevelOuter() {
        assertEquals("com.example.Outer", InnerClassMerger.getOuterClassName("com.example.Outer$Inner$Deep"));
    }

    @Test
    @DisplayName("getOuterClassName: not inner class returns same name")
    void testGetOuterClassName_notInnerClass_returnsSameName() {
        assertEquals("com.example.Outer", InnerClassMerger.getOuterClassName("com.example.Outer"));
    }

    @Test
    @DisplayName("getOuterClassName: null input returns null")
    void testGetOuterClassName_nullInput_returnsNull() {
        assertNull(InnerClassMerger.getOuterClassName(null));
    }

    @Test
    @DisplayName("getOuterClassName: dollar at start returns same name")
    void testGetOuterClassName_dollarAtStart_returnsSameName() {
        assertEquals("$Proxy", InnerClassMerger.getOuterClassName("$Proxy"));
    }

    // ==================== mergeInnerClasses tests ====================

    @Test
    @DisplayName("mergeInnerClasses: merges inner into outer")
    void testMergeInnerClasses_mergesInnerIntoOuter() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer", new TreeSet<>(Set.of("dep1")));
        graph.put("Outer$Inner", new TreeSet<>(Set.of("dep2")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        assertEquals(Set.of("Outer"), result.keySet());
        assertEquals(Set.of("dep1", "dep2"), result.get("Outer"));
        assertFalse(result.containsKey("Outer$Inner"));
    }

    @Test
    @DisplayName("mergeInnerClasses: outer not in graph creates outer entry")
    void testMergeInnerClasses_outerNotInGraph_createsOuterEntry() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer$Inner", new TreeSet<>(Set.of("dep1")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        assertEquals(Set.of("Outer"), result.keySet());
        assertEquals(Set.of("dep1"), result.get("Outer"));
    }

    @Test
    @DisplayName("mergeInnerClasses: no inner classes returns same graph")
    void testMergeInnerClasses_noInnerClasses_returnsSameGraph() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Class1", new TreeSet<>(Set.of("dep1")));
        graph.put("Class2", new TreeSet<>(Set.of("dep2")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        assertEquals(Set.of("Class1", "Class2"), result.keySet());
        assertEquals(Set.of("dep1"), result.get("Class1"));
        assertEquals(Set.of("dep2"), result.get("Class2"));
    }

    @Test
    @DisplayName("mergeInnerClasses: nested inner class merges to top level")
    void testMergeInnerClasses_nestedInnerClass_mergesToTopLevel() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer$Inner$Deep", new TreeSet<>(Set.of("dep1")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        assertEquals(Set.of("Outer"), result.keySet());
        assertEquals(Set.of("dep1"), result.get("Outer"));
    }

    @Test
    @DisplayName("mergeInnerClasses: multiple inner classes merges all to outer")
    void testMergeInnerClasses_multipleInnerClasses_mergesAllToOuter() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer", new TreeSet<>(Set.of("dep1")));
        graph.put("Outer$Inner1", new TreeSet<>(Set.of("dep2")));
        graph.put("Outer$Inner2", new TreeSet<>(Set.of("dep3")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        assertEquals(Set.of("Outer"), result.keySet());
        assertEquals(Set.of("dep1", "dep2", "dep3"), result.get("Outer"));
    }

    @Test
    @DisplayName("mergeInnerClasses: inner class depends on outer, outer not in deps")
    void testMergeInnerClasses_innerClassDependsOnOuter_outerNotInDeps() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer", new TreeSet<>(Set.of("dep1")));
        graph.put("Outer$Inner", new TreeSet<>(Set.of("Outer", "dep2")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        assertEquals(Set.of("Outer"), result.keySet());
        // Outer should not contain self-reference
        assertEquals(Set.of("dep1", "dep2"), result.get("Outer"));
    }

    @Test
    @DisplayName("mergeInnerClasses: empty graph returns empty map")
    void testMergeInnerClasses_emptyGraph_returnsEmptyMap() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mergeInnerClasses: result is sorted")
    void testMergeInnerClasses_resultIsSorted() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("ZClass", new TreeSet<>(Set.of("b", "a")));
        graph.put("AClass", new TreeSet<>(Set.of("d", "c")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        // Keys should be sorted
        String[] keys = result.keySet().toArray(new String[0]);
        assertEquals("AClass", keys[0]);
        assertEquals("ZClass", keys[1]);

        // Values should be sorted
        String[] values = result.get("AClass").toArray(new String[0]);
        assertEquals("c", values[0]);
        assertEquals("d", values[1]);
    }

    @Test
    @DisplayName("mergeInnerClasses: with merge true performs merge")
    void testMergeInnerClasses_withMergeTrue_performsMerge() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer", new TreeSet<>(Set.of("dep1")));
        graph.put("Outer$Inner", new TreeSet<>(Set.of("dep2")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph, true);

        assertEquals(Set.of("Outer"), result.keySet());
        assertEquals(Set.of("dep1", "dep2"), result.get("Outer"));
    }

    @Test
    @DisplayName("mergeInnerClasses: with merge false returns original graph")
    void testMergeInnerClasses_withMergeFalse_returnsOriginalGraph() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer", new TreeSet<>(Set.of("dep1")));
        graph.put("Outer$Inner", new TreeSet<>(Set.of("dep2")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph, false);

        // Should return original graph unchanged
        assertEquals(Set.of("Outer", "Outer$Inner"), result.keySet());
        assertEquals(Set.of("dep1"), result.get("Outer"));
        assertEquals(Set.of("dep2"), result.get("Outer$Inner"));
    }

    @Test
    @DisplayName("mergeInnerClasses: dollar in dependency name remapped to outer")
    void testMergeInnerClasses_dollarInDependencyName_remappedToOuter() {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("Outer", new TreeSet<>(Set.of("dep1")));
        graph.put("Outer$Inner", new TreeSet<>(Set.of("Outer$Inner2", "dep2")));
        graph.put("Outer$Inner2", new TreeSet<>(Set.of("dep3")));

        Map<String, Set<String>> result = InnerClassMerger.mergeInnerClasses(graph);

        // Outer$Inner dependency on Outer$Inner2 should be remapped to Outer
        assertEquals(Set.of("Outer"), result.keySet());
        assertEquals(Set.of("dep1", "dep2", "dep3"), result.get("Outer"));
    }
}