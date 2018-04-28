package spellChecker;

public class LetterProbability implements Comparable<LetterProbability> {
    //letter spics, with some prefix
    char c;
    int occurrenceInMainDec = 0;
    int frequency = 0;
    int occurrenceInUserDic = 0;
    int typingFrequency = 0;
    double occurrenceInAssociatedWords = 0;
    double probability;

    public LetterProbability(char c, int occurrenceInMainDec, int occurrenceInUserDic, int occurrenceInAssociatedWords) {
        this.c = c;
        this.occurrenceInMainDec = occurrenceInMainDec;
        this.occurrenceInUserDic = occurrenceInUserDic;
        this.occurrenceInAssociatedWords = occurrenceInAssociatedWords;
        probability = evaluationFunction();
    }

    public LetterProbability(char c, int frequency) {
        this.c = c;
        this.frequency = frequency;
    }

    public LetterProbability(char c, int occurrenceInUserDic, int typingFrequency) {
        this.c = c;
        this.occurrenceInUserDic = occurrenceInUserDic;
        this.typingFrequency = typingFrequency;

    }

    public LetterProbability(char c, double occurrenceInAssociatedWords) {
        this.c = c;
        this.occurrenceInAssociatedWords = occurrenceInAssociatedWords;
    }

    public double evaluationFunction() {
        //perform your calc here

        return 0.0;
    }

    public void updateFromMainD(LetterProbability letter) {
        this.frequency += letter.frequency;
        this.occurrenceInMainDec += 1;
    }

    public void updateFromUserD(LetterProbability letter) {
        this.occurrenceInAssociatedWords += letter.occurrenceInAssociatedWords;
        this.occurrenceInUserDic += 1;
        this.typingFrequency += letter.typingFrequency;

    }

    @Override
    public int compareTo(LetterProbability o) {
        if (this.probability < o.probability) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "LetterProbability{" +
                "c=" + c +
                ", occurrenceInMainDec=" + occurrenceInMainDec +
                ", frequency=" + frequency +
                ", occurrenceInUserDic=" + occurrenceInUserDic +
                ", occurrenceInAssociatedWords=" + occurrenceInAssociatedWords +
                ", typingFrequency=" + typingFrequency +
                ", probability=" + probability +
                '}';
    }
}
