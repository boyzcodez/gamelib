package haagahelia.fi.gamelib.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import haagahelia.fi.gamelib.igdb.GameSearchResult;
import haagahelia.fi.gamelib.igdb.IgdbService;

@Controller
public class SearchController {

    private final IgdbService igdbService;

    public SearchController(IgdbService igdbService) {
        this.igdbService = igdbService;
    }

    // Hakusivu. Hakusana tulee URL:sta, esim. /searchLibrary?q=zelda
    @GetMapping("/searchLibrary")
    public String searchLibrary(@RequestParam(name = "q", required = false) String query, Model model) {
        List<GameSearchResult> games = List.of();
        
        if (query != null && !query.isBlank()) {
            games = igdbService.searchGames(query.trim());
        }
        model.addAttribute("query", query);
        model.addAttribute("games", games);
        return "searchLibrary";
    }
}
