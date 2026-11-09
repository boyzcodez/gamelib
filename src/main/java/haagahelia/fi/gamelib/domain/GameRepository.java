package haagahelia.fi.gamelib.domain;

import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface GameRepository extends CrudRepository<Game, Long> {
    List<Game> findByTitle(String title);
}
