package spring.twin.analysis.fixtures;

import org.springframework.stereotype.Component;

/**
 * Test fixture - concrete component class extending base class.
 * Used to test inheritance-based dependency resolution.
 */
@Component
public class ConcreteComponentClass extends BaseNonComponentClass {
}