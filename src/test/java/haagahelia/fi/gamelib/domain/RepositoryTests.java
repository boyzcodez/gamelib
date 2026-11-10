package haagahelia.fi.gamelib.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

// Each test runs in a transaction that is rolled back afterwards, so tests don't affect each other
@SpringBootTest
@Transactional
class RepositoryTests {

    @Autowired
    private AppUserRepo userRepo;

    @Autowired
    private GameRepository gameRepository;

    private AppUser alice;
    private AppUser bob;

    @BeforeEach
    void setUp() {
        alice = userRepo.save(new AppUser("alice", "hash", "USER"));
        bob = userRepo.save(new AppUser("bob", "hash", "USER"));
    }

    private Game newGame(String title, Long igdbId, AppUser owner) {
        Game game = new Game();
        game.setTitle(title);
        game.setIgdbId(igdbId);
        game.setOwner(owner);
        return gameRepository.save(game);
    }

    @Test
    void seededUsersExist() {
        assertThat(userRepo.findByUsername("user")).isNotNull();
        assertThat(userRepo.findByUsername("admin").getRole()).isEqualTo("ADMIN");
    }

    @Test
    void findByUsernameReturnsUser() {
        AppUser found = userRepo.findByUsername("alice");
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(alice.getId());
        assertThat(found.getRole()).isEqualTo("USER");
    }

    @Test
    void findByUsernameReturnsNullForUnknownUser() {
        assertThat(userRepo.findByUsername("nobody")).isNull();
    }

    @Test
    void saveGameAssignsId() {
        Game game = newGame("Portal 2", 72L, alice);
        assertThat(game.getId()).isNotNull();
        assertThat(gameRepository.findById(game.getId())).isPresent();
    }

    @Test
    void findByOwnerReturnsOnlyThatUsersGames() {
        newGame("Portal 2", 72L, alice);
        newGame("Celeste", 26226L, alice);
        newGame("Hades", 113112L, bob);

        List<Game> aliceGames = gameRepository.findByOwner(alice);
        assertThat(aliceGames).extracting(Game::getTitle).containsExactlyInAnyOrder("Portal 2", "Celeste");

        List<Game> bobGames = gameRepository.findByOwner(bob);
        assertThat(bobGames).extracting(Game::getTitle).containsExactly("Hades");
    }

    @Test
    void findByTitle() {
        newGame("Portal 2", 72L, alice);
        assertThat(gameRepository.findByTitle("Portal 2")).hasSize(1);
        assertThat(gameRepository.findByTitle("Portal 3")).isEmpty();
    }

    @Test
    void deleteGame() {
        Game game = newGame("Portal 2", 72L, alice);
        gameRepository.delete(game);
        assertThat(gameRepository.findById(game.getId())).isEmpty();
    }
}
