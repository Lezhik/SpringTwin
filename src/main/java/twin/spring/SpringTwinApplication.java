package twin.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

/**
 * Главный класс приложения Spring Twin.
 * Запускает Spring Boot приложение с настроенными модулями.
 * Модули автоматически обнаруживаются на основе аннотации @ApplicationModule в package-info.java файлах.
 */
@SpringBootApplication
@Modulithic
public class SpringTwinApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTwinApplication.class, args);
	}

}
