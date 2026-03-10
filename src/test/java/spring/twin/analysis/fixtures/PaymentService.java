package spring.twin.analysis.fixtures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Test fixture - service that depends on interface (PaymentProcessor).
 * Used to test dependency resolution through inheritance tree.
 */
@Service
public class PaymentService {
    
    @Autowired
    private PaymentProcessor paymentProcessor;
    
    public String pay() {
        return paymentProcessor.processPayment();
    }
}