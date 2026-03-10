package spring.twin.analysis.fixtures;

import org.springframework.stereotype.Service;

/**
 * Test fixture - a simple Spring service component.
 */
@Service
public class ServiceClass {
    
    public String doSomething() {
        return "something";
    }
}