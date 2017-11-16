package server.filehandler;

import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    /**
     * This method returns an Array list with the names on the file
     * @return ArrayList wih names on the file
     * @throws FileNotFoundException
     */
    private static ArrayList<String> getWordsFromFile () throws FileNotFoundException {
            ArrayList<String> words = new ArrayList<String>();

            try {
                BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Gigi\\Documents\\Master's in Software Engineering of distributed systems\\Period 2\\Network Programming\\Homework\\HW1\\Hangman\\src\\server\\resources\\words.txt"));
                String line;
                //Read all words from the list and add them to an array
                while ((line = reader.readLine()) != null) {
                    words.add(line);
                }
                reader.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return words;
    }

    /**
     * Get a random word from the list.
     * */
    public static String getRandomWord() {
        ArrayList<String> words = null;
        try {
            words = getWordsFromFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int index = (int) (Math.random() * words.size());
        return words.get(index).toLowerCase();
    }

}
