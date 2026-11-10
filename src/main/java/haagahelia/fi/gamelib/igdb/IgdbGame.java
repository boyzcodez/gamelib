package haagahelia.fi.gamelib.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Yksi peli siinä muodossa kuin IGDB palauttaa sen haussa (JSON -> Java-olio).
// IGDB palauttaa vain ne kentät, joita pyydämme, joten tässä on vain id, nimi ja kansikuva.
// Käytetään vain sisäisesti; muu sovellus käyttää yksinkertaisempaa GameSearchResultia.
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGame(Long id, String name, Cover cover) {

    // IGDB ei anna kansikuvasta valmista linkkiä, vain kuvan id:n.
    // Varsinainen kuvan URL rakennetaan tästä id:stä IgdbServicessä (ks. COVER_URL).
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cover(@JsonProperty("image_id") String imageId) {
    }
}
