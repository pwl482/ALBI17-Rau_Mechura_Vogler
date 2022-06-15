package frequentwordsvariants;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class FrequentWords2 {                                                                               //Laufzeit:O(|Text|^2 * k)
    
    public ArrayList<String> findFrequentWords(String text, int k) {
        
        ArrayList<String> frequentPatterns = new ArrayList<>();                                             //frequentPatterns(ausgabe)
        ArrayList<Integer> countList = new ArrayList<>();                                                   //countList(counts für k-mere)
        for (int i = 0; i < text.length() - k + 1; i++) {                                                   //count für jedes pattern im text, Laufzeit: O(|Text| - k + 1)
            String pattern = text.substring(i, i + k);                                                      //pattern an Stelle (i bis i+k-1)
            countList.add(patternCount2(text, pattern));                                                    //Aufruf PatternCount
        }
        
        int maxCount = Collections.max(countList);                                                          //maxCount

        
        for (int i = 0; i < text.length() - k + 1; i++) {                                                   //einfügen der häufigsten pattern in ArrayList
            if (countList.get(i) == maxCount && !frequentPatterns.contains(text.substring(i,i + k))) {      //einfügen nur von nicht enthaltenen pattern
                frequentPatterns.add(text.substring(i, i + k));
            }
        }
        return frequentPatterns;                                                                            //Ausgabe der Methode
    }
    
    public int patternCount2(String text, String pattern) {                                                 //Laufzeit: O(|Text| - k + 1)
        
        int count = 0;                                                                                      //count-Initialisierung
        for (int j = 0; j < text.length() - pattern.length() + 1; j++) {                                    //für alle substrings des textes
            if ((text.substring(j, j + pattern.length())).equals(pattern)) {                                //bei Gleichheit mit pattern => count++, Laufzeit: O(k)
                count++;        
            }
        }
        return count;
    }      
}