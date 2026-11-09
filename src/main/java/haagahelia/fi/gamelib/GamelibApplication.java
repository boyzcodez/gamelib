package haagahelia.fi.gamelib;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import haagahelia.fi.gamelib.domain.AppUser;
import haagahelia.fi.gamelib.domain.AppUserRepo;
import haagahelia.fi.gamelib.domain.GameRepository;

@SpringBootApplication
public class GamelibApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamelibApplication.class, args);
	}

	@Bean 
	public CommandLineRunner initRepository(GameRepository gameRepository, AppUserRepo userRepo) {
		return (args) -> {

			AppUser user1 = new AppUser("user", "$2a$06$3jYRJrg0ghaaypjZ/.g4SethoeA51ph3UD4kZi9oPkeMTpjKU5uo6", "USER");
			AppUser user2 = new AppUser("admin", "$2a$10$0MMwY.IQqpsVc1jC8u7IJ.2rT8b0Cd3b3sfIBGV2zfgnPGtT4r0.C", "ADMIN");
			userRepo.save(user1);
			userRepo.save(user2);
		};
	}

}
