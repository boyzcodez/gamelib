package haagahelia.fi.gamelib.web;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import haagahelia.fi.gamelib.domain.AppUser;
import haagahelia.fi.gamelib.domain.AppUserRepo;
import haagahelia.fi.gamelib.domain.Game;
import haagahelia.fi.gamelib.domain.GameRepository;
import haagahelia.fi.gamelib.igdb.GameDetails;
import haagahelia.fi.gamelib.igdb.IgdbService;

@Controller
public class GameController {

    private final GameRepository gameRepository;
    private final AppUserRepo userRepo;
    private final IgdbService igdbService;

    public GameController(GameRepository gameRepository, AppUserRepo userRepo, IgdbService igdbService) {
        this.gameRepository = gameRepository;
        this.userRepo = userRepo;
        this.igdbService = igdbService;
    }

    @RequestMapping("/")
    public String index() {
        return "redirect:/privateLibrary";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value="/privateLibrary")
    public String gameLibrary(Principal principal, Model model) {

        AppUser user = userRepo.findByUsername(principal.getName());
        model.addAttribute("games", gameRepository.findByOwner(user));

        return "privateLibrary";
    }

    // Details page for a game in the user's own library
    @GetMapping("/library/game/{id}")
    public String gameDetails(@PathVariable Long id, Principal principal, Model model) {

        AppUser user = userRepo.findByUsername(principal.getName());
        Game game = gameRepository.findById(id).orElse(null);

        // Only allow viewing games that belong to the logged in user
        if (game == null || game.getOwner() == null || !game.getOwner().getId().equals(user.getId())) {
            return "redirect:/privateLibrary";
        }

        GameDetails details = null;
        if (game.getIgdbId() != null) {
            try {
                details = igdbService.getGameDetails(game.getIgdbId());
            } catch (Exception e) {
                // IGDB unavailable -> fall back to what is stored locally
            }
        }
        model.addAttribute("game", game);
        model.addAttribute("details", details);
        return "gameDetails";
    }

    // Called when a game's cover is clicked on the search page
    @PostMapping("/library/add")
    public String addGame(@RequestParam Long igdbId,
                          @RequestParam String title,
                          @RequestParam(required = false) String coverUrl,
                          Principal principal) {
        
        AppUser user = userRepo.findByUsername(principal.getName());
        
        if (!gameRepository.existsByOwnerAndIgdbId(user, igdbId)) {
            Game game = new Game();
            game.setIgdbId(igdbId);
            game.setTitle(title);
            game.setCoverImageUrl(coverUrl);
            game.setOwner(user);
            gameRepository.save(game);
        }
        return "redirect:/privateLibrary";
    }
    
}
