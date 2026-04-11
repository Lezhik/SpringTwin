package spring.twin.testee;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Complex class combining multiple dependency types for BytecodeClassAnalyzer tests.
 * 
 * <p>This class demonstrates all types of dependencies:
 * <ul>
 *   <li>Inheritance (extends and implements)</li>
 *   <li>Fields with generic types</li>
 *   <li>Methods with parameters and return types</li>
 *   <li>Annotations</li>
 *   <li>Types used in method bodies</li>
 * </ul>
 */
@Deprecated
public class ComplexService extends InheritanceBase implements InheritanceChildInterface {

    private String name;
    private List<String> items;
    private Map<String, Integer> data;

    public ComplexService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @CustomAnnotation
    public List<String> processItems() {
        List<String> result = new ArrayList<>();
        result.add(String.valueOf(42));
        return result;
    }

    public void processData(Map<String, Integer> input) {
        String key = input.keySet().iterator().next();
        Integer value = input.get(key);
    }

    public int calculate() {
        return items.size();
    }
}