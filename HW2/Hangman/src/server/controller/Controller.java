package server.controller;

import server.filehandler.FileHandler;
import server.model.User;

/**
 * The server side controller. All calls to the server side model pass through here.
 */
public class Controller {
    private final User user = new User();

    public String start_game() {
        //Game is starting, reset values and choose a random word.
        user.setActualWord(FileHandler.getRandomWord());
        System.out.println(user.getActualWord());
        getWordWithDashes();
        user.setGuessesLeft(user.getActualWord().length());
        user.setGuessedCharacters("");
        user.setMessage( "Guess the word!");

        return getUserInfoToSend();
    }



    public String handle_guess(String guess) {
        if(guess.length()==1) {
            //Only 1 Letter
            boolean already_guessed = false;

            if(!user.getGuessedCharacters().isEmpty()) {

                for (char i : user.getGuessedCharacters().toCharArray()) {
                    if (i == guess.toCharArray()[0]) {
                        already_guessed = true;
                        user.setMessage("You have already guessed that letter");
                    }
                }
            }
            if (already_guessed == false) {
            //It is a new letter
                String oldWordWithDashes = getWordWithDashes();
                user.setGuessedCharacters(user.getGuessedCharacters()+guess);
                String newWordWithDashes = getWordWithDashes();
                if (newWordWithDashes.equals(user.getActualWord())) {
                    //Word is completed
                    user.setScore(user.getScore()+1);
                    user.setMessage("Congratulations! You won!");
                } else if (oldWordWithDashes.equals(newWordWithDashes)) {
                    //Guess was wrong
                    user.setGuessesLeft(user.getGuessesLeft() - 1);

                    if (user.getGuessesLeft() == 0) {
                        //Game over
                        user.setScore(user.getScore() - 1);
                        user.setMessage("Game over!");
                    } else {
                        //Game goes on, with one less guess left
                        user.setMessage("Guess was wrong!");
                    }
                } else {
                    //Guess was correct
                    user.setMessage("Guess was correct!");
                }
            }
        }else{
            //Guessing the whole word
            user.setGuessesLeft(user.getGuessesLeft() - 1);

            if (user.getGuessesLeft() == 0) {
                //Game over
                user.setScore(user.getScore() - 1);
                user.setMessage("Game over!");
            } else {
                //Game goes on, with one less guess left
                user.setMessage("Guess was wrong!");
            }

            if(guess.equals(user.getActualWord())){
                //Win game
                user.setGuessedCharacters(user.getGuessedCharacters()+guess);
                getWordWithDashes();
                user.setScore(user.getScore()+1);
                user.setMessage("Congratulations! You won!");
            }
        }
        return getUserInfoToSend();
    }




    /**
     * Generate a dashed word from the guesses made.
     * */
    private String getWordWithDashes() {
        if (user.getGuessedCharacters().length() > 0) {
            return user.getActualWord().replaceAll("[^" + user.getGuessedCharacters() + "]", "_ ");
        }
        else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < user.getActualWord().length(); i++) {
                sb.append("_ ");
            }
            return sb.toString();
        }
    }

    /**
     * Send the INFO to the client.
     * @return a String with the INFO
     */
    private String getUserInfoToSend() {
        String fullMessage = user.getMessage() + "##" +
                user.getGuessesLeft() + "##" +
                user.getScore() + "##" +
                getWordWithDashes();
        return fullMessage;

    }

}
