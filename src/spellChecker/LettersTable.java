package spellChecker;

import java.util.HashMap;

/**
 * Created by Muhammad on 19/09/2017.
 */
/*
* giving the following prefix, what is the probability of letter l
* */
public class LettersTable {
    private HashMap<Character, LetterProbability> lettersTable = new HashMap<>();
    private static LettersTable lettersTableInstance = null;

    private LettersTable() {
    }

    public HashMap<Character, LetterProbability> getLettersTable() {
        return lettersTable;
    }

    public static LettersTable create() {
        if (lettersTableInstance == null) {
            lettersTableInstance = new LettersTable();
        }
        return lettersTableInstance;
    }

    public void flush() {
        this.lettersTable = new HashMap<>();
    }

    public void addFromMainD(LetterProbability newLetter) {
        char c = newLetter.c;
        LetterProbability tableLet = this.lettersTable.get(c);
        if (tableLet == null) {
            this.lettersTable.put(c, newLetter);
        } else {
            tableLet.updateFromMainD(newLetter);
            this.lettersTable.put(c, tableLet);
        }
    }

    public void addFromUserD(LetterProbability newLetter) {
        char c = newLetter.c;
        LetterProbability tableLet = this.lettersTable.get(c);
        if (tableLet == null) {
            this.lettersTable.put(c, newLetter);
        } else {
            tableLet.updateFromUserD(newLetter);
            this.lettersTable.put(c, tableLet);
        }
    }
}


