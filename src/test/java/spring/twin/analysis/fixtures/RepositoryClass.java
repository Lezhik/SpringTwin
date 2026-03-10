package spring.twin.analysis.fixtures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Test fixture - a Spring repository with field injection.
 */
@Repository
public class RepositoryClass {
    
    @Autowired
    private ServiceClass serviceClass;
    
    public String fetchData() {
        return serviceClass.doSomething();
    }
}