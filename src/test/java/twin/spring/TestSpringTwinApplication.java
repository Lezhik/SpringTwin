package twin.spring;

import org.springframework.boot.SpringApplication;

public class TestSpringTwinApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringTwinApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
