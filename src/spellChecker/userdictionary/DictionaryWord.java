package spellChecker.userdictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad on 19/09/2017.
 */
public class DictionaryWord implements Comparable<DictionaryWord> {
    public String word;
    int typingFrequency = 0;
    public int pickingFrequency = 0;
    public List<AssociatedWord> associatedWords = new ArrayList<>();
    public int addedTimes = 0; //for threshold checking

    public DictionaryWord(String word, int typingFrequency, int pickingFrequency, AssociatedWord... words) {
        this.word = word;
        this.typingFrequency = typingFrequency;
        this.pickingFrequency = pickingFrequency;
        for (int i = 0; i < words.length; i++) {
            this.associatedWords.add(words[i]);
        }
    }

    public int associateWordIndex(String word) {
        for (int i = 0; i < associatedWords.size(); i++) {
            if (associatedWords.get(i).
                    word.equalsIgnoreCase(word)) {
                return i;
            }
        }
        return -1;
    }

    public void addAssociateWords(AssociatedWord... words) {

        for (int i = 0; i < words.length; i++) {
            int index = associateWordIndex(words[i].word);
            if (index == -1)
                this.associatedWords.add(words[i]);
            else {
                words[i].count = 1 + associatedWords.get(i).count;
                associatedWords.set(index, words[i]);
            }
        }
    }


    public DictionaryWord(String csvLine) {
        String[] elements = csvLine.split(",");
        if (elements.length > 2) {
            this.word = elements[0];
            this.typingFrequency = Integer.parseInt(elements[1]);
            this.pickingFrequency = Integer.parseInt(elements[2]);
        }
        if (elements.length > 3) {
            for (int i = 3; i < elements.length; i++) {
                addAssociateWords(new AssociatedWord(elements[i]));
            }
        }
    }

    public void updateOnAdding() {
        this.typingFrequency += 1;
        this.pickingFrequency += 1;
        this.addedTimes += 1;
    }

    @Override
    public String toString() {
        return "DictionaryWord{" +
                "word='" + word + '\'' +
                ", typingFrequency=" + typingFrequency +
                ", pickingFrequency=" + pickingFrequency +
                ", associatedWords=" + associatedWords +
                ", typingFrequency=" + addedTimes +
                '}';
    }

    @Override
    public int compareTo(DictionaryWord o) {
        if (this.addedTimes < o.addedTimes) {
            return 1;
        } else {
            return -1;
        }
    }
}
