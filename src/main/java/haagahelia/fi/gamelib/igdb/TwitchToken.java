package haagahelia.fi.gamelib.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// IGDB on Twitchin omistama, joten IGDB:n käyttöä varten pitää ensin kirjautua Twitchin kautta.
// Lähetämme Twitchille client id:n ja secretin, ja saamme takaisin access tokenin (väliaikainen "salasana"),
// joka pitää liittää jokaiseen IGDB-pyyntöön. Tämä record sisältää Twitchin JSON-vastauksen.
// @JsonIgnoreProperties: ohitetaan JSONin ylimääräiset kentät, joita emme tarvitse.

@JsonIgnoreProperties(ignoreUnknown = true)
public record TwitchToken(
        // Itse token. @JsonProperty yhdistää JSON-kentän "access_token" tähän Java-kenttään.
        @JsonProperty("access_token") String accessToken,
        // Kuinka monta sekuntia token on voimassa (yleensä noin 60 päivää)
        @JsonProperty("expires_in") long expiresIn) {
}
