package screenWriter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Created by Dr.Alaa on 8/8/2017.
 */
public class ScreenWriter {
    Robot robot = new Robot();
    public HashMap<String, String> lettersMap;
    public HashMap<String, String> autoCompeletionOptionsMap;
    public HashMap<String, String> settingsMap;

    String[] letters = {"ا", "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "ه", "و", "ي"};
    String[] options = {"1", "2", "3", "4", "5", "sword"};//right the rest here
    String[] settings = {"enter", "shift", "delete", "space"};
    String[] settingsSign = {"horns", "OK", "gun", "yaa_horns"};
    boolean shiftPressed;

    public ScreenWriter() throws AWTException {
        String alphabetClasses = "alph,baa,taa,thaa,geem,hhaa,khaa,daal,tzaal,raa,zaay,seen,sheen,saad,daad,ttaa,zaa,ayin,gheenToBeDeleted,faa,qaaf,kaaf,laam,meem,noon,haa,wau,yaa";
        String arr[] = alphabetClasses.split(",");
        lettersMap = new HashMap<>();
        autoCompeletionOptionsMap = new HashMap<>();
        settingsMap = new HashMap<>();
        for (int i = 0; i < letters.length; i++) {
            lettersMap.put(arr[i], letters[i]);
        }
        for (int i = 0; i < options.length; i++) {
            autoCompeletionOptionsMap.put(arr[i], options[i]);
        }
        for (int i = 0; i < settings.length; i++) {
            settingsMap.put(settingsSign[i], settings[i]);
        }
        System.out.println(lettersMap);
    }


    public void printLetter(int i) {
        String c = letters[i];
        printLetter(c);
    }

    public void printLetter(String c) {
        boolean pressSpace = false;
        String letter = lettersMap.get(c);
        if (letter == null) {
            letter = c;
            pressSpace = true;
        }
        StringSelection selection = new StringSelection(letter);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        this.robot.keyPress(KeyEvent.VK_CONTROL);
        this.robot.keyPress(KeyEvent.VK_V);
        this.robot.keyRelease(KeyEvent.VK_V);
        this.robot.keyRelease(KeyEvent.VK_CONTROL);
        if (pressSpace) {
            clickSpace();
        }
        this.robot.delay(10);
        releaseShift();
    }

    public void deleteLetter() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        releaseShift();
    }

    public void deleteMultipleLetters(int num) {
        for (int i = 0; i < num; i++) {
            deleteLetter();
            robot.delay(10);
        }
    }

    public void clickEnter() {
        releaseShift();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    public void clickSpace() {
        releaseShift();
        robot.keyPress(KeyEvent.VK_SPACE);
        robot.keyRelease(KeyEvent.VK_SPACE);
    }

    public String getAction(String sign) {
        return settingsMap.get(sign);
    }

    public void clickShift() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        shiftPressed = true;
    }

    public void releaseShift() {
        if (shiftPressed) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
        shiftPressed = false;
    }

    public void performKeyboardAction(String sign) {

        String action = settingsMap.get(sign);
        switch (action) {
            case "enter":
                clickEnter();
                break;
            case "delete":
                deleteLetter();
                break;
            case "space":
                clickSpace();
                break;
            case "shift":
                clickShift();
                break;
        }
    }
}
