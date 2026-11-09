package haagahelia.fi.gamelib.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Response from Twitch's OAuth client-credentials endpoint
@JsonIgnoreProperties(ignoreUnknown = true)
public record TwitchToken(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn) {
}
