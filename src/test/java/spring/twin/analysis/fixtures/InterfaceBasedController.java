package spring.twin.analysis.fixtures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Test fixture - controller that depends on TestInterface (not the concrete service).
 * Used to test that analysis finds the concrete implementation.
 */
@Controller
public class InterfaceBasedController {

    @Autowired
    private TestInterface testInterface;
}