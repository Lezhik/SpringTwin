package spring.twin.analysis.fixtures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Test fixture - service that depends on ParentClass.
 * ParentClass has descendants (ChildClass, GrandchildClass) but none are Spring components.
 * Used to test that no edges are created when no Spring components implement the dependency.
 */
@Service
public class ServiceInjectingParentClass {

    @Autowired
    private ParentClass parentClass;
}