package server.model;

/**
 * Holds the entire Info of User. All methods are thread safe.
 */
public class User{

    private String actualWord;
    private String 	guessedCharacters;
    private int 	guessesLeft;
    private int 	score;
    private String message;

    public User() {
        score =0;
        actualWord="";
        guessedCharacters="";
        message="";

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGuessedCharacters() {
        return guessedCharacters;
    }

    public void setGuessedCharacters(String guessedCharacters) {
        this.guessedCharacters = guessedCharacters;
    }

    public int getGuessesLeft() {
        return guessesLeft;
    }

    public void setGuessesLeft(int guessesLeft) {
        this.guessesLeft = guessesLeft;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getActualWord() {
        return actualWord;
    }

    public void setActualWord(String actualWord) {
        this.actualWord = actualWord;
    }

}
