package spellChecker.userdictionary;

import spellChecker.LetterProbability;
import spellChecker.LettersTable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Muhammad on 12/09/2017.
 */
public class UserDictionaryBean {
    public String dictionaryPath = "user dictionary.csv";
    public FileWriter fileWriter = new FileWriter(dictionaryPath, true);
    public final static int ADDING_TO_DICTIONARY_THRESHOLD = 2;
    public Charset charset = StandardCharsets.UTF_8;
    public SortedMap<String, DictionaryWord> dictionary = new TreeMap<>();
    public static final int SUGGESTED_LIMIT = 6;
    private static LettersTable lettersTable = LettersTable.create();


    public UserDictionaryBean(String dictionaryPath) throws IOException {
        this.dictionaryPath = dictionaryPath;
        loadDictionary();
    }


    public void addWord(DictionaryWord word) throws IOException {
        word.updateOnAdding();
        if (word.addedTimes >= ADDING_TO_DICTIONARY_THRESHOLD) {
            saveToDictionaryFile(word);
        }
        dictionary.put(word.word, word);
    }

    public void addWord(String word) throws IOException {
        DictionaryWord dictionaryWord = findWord(word);
        if (dictionaryWord == null) {
            addWord(new DictionaryWord(word, 1, 1));
        } else {
            addWord(dictionaryWord);
        }
    }

    public void loadDictionary() throws FileNotFoundException, UnsupportedEncodingException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.dictionaryPath), "UTF-8"));
            br.lines().forEach(s -> {
                DictionaryWord dictionaryWord = new DictionaryWord(s);
                if (dictionaryWord.word != null) {
                    dictionary.put(dictionaryWord.word, dictionaryWord);
                }
            });
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void saveToDictionaryFile(DictionaryWord word) throws IOException {
        String csvLine = word.word + "," + word.typingFrequency + "," + word.pickingFrequency;
        List<AssociatedWord> associatedWordList = word.associatedWords;
        List<String> stringList = new ArrayList<>();
        for (AssociatedWord associatedWord : associatedWordList) {
            stringList.add(associatedWord.dashedFormat());
        }
        String associatedWords = String.join(",", stringList);
        if (associatedWords.length() > 0)
            csvLine += "," + associatedWords;
        updateLine(word.word, csvLine);
    }

    public DictionaryWord findWord(String word) {
        DictionaryWord word1 = dictionary.get(word);
        return word1;
    }


    public void addAssociateWords(String word, String... associates) throws IOException {
        DictionaryWord dictionaryWord = findWord(word);
    }

    public void updateLine(String atWord, String newCSVLine) throws IOException {
        //resource intensive *apply cursor approach later*
        //TODO
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.dictionaryPath), "UTF-8"));
        List<String> lines = br.lines().collect(Collectors.toList());
        Iterator<String> iterator = lines.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(atWord)) {
                lines.remove(line);
                lines.add(i, newCSVLine);
                break;
            }
            i++;
        }
        if (i == lines.size())
            lines.add(newCSVLine);
        Files.write(Paths.get(this.dictionaryPath), lines, charset, StandardOpenOption.CREATE);

    }

    public List<String> searchByPrefix(String prefix) {
        if (prefix.length() > 0) {
            char nextLetter = (char) (prefix.charAt(prefix.length() - 1) + 1);
            String end = prefix.substring(0, prefix.length() - 1) + nextLetter;
            List<DictionaryWord> dictionaryWords = new ArrayList<>(this.dictionary.subMap(prefix, end).values());
            dictionaryWords = dictionaryWords.size() > this.SUGGESTED_LIMIT ? dictionaryWords.subList(0, SUGGESTED_LIMIT) : dictionaryWords;
            Collections.sort(dictionaryWords);
            List<String> finalList = new ArrayList<>();
            dictionaryWords.forEach(dictionaryWord -> finalList.add(dictionaryWord.word));
            return finalList;
        }
        return null;
    }

    public List<String> searchLineByPrefix(String previousWord, String prefix) {

        List<String> suggestedList = new ArrayList<>();
        if (previousWord == null & prefix == null) {
            //handel this by default list of words
            return null;
        } else if (previousWord == null & prefix != null) {
            return searchByPrefix(prefix);
        }
        List<AssociatedWord> associatedWords = findWord(previousWord).associatedWords;
        if (previousWord != null & prefix == null) {
            List<String> finalSuggestedList1 = suggestedList;
            associatedWords.forEach(associatedWord -> finalSuggestedList1.add(associatedWord.word));
            return finalSuggestedList1;
        } else {
            //check associated words that start with prefix
            List<String> finalSuggestedList = suggestedList;
            associatedWords.stream().
                    filter(associatedWord -> associatedWord.word.startsWith(prefix)).
                    forEach(associatedWord -> finalSuggestedList.add(associatedWord.word));
            suggestedList = finalSuggestedList;
            //check for all other words in user's dictionary that start with prefix
            List<String> otherPrefixedList = searchByPrefix(prefix);
            suggestedList.addAll(otherPrefixedList);
            return suggestedList;
        }
    }

    public void countLettersByPrefix(String prefix) {
        if (prefix.length() > 0) {
            char nextLetter = (char) (prefix.charAt(prefix.length() - 1) + 1);
            String end = prefix.substring(0, prefix.length() - 1) + nextLetter;
            List<DictionaryWord> dictionaryWords = new ArrayList<>(this.dictionary.subMap(prefix, end).values());
            for (DictionaryWord dictionaryWord : dictionaryWords) {
                int nextLetterLocation = prefix.length();
                String word = dictionaryWord.word;
                if (word.length() > nextLetterLocation) {
                    LetterProbability letterProbability = new LetterProbability(word.charAt(nextLetterLocation),
                            dictionaryWord.typingFrequency, dictionaryWord.typingFrequency);
                    lettersTable.addFromUserD(letterProbability);
                }
            }
        }

    }

    public void predictNextLetter(String previousWord, String prefix) {
        if (previousWord == null & prefix == null) {
            //handel this by default list of words
            return;
        } else if (previousWord == null & prefix != null) {
            countLettersByPrefix(prefix);
            return;
        }
        DictionaryWord dictionaryWord = findWord(previousWord);
        if (dictionaryWord == null) {
            return;
        }
        List<AssociatedWord> associatedWords = dictionaryWord.associatedWords;

        if (previousWord != null & prefix == null) {
            for (AssociatedWord associatedWord : associatedWords) {
                String word = associatedWord.word;
                LetterProbability letterProbability = new LetterProbability(word.charAt(0), associatedWord.count);
                lettersTable.addFromUserD(letterProbability);
            }
        } else {
            //check associated words that start with prefix
            associatedWords.stream().
                    filter(associatedWord -> associatedWord.word.startsWith(prefix)).forEach(associatedWord -> {
                int nextLetterLocation = prefix.length();
                String word = associatedWord.word;
                if (word.length() >= nextLetterLocation) {
                    lettersTable.addFromUserD(new LetterProbability(word.charAt(nextLetterLocation), associatedWord.count));
                }
            });
            //check for all other words in user's dictionary that start with prefix
            countLettersByPrefix(prefix);
        }
    }
}


