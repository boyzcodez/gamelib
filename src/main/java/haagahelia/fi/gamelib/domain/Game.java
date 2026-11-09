package haagahelia.fi.gamelib.domain;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Game {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String title;
    public String coverImageUrl;
    public Date releaseDate;
    public String Genres;
    public String platforms;
    public String description;

    public Game() {
    }

    public Game(Long id, String title, String coverImageUrl, Date releaseDate, String genres,
            String platforms, String description) {
        this.id = id;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.releaseDate = releaseDate;
        Genres = genres;
        this.platforms = platforms;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenres() {
        return Genres;
    }

    public void setGenres(String genres) {
        Genres = genres;
    }

    public String getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
}
