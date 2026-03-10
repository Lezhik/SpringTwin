package spring.twin.analysis.fixtures;

import org.springframework.stereotype.Component;

/**
 * Test fixture - implementation of PaymentProcessor.
 */
@Component
public class StripePaymentProcessor implements PaymentProcessor {
    
    @Override
    public String processPayment() {
        return "stripe";
    }
}