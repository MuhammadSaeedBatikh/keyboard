package spellChecker;/*
    A class that uses Ternary search tree and lavensthene distance
	to return a list of suggestion of 10 ten words.

	@author : 	Amarjeet Anand
	@since 	: 	6/5/2017
	@version:	1.0
*/


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SpellCorrect {

    private int EDIT_LIMIT = 3;
    private int SUGGESTED_WORD_LIST_LIMIT = 10;
    private static final String WORD_LIST_FILE = "word_freq.txt";
    private String inputString = "";
    private PriorityQueue<PQElement> suggestedWords = new PriorityQueue<PQElement>(10);
    private static TST tst = null;
    private static LettersTable lettersTable = LettersTable.create();
    private List<LetterProbability> letterProbabilityList = new ArrayList<>();

    // create the ternary search tree and populate with words.
    static {
        long startTime = System.currentTimeMillis();
        tst = new TST();
        createTST(tst);
        System.out.println("Tree created in : " + ((System.currentTimeMillis() - startTime)) + " ms");
    }

    public void setEditLimit(int edit_limit) {
        if (edit_limit < 0) {
            return;
        }
        this.EDIT_LIMIT = edit_limit;
    }

    // set how many words will be suggested
    // default is 10
    public void setSuggestedWordListLimit(int word_list_limit) {
        if (word_list_limit <= 0) {
            return;
        }
        this.SUGGESTED_WORD_LIST_LIMIT = word_list_limit;
    }

    // returns a linked lettersMap of key value pair, sorted by value, where
    // key is the suggested word,
    // value is its distance from input word.
    public List<PQElement> correct(String str) throws IllegalArgumentException {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("Input string is blank.");
        }
        inputString = str;
        traverse(tst.getRoot(), "");

        // adding 10 words to linkedHashMap to give as output.
        List<PQElement> elementList =
                new ArrayList<>();
        for (int i = 0; suggestedWords.isEmpty() == false && i < SUGGESTED_WORD_LIST_LIMIT; i++) {
            PQElement element = suggestedWords.poll();
            elementList.add(element);
        }
        return elementList;
    }

    private void traverse(Node root, String str) {

        if (root == null) return;

        int dis = getLevensthienDistance(inputString, str + root.getData());
        // skip traversing the nodes below which distance is grater than EDIT_LIMIT.
        if ((str.length() < inputString.length()) &&
                (getLevensthienDistance(str, inputString.substring(0, str.length() + 1)) > EDIT_LIMIT)) {
            return;
        } else if (str.length() > inputString.length() + EDIT_LIMIT) {
            return;
        } else if (Math.abs(str.length() - inputString.length()) <= EDIT_LIMIT &&
                dis > EDIT_LIMIT) {
            return;
        }

        // recursively traverse through the nodes for words
        traverse(root.getLeft(), str);
        if (root.getIsEnd() == true) {
            if (dis <= EDIT_LIMIT) {
                suggestedWords.add(new PQElement(str + root.getData(), dis, root.getFrequency()));
            }
        }
        traverse(root.getEqual(), str + root.getData());
        traverse(root.getRight(), str);
    }

    private void traverseForPrefix(Node root, String str) {

        if (root == null) return;

        //int dis = getLevensthienDistance(inputString, str + root.getData());
        // skip traversing the nodes below which distance is grater than EDIT_LIMIT.

        // recursively traverse through the nodes for words
        traverseForPrefix(root.getLeft(), str);
        if (root.getIsEnd() == true) {
            String word = str + root.getData();
            if (word.startsWith(inputString)) {
                suggestedWords.add(new PQElement(str + root.getData(), 0, root.getFrequency()));
            }
        }
        traverseForPrefix(root.getEqual(), str + root.getData());
        traverseForPrefix(root.getRight(), str);
    }

    public List<String> searchByPrefix(String prefix) {
        if (prefix == null || prefix.equals("")) {
            throw new IllegalArgumentException("Input string is blank.");
        }
        inputString = prefix;
        traverseForPrefix(tst.getRoot(), "");

        // adding 10 words to linkedHashMap to give as output.
        List<String> elementList = new ArrayList<>();
        for (int i = 0; suggestedWords.isEmpty() == false && i < SUGGESTED_WORD_LIST_LIMIT; i++) {
            PQElement element = suggestedWords.poll();
            elementList.add(element.word);
        }
        return elementList;
    }

    private static void createTST(TST tst) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(WORD_LIST_FILE), "UTF-8"));
            br.lines().forEach(line -> {
                String token[] = null;
                token = line.split(" ");
                // token[0] = actual word.
                // token[1] = frequency of the word.
                tst.insert(token[0], token[1]);
            });
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return root;
    }


    // Levenshtein distance
    private int getLevensthienDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private void traverseForCountingNextLetter(Node root, String str, int nextLetterLocation) {

        if (root == null) return;

        //int dis = getLevensthienDistance(inputString, str + root.getData());
        // skip traversing the nodes below which distance is grater than EDIT_LIMIT.

        // recursively traverse through the nodes for words


        traverseForCountingNextLetter(root.getLeft(), str, nextLetterLocation);
        if (root.getIsEnd() == true) {
            String word = str + root.getData();
            if (word.startsWith(inputString)) {
                if (word.length() > nextLetterLocation) {
                    System.out.println(word);
                    LetterProbability letterProbability = new LetterProbability(word.charAt(nextLetterLocation),
                            Integer.parseInt(root.getFrequency()));

                    lettersTable.addFromMainD(letterProbability);
                }
            }
        }
        traverseForCountingNextLetter(root.getEqual(), str + root.getData(), nextLetterLocation);
        traverseForCountingNextLetter(root.getRight(), str, nextLetterLocation);

    }

    public void predictNextLetter(String prefix) {
        if (prefix == null || prefix.equals("")) {
            throw new IllegalArgumentException("Input string is blank.");
        }
        inputString = prefix;
        traverseForCountingNextLetter(tst.getRoot(), "", prefix.length());

    }
}

