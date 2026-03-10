package spring.twin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

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
		SpringApplication.run(SpringTwinApplication.class, args);
	}

}
