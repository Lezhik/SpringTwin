package spring.twin.analysis.fixtures;

import org.springframework.stereotype.Service;

/**
 * Test fixture - service implementing TestInterface.
 * Used to test interface dependency resolution.
 */
@Service
public class InterfaceBasedService implements TestInterface {
}