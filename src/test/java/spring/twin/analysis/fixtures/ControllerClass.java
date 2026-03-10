package spring.twin.analysis.fixtures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Test fixture - a Spring controller with constructor injection.
 */
@Controller
public class ControllerClass {
    
    private final ServiceClass serviceClass;
    private final RepositoryClass repositoryClass;
    
    public ControllerClass(ServiceClass serviceClass, RepositoryClass repositoryClass) {
        this.serviceClass = serviceClass;
        this.repositoryClass = repositoryClass;
    }
    
    public String handleRequest() {
        return repositoryClass.fetchData();
    }
}