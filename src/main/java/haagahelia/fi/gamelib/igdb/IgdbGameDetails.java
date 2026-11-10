package haagahelia.fi.gamelib.igdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Yksi peli tarkempine tietoineen siinä muodossa kuin IGDB palauttaa sen (JSON -> Java-olio).

@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGameDetails(
        Long id,
        String name,
        String summary,
        @JsonProperty("first_release_date") Long firstReleaseDate, // unix-aikaleima sekunteina
        @JsonProperty("total_rating") Double totalRating, // pisteet väliltä 0-100
        IgdbGame.Cover cover,
        List<Named> genres,
        List<Named> platforms) {

    // Genret ja alustat tulevat olioina, esim. { "name": "Shooter" },
    // joten tällä pienellä recordilla luetaan niistä pelkkä nimi.
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Named(String name) {
    }
}
