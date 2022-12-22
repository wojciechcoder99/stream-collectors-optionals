package custom_collectors;

public class Main {
    public static void main(String[] args) {
        CustomCollectors.processFileAndExtractMoviesAndActors();
//        CustomCollectors.countNumberOfAllActors();
//        CustomCollectors.findTenActorsWhoPlayedInTheMostMovies();
        CustomCollectors.findActorWhoPlayedInTheMostMoviesDuringAYear();
    }
}
