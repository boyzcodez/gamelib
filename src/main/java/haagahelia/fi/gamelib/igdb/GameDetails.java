package haagahelia.fi.gamelib.igdb;

import java.util.List;

// What the game details page displays
public record GameDetails(
        Long id,
        String name,
        String coverUrl,
        Integer releaseYear,
        String summary,
        List<String> genres,
        List<String> platforms,
        Integer rating) {
}
