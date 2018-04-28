package spellChecker.userdictionary;

/**
 * Created by Muhammad on 19/09/2017.
 */
public class AssociatedWord implements Comparable<AssociatedWord> {
  public   String word;
  public   double count = 0;

    String dashedFormat() {
        return this.word + "-" + this.count;
    }

    public AssociatedWord(String word, double count) {
        this.word = word;
        this.count = count;
    }

    public AssociatedWord(String dashedElement) {
        String[] strings = dashedElement.split("-");
        this.word = strings[0];
        this.count = Double.parseDouble(strings[1]);
    }

    @Override
    public int compareTo(AssociatedWord o) {
        if (this.count < o.count) {
            return 1;
        } else {
            return -1;
        }
    }
}
