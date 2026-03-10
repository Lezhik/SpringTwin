package spring.twin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import spring.twin.analysis.SpringDiAnalyzerService;

/**
 * Spring Twin CLI application for analyzing Spring Boot projects.
 * <p>
 * This application provides commands for:
 * <ul>
 *   <li>scan-classes - Analyze .class files and extract Spring DI dependencies</li>
 *   <li>scan-bytecode - Analyze structural dependencies from bytecode</li>
 *   <li>cluster - Perform clustering on dependency graphs</li>
 *   <li>generate-refactoring - Generate refactoring tasks for AI agents</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(
	basePackages = "spring.twin",
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.REGEX,
		pattern = "spring\\.twin\\.analysis\\.fixtures\\..*"
	)
)
public class SpringTwinApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(SpringTwinApplication.class, args)));
	}

	/**
	 * Provides the Spring DI analyzer service as a Spring bean.
	 *
	 * @return the analyzer service
	 */
	@Bean
	public SpringDiAnalyzerService springDiAnalyzerService() {
		return new SpringDiAnalyzerService();
	}
}
