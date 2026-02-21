package twin.spring.unit;

import org.springframework.boot.SpringApplication;
import twin.spring.SpringTwinApplication;

/**
 * Test application entry point for running the application in test mode.
 * Uses Neo4j embedded (in-memory) instead of Testcontainers.
 */
public class TestSpringTwinApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringTwinApplication::main).run(args);
	}

}
