package haagahelia.fi.gamelib.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Raw game object from IGDB's /games endpoint. Only the fields we request.
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGame(Long id, String name, Cover cover) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cover(@JsonProperty("image_id") String imageId) {
    }
}
