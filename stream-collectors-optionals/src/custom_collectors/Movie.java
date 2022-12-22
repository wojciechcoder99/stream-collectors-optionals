package custom_collectors;

import java.util.HashSet;
import java.util.Set;

class Movie {
    private String title;
    private int releaseYear;

    private Set<Actor> actors = new HashSet<>();

    public Movie(String title, int releaseYear) {
        this.title = title;
        this.releaseYear = releaseYear;
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public int getReleaseYear() {
        return releaseYear;
    }
}
