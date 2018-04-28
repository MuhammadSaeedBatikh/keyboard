package spellChecker;

import spellChecker.userdictionary.AssociatedWord;
import spellChecker.userdictionary.DictionaryWord;
import spellChecker.userdictionary.UserDictionaryBean;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class InputAnalyzerAPI {
    public static SpellCorrect spellCorrect = new SpellCorrect();
    public static UserDictionaryBean userDictionary;
    public static final int SUGGESTED_LIMIT = 6;
    //  public static List<String> defaultSuggestedWord = Arrays.asList("·«", "„‰", "√‰«", "√‰ ", "‰⁄„");

    public static void initialize() throws IOException {
        userDictionary = new UserDictionaryBean("user dictionary.csv");
        spellCorrect.setEditLimit(2);
        spellCorrect.setSuggestedWordListLimit(SUGGESTED_LIMIT);
    }

    public static void analyzing() throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter a word to correct : ");
            String inputWord = sc.nextLine();
            List<String> suggestedWords = inputAnalyzer(inputWord);

            System.out.println("pick a word " + suggestedWords);
            String chosenWord = sc.nextLine();
            String prevWord = null;
            String[] input = chosenWord.split(" ");
            chosenWord = input[input.length - 1];
            if (input.length > 1) {
                prevWord = input[input.length - 2];
            }
            userChooseWord(chosenWord, prevWord);
        }
    }


    public static List<String> inputAnalyzer(String input) {
        String[] prepInput = chunkInput(input);
        String previousWord = prepInput[0];
        String prefix = prepInput[1];
        List<String> suggestedWords = new ArrayList<>();
        if (previousWord == null & prefix == null) {
            // default list
            return null;
        } else if (previousWord == null & prefix != null) {
            //checkIfCorrect i.e exist in the words_freq or userDictionary
            //check if not in user dic
           /* if (prefix.length() > 2) {
                suggestedWords = generateAllSuggestions(prefix);
                suggestedWords = generateCompactWords(suggestedWords);
            } */
            //else
            suggestedWords = searchWordByPrefix(prefix);
            suggestedWords = generateCompactWords(suggestedWords);


        } else if (previousWord != null) {
            DictionaryWord dictionaryWord = userDictionary.findWord(previousWord);
            List<AssociatedWord> associatedWords = new ArrayList<>();
            if (prefix == null) {
                if (dictionaryWord == null) {
                    System.out.println(previousWord + " default list");
                    return null; //default list here
                }
                associatedWords = dictionaryWord.associatedWords;
                if (associatedWords == null) {
                    associatedWords = new ArrayList<>();
                    //   return null; //default list
                }
                Collections.sort(associatedWords);
                for (AssociatedWord associatedWord : associatedWords) {
                    String word = associatedWord.word;
                    suggestedWords.add(word);
                }
            } else {
                //check associated words that start with prefix
                if (dictionaryWord == null) {
                    associatedWords = new ArrayList<>();
                } else {
                    associatedWords = dictionaryWord.associatedWords;
                }
                List<String> finalSuggestedWords1 = suggestedWords;
                associatedWords.stream().filter(associatedWord -> associatedWord.word.startsWith(prefix)).
                        forEach(associatedWord -> finalSuggestedWords1.add(associatedWord.word));
                suggestedWords = finalSuggestedWords1;
            }
        }
        if (suggestedWords.size() < SUGGESTED_LIMIT)

        {
            List<String> complementaryList = new ArrayList<>();
            if (prefix != null) {
                //intensive
                complementaryList = searchWordByPrefix(prefix);
            }
            List<String> finalSuggestedWords = suggestedWords;
            suggestedWords.addAll(complementaryList.stream().
                    filter(s -> finalSuggestedWords.contains(s))
                    .collect(Collectors.toList()));
            suggestedWords = complementaryList;
            suggestedWords = suggestedWords.
                    subList(0, suggestedWords.size() < SUGGESTED_LIMIT ? suggestedWords.size() : SUGGESTED_LIMIT);
        }
        suggestedWords.add(0, prefix);
        return suggestedWords;


    }

    public static void userChooseWord(String chosenWord, String prevWord) throws IOException {
        //i.e one word
        DictionaryWord chosenDictionaryWord = userDictionary.findWord(chosenWord);
        String[] chunkInput = chunkInput(prevWord + " " + chosenWord);

        if (chunkInput[0] == null & chunkInput[1] != null) {
            if (chosenDictionaryWord == null) {
                chosenDictionaryWord = new DictionaryWord(chosenWord, 1, 1);
                userDictionary.addWord(chosenDictionaryWord);
            } else {
                userDictionary.addWord(chosenDictionaryWord);
            }

        } else if (chunkInput[0] != null & chunkInput[1] != null) {
            DictionaryWord prevDictionaryWord = userDictionary.findWord(prevWord);
            if (prevDictionaryWord == null) {
                prevDictionaryWord = new DictionaryWord(prevWord, 1, 1);
            }

            prevDictionaryWord.addAssociateWords(new AssociatedWord(chosenWord, 1));
            userDictionary.addWord(prevDictionaryWord);
            if (chosenDictionaryWord == null) {
                chosenDictionaryWord = new DictionaryWord(chosenWord, 1, 1);
                userDictionary.addWord(chosenDictionaryWord);
            } else {
                userDictionary.addWord(chosenDictionaryWord);
            }
        }
        if (prevWord == null) {
            if (chosenDictionaryWord == null) {
                chosenDictionaryWord = new DictionaryWord(chosenWord, 1, 1);
                userDictionary.addWord(chosenDictionaryWord);
            } else {
                userDictionary.addWord(chosenDictionaryWord);
            }
        }

    }

    public static void predictNextLetter(String prefix) {
        String[] prepInput = chunkInput(prefix);
        System.out.println(Arrays.toString(prepInput));
        userDictionary.predictNextLetter(prepInput[0], prepInput[1]);
        if (prepInput.length > 1) {
            spellCorrect.predictNextLetter(prepInput[1]); //updates Letters Table
        } else {
            spellCorrect.predictNextLetter(prefix); //updates Letters Table
        }
    }

    public static String[] chunkInput(String rawInput) {
        /*
        * receiving methods take in (previous word, prefix)
        * I want _   method(want, null)
        * I want y   method(want, y)
        * y          method(null, y)
        * _          method(null, null)
        * */

        String[] output = {null, null};
        boolean islastLastCharSpace = rawInput.charAt(rawInput.length() - 1) == ' ';
        if (rawInput.isEmpty()) {
            return output;
        }
        String[] input = rawInput.split(" ");
        if (input.length > 1) {
            output[0] = input[input.length - 2];//previous
            output[1] = input[input.length - 1]; //prefix
            if (islastLastCharSpace) {
                output[0] = output[1];
                output[1] = null;
            }

        } else if (input[0].equals(rawInput)) {
            output[0] = null;
            output[1] = input[0];
        } else {
            output[0] = input[0];
            output[1] = null;
        }


        return output;
    }

    public static String[] chunkInput(StringBuilder rawInput) {
        return chunkInput(rawInput.toString());
    }

    private static List<String> generateCompactWords(List<String> list) {
        if (list.size() != 0) {
            List<String> finalList = new ArrayList<>();
            //add the word itself
            finalList.add(list.get(0));
            //find if each word in user's dictionary
            for (String s : list) {
                DictionaryWord dictionaryWord = userDictionary.findWord(s);
                if (dictionaryWord != null) {
                    String word = dictionaryWord.word;
                    //get associated words
                    List<AssociatedWord> associatedWords = dictionaryWord.associatedWords;
                    if (associatedWords.size() > 0) {
                        Collections.sort(associatedWords);
                        int assoWordSize = associatedWords.size();
                        int limit = assoWordSize > SUGGESTED_LIMIT / 2 ? SUGGESTED_LIMIT / 2 : assoWordSize;
                        for (int i = 0; i < limit; i++) {
                            finalList.add(word + " " + associatedWords.get(i).word);
                        }
                    }
                }
            }
            for (int i = 0; i < list.size(); i++) {
                String word = list.get(i);
                if (!finalList.contains(word))
                    finalList.add(word);
            }
            int limit = finalList.size() < SUGGESTED_LIMIT ? finalList.size() : SUGGESTED_LIMIT;
            finalList = finalList.subList(0, limit);
            return finalList;
        } else {
            return null;
        }
    }

    private static List<String> generateSuggestionsFromMainDictionary(String word) {
        ArrayList<String> suggestedWords = new ArrayList<>();
        List<PQElement> elements = spellCorrect.correct(word);
        Collections.sort(elements);
        for (PQElement element : elements) {
            String elementWord = element.word;
            suggestedWords.add(elementWord);
        }
        return suggestedWords;
    }

    private static List<String> generateAllSuggestions(String word) {
        List<String> basicSuggestedWords = generateSuggestionsFromMainDictionary(word);
        ArrayList<String> suggestedWords = new ArrayList<>();
        //in the dictionary
        if (userDictionary.findWord(word) != null) {
            basicSuggestedWords.remove(word);
            basicSuggestedWords.add(0, word);
            return basicSuggestedWords;
        } else {
            List<String> foundInUserDictionary = new ArrayList<>();
            for (String elementWord : basicSuggestedWords) {
                if (userDictionary.findWord(elementWord) != null) {
                    foundInUserDictionary.add(elementWord);
                } else {
                    suggestedWords.add(elementWord);
                }
            }
            // order relative to user dictionary
            for (int i = 0; i < foundInUserDictionary.size(); i++) {
                suggestedWords.add(i, foundInUserDictionary.get(i));
            }
        }
        suggestedWords.add(word);
        return suggestedWords;
    }

    public static List<String> searchWordByPrefix(String prefix) {
        String[] prepInput = chunkInput(prefix);
        List<String> mainDic = spellCorrect.searchByPrefix(prefix);
        List<String> userList = generateCompactWords(userDictionary.searchLineByPrefix(prepInput[0], prepInput[1]));
        if (userList == null) {
            userList = new ArrayList<>();
        }
        if (mainDic == null) {
            mainDic = new ArrayList<>();
        }

        List<String> finalUserList = userList;
        mainDic = mainDic.stream().filter(s -> !finalUserList.contains(s)).collect(Collectors.toList());
        int addedPortion = userList.size() > SUGGESTED_LIMIT ? 0 : SUGGESTED_LIMIT - userList.size();
        addedPortion = addedPortion < mainDic.size() ? addedPortion : mainDic.size();
        userList.addAll(mainDic.subList(0, addedPortion));
        return userList;
    }

   /* public static List<String> searchLineByPrefix(String previousWord, String prefix) {
        List<String> userList = userDictionary.searchLineByPrefix(previousWord, prefix);
        List<String> mainDic = spellCorrect.searchByPrefix(prefix);
        int addedPortion = userList.size() > SUGGESTED_LIMIT ? 0 : SUGGESTED_LIMIT - userList.size();
        addedPortion = addedPortion < mainDic.size() ? addedPortion : mainDic.size();
        userList.addAll(mainDic.subList(0, addedPortion));
        return userList;
    }*/

   /* public static List<Character> correctNextLetter(String prefix){

    }*/
}
