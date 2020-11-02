package capstone.petitehero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetiteHeroApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetiteHeroApplication.class, args);
	}

}
