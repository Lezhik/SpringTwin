package spring.twin.analysis.fixtures;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test fixture - configuration class with bean definitions.
 */
@Configuration
public class OrderService {
    
    @Bean
    public String orderBean() {
        return "order";
    }
    
    public String processOrder() {
        return "processed";
    }
}