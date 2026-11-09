package haagahelia.fi.gamelib.igdb;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// Talks to the IGDB API (https://api-docs.igdb.com). IGDB uses Twitch for authentication.
@Service
public class IgdbService {

    private static final String COVER_URL = "https://images.igdb.com/igdb/image/upload/t_cover_big/%s.jpg";

    private final RestClient restClient = RestClient.create();
    private final String clientId;
    private final String clientSecret;

    private String accessToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    public IgdbService(@Value("${igdb.client-id}") String clientId,
                       @Value("${igdb.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public List<GameSearchResult> searchGames(String query) {
        // IGDB uses its own query language ("apicalypse") sent as a plain-text body
        String body = "search \"" + query.replace("\\", "").replace("\"", "") + "\";"
                + " fields name, cover.image_id;"
                + " limit 20;";

        IgdbGame[] games = restClient.post()
                .uri("https://api.igdb.com/v4/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + getAccessToken())
                .contentType(MediaType.TEXT_PLAIN)
                .body(body)
                .retrieve()
                .body(IgdbGame[].class);

        if (games == null) {
            return List.of();
        }
        return Arrays.stream(games)
                .map(g -> new GameSearchResult(g.id(), g.name(),
                        g.cover() != null && g.cover().imageId() != null
                                ? COVER_URL.formatted(g.cover().imageId())
                                : null))
                .toList();
    }

    // Fetches the full details of a single game by its IGDB id. Returns null if not found.
    public GameDetails getGameDetails(Long igdbId) {
        String body = "fields name, summary, first_release_date, total_rating, cover.image_id, genres.name, platforms.name;"
                + " where id = " + igdbId + ";";

        IgdbGameDetails[] games = restClient.post()
                .uri("https://api.igdb.com/v4/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + getAccessToken())
                .contentType(MediaType.TEXT_PLAIN)
                .body(body)
                .retrieve()
                .body(IgdbGameDetails[].class);

        if (games == null || games.length == 0) {
            return null;
        }
        IgdbGameDetails g = games[0];
        return new GameDetails(
                g.id(),
                g.name(),
                g.cover() != null && g.cover().imageId() != null ? COVER_URL.formatted(g.cover().imageId()) : null,
                g.firstReleaseDate() != null
                        ? Instant.ofEpochSecond(g.firstReleaseDate()).atZone(ZoneOffset.UTC).getYear()
                        : null,
                g.summary(),
                g.genres() != null ? g.genres().stream().map(IgdbGameDetails.Named::name).toList() : List.of(),
                g.platforms() != null ? g.platforms().stream().map(IgdbGameDetails.Named::name).toList() : List.of(),
                g.totalRating() != null ? (int) Math.round(g.totalRating()) : null);
    }

    // Fetches an app access token from Twitch and reuses it until it is about to expire
    private synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiresAt)) {
            TwitchToken token = restClient.post()
                    .uri("https://id.twitch.tv/oauth2/token?client_id={id}&client_secret={secret}&grant_type=client_credentials",
                            clientId, clientSecret)
                    .retrieve()
                    .body(TwitchToken.class);
            accessToken = token.accessToken();
            tokenExpiresAt = Instant.now().plusSeconds(token.expiresIn() - 60);
        }
        return accessToken;
    }
}
