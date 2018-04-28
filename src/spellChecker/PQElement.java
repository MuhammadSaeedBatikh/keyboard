package spellChecker;

/**
 * PQElement = PRIORITY_QUEUE_ELEMENT
 */


class PQElement implements Comparable<PQElement> {
    public String word = "";
    public int editDistance = 0;
    public String frequency = "";
    public double tempRank = 0;

    public PQElement(String word, int editDistance, String frequency) {
        this.word = word;
        this.editDistance = editDistance;
        this.frequency = frequency;
    }


    public int getDistance() {
        return editDistance;
    }


    public String getFrequency() {
        return this.frequency;
    }


    public int compareTo(PQElement element) {
        if (rank(this)<rank(element))
            return 1;
        else return -1;

    }   // compare;

    double rank(int distance, Long frequency) {
        double nFreq = Math.log10(frequency);
        double nDist = 30 / (distance + 2);
        return Math.sqrt(nDist * nDist + nFreq * nFreq);
    }

    double rank(PQElement element) {
        String f = element.getFrequency().replaceAll("\\D","");
        return rank(element.getDistance(),Long.parseLong(f));
    }

    @Override
    public String toString() {
        return word;
    }
}

