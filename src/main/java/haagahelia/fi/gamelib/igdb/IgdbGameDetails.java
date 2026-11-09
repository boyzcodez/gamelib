package haagahelia.fi.gamelib.igdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Raw detailed game object from IGDB's /games endpoint (used by the details page)
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGameDetails(
        Long id,
        String name,
        String summary,
        @JsonProperty("first_release_date") Long firstReleaseDate, // unix timestamp in seconds
        @JsonProperty("total_rating") Double totalRating,
        IgdbGame.Cover cover,
        List<Named> genres,
        List<Named> platforms) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Named(String name) {
    }
}
