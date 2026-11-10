package haagahelia.fi.gamelib.igdb;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// Hoitaa yhteyden IGDB:n rajapintaan (https://api-docs.igdb.com).
//
// Näin IGDB-pyyntö toimii:
//   1. Haetaan access token Twitchiltä (IGDB on Twitchin omistama ja käyttää sen kirjautumista).
//   2. Lähetetään IGDB:lle POST-pyyntö kahdella headerilla: Client-ID ja access token.
//   3. Pyynnön runko on lyhyt tekstimuotoinen kysely, joka kertoo mitä haluamme (ks. searchGames).
//   4. IGDB vastaa JSONilla, jonka Spring muuntaa automaattisesti Java-recordeiksemme.


@Service
public class IgdbService {

    // IGDB antaa kansikuvista vain kuvan id:n. Linkki + id = kuva
    private static final String COVER_URL = "https://images.igdb.com/igdb/image/upload/t_cover_big/%s.jpg";

    // RestClient on Springin työkalu ???
    private final RestClient restClient = RestClient.create();

    // Sovelluksemme tunnukset / application.propertiesista.
    private final String clientId;
    private final String clientSecret;

    // Nykyinen Twitchin access token ja hetki, jolloin se lakkaa toimimasta.
    private String accessToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    // @Value hakee arvot application.propertiesista
    public IgdbService(@Value("${igdb.client-id}") String clientId,
                       @Value("${igdb.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    // Hakee IGDB:stä pelejä annetulla hakusanalla. Hakusivu käyttää tätä.
    public List<GameSearchResult> searchGames(String query) {

        // IGDB:n omat haku kieli
        String body = "search \"" + query.replace("\\", "").replace("\"", "") + "\";"
                + " fields name, cover.image_id;"
                + " limit 20;";

        // Lähetetään kysely IGDB:lle. Jokainen pyyntö tarvitsee Client-ID:n ja Twitchin tokenin.
        // IGDB palauttaa JSON-listan peleistä, joka muunnetaan IgdbGame-taulukoksi.
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

        // Muunnetaan IGDB:n muoto yksinkertaisemmaksi GameSearchResultiksi.
        // Kaikilla peleillä ei ole kansikuvaa, joten niillä kuvan URL on null.
        return Arrays.stream(games)
                .map(g -> new GameSearchResult(g.id(), g.name(),
                        g.cover() != null && g.cover().imageId() != null
                                ? COVER_URL.formatted(g.cover().imageId())
                                : null))
                .toList();
    }

    // Hakee yhden pelin kaikki tiedot sen IGDB-id:n perusteella.
    public GameDetails getGameDetails(Long igdbId) {

        String body = "fields name, summary, first_release_date, total_rating, cover.image_id, genres.name, platforms.name;"
                + " where id = " + igdbId + ";";

        // Samanlainen pyyntö kuin searchGamesissa, vain eri kysely
        IgdbGameDetails[] games = restClient.post()
                .uri("https://api.igdb.com/v4/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + getAccessToken())
                .contentType(MediaType.TEXT_PLAIN)
                .body(body)
                .retrieve()
                .body(IgdbGameDetails[].class);

        // IGDB palauttaa aina listan, vaikka kysyttäisiin vain yhtä id:tä.
        if (games == null || games.length == 0) {
            return null;
        }
        IgdbGameDetails g = games[0];

        // Muunnetaan IGDB:n raakadata sivulla helposti näytettävään muotoon.
        return new GameDetails(
                g.id(),
                g.name(),
                // kuvan id -> täysi kuvan URL
                g.cover() != null && g.cover().imageId() != null ? COVER_URL.formatted(g.cover().imageId()) : null,
                // julkaisupäivä tulee sekunteina vuodesta 1970 -> halutaan vain vuosi
                g.firstReleaseDate() != null
                        ? Instant.ofEpochSecond(g.firstReleaseDate()).atZone(ZoneOffset.UTC).getYear()
                        : null,
                g.summary(),
                // lista { name: "..." } -olioita -> lista tavallisia merkkijonoja
                g.genres() != null ? g.genres().stream().map(IgdbGameDetails.Named::name).toList() : List.of(),
                g.platforms() != null ? g.platforms().stream().map(IgdbGameDetails.Named::name).toList() : List.of(),
                // arvosana kuten 87.43 -> 87
                g.totalRating() != null ? (int) Math.round(g.totalRating()) : null);
    }

    // Palauttaa voimassa olevan Twitchin access tokenin, jota tarvitaan jokaisessa IGDB-pyynnössä.
    private synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiresAt)) {

            // "client_credentials" = kirjaudutaan sovelluksena (ei Twitch-käyttäjänä)
            TwitchToken token = restClient.post()
                    .uri("https://id.twitch.tv/oauth2/token?client_id={id}&client_secret={secret}&grant_type=client_credentials",
                            clientId, clientSecret)
                    .retrieve()
                    .body(TwitchToken.class);
            accessToken = token.accessToken();

            // Tokenia pidetään vanhentuneena 60 sekuntia etuajassa varmuuden vuoksi
            tokenExpiresAt = Instant.now().plusSeconds(token.expiresIn() - 60);
        }
        return accessToken;
    }
}
