package spring.twin.analysis.fixtures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Test fixture - service that depends on BaseNonComponentClass (not a component itself).
 * Used to test that analysis finds the concrete component that extends the base class.
 */
@Service
public class ServiceWithBaseClassDependency {

    @Autowired
    private BaseNonComponentClass baseClass;
}