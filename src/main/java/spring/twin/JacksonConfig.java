package spring.twin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Configuration for Jackson ObjectMapper.
 *
 * <p>Provides a configured ObjectMapper bean for JSON serialization.
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures the Jackson ObjectMapper.
     *
     * @return a configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
}