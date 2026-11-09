package haagahelia.fi.gamelib.domain;

import org.springframework.data.repository.CrudRepository;

public interface AppUserRepo extends CrudRepository<AppUser, Long> {
	AppUser findByUsername(String username);
}