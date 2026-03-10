package spring.twin.analysis.fixtures;

import org.springframework.stereotype.Component;

/**
 * Test fixture - second implementation of TestInterface.
 * Used to test that when multiple Spring components implement the same interface,
 * all of them are found as dependency targets.
 */
@Component
public class SecondInterfaceImplementation implements TestInterface {
}