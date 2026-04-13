package spring.twin.scan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InnerClassMerger#mergeInnerClassesDetails(Map, boolean)}.
 */
class InnerClassMergerDetailsTest {

    @Test
    @DisplayName("merge=false returns unmodified graph")
    void testMergeDetails_mergeFalse_returnsUnmodifiedGraph() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        Map<String, Set<LinkDetails>> outerDeps = new LinkedHashMap<>();
        outerDeps.put("Dependency", Set.of(LinkDetails.of(LinkType.FIELD, "field1")));
        graph.put("Outer", outerDeps);
        
        Map<String, Set<LinkDetails>> innerDeps = new LinkedHashMap<>();
        innerDeps.put("InnerDep", Set.of(LinkDetails.of(LinkType.METHOD, "method1()")));
        graph.put("Outer$Inner", innerDeps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, false);

        // Should return the original graph unchanged
        assertSame(graph, result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("Outer"));
        assertTrue(result.containsKey("Outer$Inner"));
    }

    @Test
    @DisplayName("inner class as key is removed, dependencies added to parent")
    void testMergeDetails_innerClassMergedToOuter_innerKeyRemoved() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        
        Map<String, Set<LinkDetails>> outerDeps = new LinkedHashMap<>();
        outerDeps.put("Dep1", Set.of(LinkDetails.of(LinkType.FIELD, "field1")));
        graph.put("Outer", outerDeps);
        
        Map<String, Set<LinkDetails>> innerDeps = new LinkedHashMap<>();
        innerDeps.put("Dep2", Set.of(LinkDetails.of(LinkType.METHOD, "method1()")));
        graph.put("Outer$Inner", innerDeps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, true);

        assertFalse(result.containsKey("Outer$Inner"));
        assertTrue(result.containsKey("Outer"));
        assertEquals(2, result.get("Outer").size());
        assertTrue(result.get("Outer").containsKey("Dep1"));
        assertTrue(result.get("Outer").containsKey("Dep2"));
    }

    @Test
    @DisplayName("LinkDetails are preserved when merging inner class dependencies")
    void testMergeDetails_innerClassDepsAddedToOuter_linkDetailsPreserved() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        
        Set<LinkDetails> outerLinkDetails = new HashSet<>();
        outerLinkDetails.add(LinkDetails.of(LinkType.FIELD, "outerField"));
        Map<String, Set<LinkDetails>> outerDeps = new LinkedHashMap<>();
        outerDeps.put("Dependency", outerLinkDetails);
        graph.put("Outer", outerDeps);
        
        Set<LinkDetails> innerLinkDetails = new HashSet<>();
        innerLinkDetails.add(LinkDetails.of(LinkType.METHOD, "innerMethod()"));
        innerLinkDetails.add(LinkDetails.of(LinkType.FIELD_ANNOTATION, "annotatedField"));
        Map<String, Set<LinkDetails>> innerDeps = new LinkedHashMap<>();
        innerDeps.put("Dependency", innerLinkDetails);
        graph.put("Outer$Inner", innerDeps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, true);

        Set<LinkDetails> mergedDetails = result.get("Outer").get("Dependency");
        assertNotNull(mergedDetails);
        assertEquals(3, mergedDetails.size());
        assertTrue(mergedDetails.contains(LinkDetails.of(LinkType.FIELD, "outerField")));
        assertTrue(mergedDetails.contains(LinkDetails.of(LinkType.METHOD, "innerMethod()")));
        assertTrue(mergedDetails.contains(LinkDetails.of(LinkType.FIELD_ANNOTATION, "annotatedField")));
    }

    @Test
    @DisplayName("inner class references in values are replaced with outer class")
    void testMergeDetails_innerClassInValues_replacedWithOuter() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        
        Map<String, Set<LinkDetails>> outerDeps = new LinkedHashMap<>();
        outerDeps.put("Outer$Inner", Set.of(LinkDetails.of(LinkType.FIELD, "innerField")));
        outerDeps.put("OtherClass", Set.of(LinkDetails.of(LinkType.METHOD, "otherMethod()")));
        graph.put("Outer", outerDeps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, true);

        // Outer$Inner in values should be replaced with Outer, then self-reference removed
        assertFalse(result.get("Outer").containsKey("Outer$Inner"));
        // Self-reference (Outer -> Outer) should be removed
        assertFalse(result.get("Outer").containsKey("Outer"));
        assertTrue(result.get("Outer").containsKey("OtherClass"));
    }

    @Test
    @DisplayName("self-reference is removed after merge")
    void testMergeDetails_selfReferenceRemoved_afterMerge() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        
        Map<String, Set<LinkDetails>> innerDeps = new LinkedHashMap<>();
        innerDeps.put("Outer", Set.of(LinkDetails.of(LinkType.METHOD, "callOuter()")));
        innerDeps.put("OtherDep", Set.of(LinkDetails.of(LinkType.FIELD, "otherField")));
        graph.put("Outer$Inner", innerDeps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, true);

        // After merging Outer$Inner into Outer, self-reference to Outer should be removed
        assertTrue(result.containsKey("Outer"));
        assertFalse(result.get("Outer").containsKey("Outer"));
        assertTrue(result.get("Outer").containsKey("OtherDep"));
    }

    @Test
    @DisplayName("no inner classes - graph unchanged")
    void testMergeDetails_noInnerClasses_graphUnchanged() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        
        Map<String, Set<LinkDetails>> class1Deps = new LinkedHashMap<>();
        class1Deps.put("Dependency1", Set.of(LinkDetails.of(LinkType.FIELD, "field1")));
        graph.put("Class1", class1Deps);
        
        Map<String, Set<LinkDetails>> class2Deps = new LinkedHashMap<>();
        class2Deps.put("Dependency2", Set.of(LinkDetails.of(LinkType.METHOD, "method1()")));
        graph.put("Class2", class2Deps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, true);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Class1"));
        assertTrue(result.containsKey("Class2"));
        assertEquals(1, result.get("Class1").size());
        assertEquals(1, result.get("Class2").size());
        assertTrue(result.get("Class1").containsKey("Dependency1"));
        assertTrue(result.get("Class2").containsKey("Dependency2"));
    }

    @Test
    @DisplayName("deep inner class merged to first outer")
    void testMergeDetails_deepInnerClass_mergedToFirstOuter() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        
        Map<String, Set<LinkDetails>> deepDeps = new LinkedHashMap<>();
        deepDeps.put("SomeDep", Set.of(LinkDetails.of(LinkType.FIELD, "deepField")));
        graph.put("Outer$Inner$Deep", deepDeps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, true);

        assertFalse(result.containsKey("Outer$Inner$Deep"));
        assertFalse(result.containsKey("Outer$Inner"));
        assertTrue(result.containsKey("Outer"));
        assertEquals(1, result.get("Outer").size());
        assertTrue(result.get("Outer").containsKey("SomeDep"));
    }

    @Test
    @DisplayName("outer not in graph - inner deps become outer deps")
    void testMergeDetails_outerNotInGraph_innerDepsBecomeOuterDeps() {
        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        
        Set<LinkDetails> innerLinkDetails = new HashSet<>();
        innerLinkDetails.add(LinkDetails.of(LinkType.FIELD, "field1"));
        innerLinkDetails.add(LinkDetails.of(LinkType.METHOD, "method1()"));
        Map<String, Set<LinkDetails>> innerDeps = new LinkedHashMap<>();
        innerDeps.put("Dependency", innerLinkDetails);
        innerDeps.put("AnotherDep", Set.of(LinkDetails.of(LinkType.CLASS_ANNOTATION)));
        graph.put("Outer$Inner", innerDeps);

        Map<String, Map<String, Set<LinkDetails>>> result = InnerClassMerger.mergeInnerClassesDetails(graph, true);

        assertFalse(result.containsKey("Outer$Inner"));
        assertTrue(result.containsKey("Outer"));
        assertEquals(2, result.get("Outer").size());
        assertTrue(result.get("Outer").containsKey("Dependency"));
        assertTrue(result.get("Outer").containsKey("AnotherDep"));
        
        Set<LinkDetails> depDetails = result.get("Outer").get("Dependency");
        assertEquals(2, depDetails.size());
        assertTrue(depDetails.contains(LinkDetails.of(LinkType.FIELD, "field1")));
        assertTrue(depDetails.contains(LinkDetails.of(LinkType.METHOD, "method1()")));
    }
}