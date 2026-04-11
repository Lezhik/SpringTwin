package spring.twin.testee;

import java.util.List;
import java.util.Map;

/**
 * Test class with various method types for MethodTypeExtractor tests.
 */
public class MethodHolder {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getItems() {
        return null;
    }

    public void process(Map<String, Integer> data) {
    }

    public int calculate() {
        return 0;
    }

    public void setValues(String[] values) {
    }

    public MethodHolder(String name) {
        this.name = name;
    }

}