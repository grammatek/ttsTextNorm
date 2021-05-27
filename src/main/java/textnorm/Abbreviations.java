package textnorm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * This class initializes and stores sets of abbreviations from res/raw abbreviation files.
 */
public class Abbreviations {
    private Set<String> abbreviations = new HashSet<>();
    // nonEndingAbbr are not allowed at the end of a sentence
    private Set<String> nonEndingAbbr = new HashSet<>();

    public Set<String> getAbbreviations() {
        if (abbreviations.isEmpty())
            abbreviations = readAbbrFromFile("abbreviations_general.txt");
        return abbreviations;
    }

    public Set<String> getNonEndingAbbr() {
        if (nonEndingAbbr.isEmpty())
            nonEndingAbbr = readAbbrFromFile("abbreviations_nonending.txt");
        return nonEndingAbbr;
    }

    private Set<String> readAbbrFromFile(String filename) {
        Set<String> abbrSet = new HashSet<>();
        String line = "";
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((line = reader.readLine()) != null) {
                    abbrSet.add(line.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return abbrSet;
    }
}
