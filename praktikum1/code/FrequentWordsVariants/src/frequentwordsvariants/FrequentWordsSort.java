package frequentwordsvariants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class FrequentWordsSort {                                                                                //Laufzeit: O(n * log(n) * k)
                                                                                                                //Laufzeit: O((|Text| - k)*k)  +  O((|Text|-k)*log(|Text|-k)) + O(|Text|)  +  O(|Text|-k) + O((|Text|-k)*k) )
                                                                                                                //          Zeile ?-?               Zeile ?-?                                   Zeile ?-?
        
    public ArrayList<String> frequentWordsSort(String text, int k) {
           
        ArrayList<String> frequentPatterns = new ArrayList<>();                                                 //frequentPatterns erstellt
        ArrayList<Integer> index = new ArrayList<>();                                                           //index erstellt
        ArrayList<Integer> countList = new ArrayList<>();                                                       //countList erstellt
        
        for (int i = 0; i < text.length() - k + 1; i++) {                                                       //O(|Text| - k + 1)
            String pattern = text.substring(i, i + k);                                                          //pattern = subsstring i bis i+k-1
            index.add(patternToNumber(pattern));                                                                //patternNummer im index speichern, O(k)
            countList.add(1);                                                                                   //countList mit 1 initialisiert, O(|Text| - k + 1)
        }
        
        Collections.sort(index);                                                                                //lexikographische Sortierung der pattern(in Zahlenform), Quicksort, O(n * log(n))
        for (int i = 1; i < text.length() - k + 1; i++) {                                                       //Duplikate entfernen, O(|Text| - k + 1)
            if (Objects.equals(index.get(i), index.get(i - 1))) {                                               //wenn zwei aufeinanderfolgene Elemente gleich sind in index...
                countList.set(i, countList.get(i - 1) + 1);                                                     //...setze Wert des zweiten Elements in countList auf Wert des Ersten
            }
        }        
        
        int maxCount = Collections.max(countList);                                                              //maxCount
                
        for (int i = 0; i < text.length() - k + 1; i++) {                                                       //einfügen der häufigsten pattern in frequentPatterns, O(|Text| - k + 1)
            if (countList.get(i) == maxCount && !frequentPatterns.contains(text.substring(i, i + k))) {         //wenn Nummer von pattern das an i beginnt max. Count hat und pattern noch nicht in frequentPatterns...
                String pattern = numberToPattern(index.get(i), k);                                              //...k-mer pattern an Stelle i bis i+k-1 (mit NTP-Fkt. bestimmt), O(k)
                frequentPatterns.add(pattern);                                                                  //pattern einfügen
            }                
        }            
        return frequentPatterns;                                                                                //Ausgabe der Methode
    }
   
    
    public String numberToPattern(int index, int k) {                                                           //Laufzeit: O(k)
        if (k == 1) {
            return numberToSymbol(index);                                                                       //Abbruchbedingung für rekursive Benutzung von NumberToPattern
        }
        int prefixIndex = index / 4;                                                                            //neuer Index (eine Base "weniger")
        int r = index % 4;                                                                                      //Rest zum Übersetzen in NumberToSymbol
        String symbol = numberToSymbol(r);                                                                      //Übersetzung
        String prefixPattern = numberToPattern(prefixIndex, k - 1);                                             //rekursiver Aufruf der Fkt.
        String pattern = prefixPattern + symbol;                                                                //Zusammensetzen der einzelstrings
    return pattern;
    }  
    
    public static String numberToSymbol(int k) {
        String base;                                                                                            //switch-statement zur Übersetzung
        switch (k) {
            case 0: base = "A";
                break;
            case 1: base ="C";
                break;
            case 2: base ="G";
                break;
            case 3: base = "T";
                break;
            default: base = "Invalid Input";                                                                    //Default-Wert für base-string
                break;
        }
        return base;    
    }  
    
    
    public int patternToNumber(String pattern) {                                                                //Laufzeit: O(k)
        
        if (pattern.equals("")) {                                                                               //0-Ausgabe, wenn Pattern leer
            return 0;
        }
        
        pattern = pattern.toLowerCase();                                                                        //Groß-/Kleinbuchstaben egal in Eingabe, Laufzeit: O(k)
        
        String symbol = pattern.substring(pattern.length() - 1);                                                //letzte Base im pattern
        String prefix = pattern.substring(0, pattern.length() - 1);                                             //prefix des patterns
        return 4 * patternToNumber(prefix) + symbolToNumber(symbol);                                            //Nummer der letzten Base + rekursiver Aufruf der Fkt.
        }
 
    
    public int symbolToNumber(String sym) {
        int number;                                                                                             //switch-statement zur Übersetzung
        switch (sym) {
            case "a": number = 0;
                break;
            case "c": number = 1;
                break;
            case "g": number = 2;
                break;
            case "t": number = 3;
                break;
            case "": number = 3;
                break;    
            default: number = -1;
                break;
        }
        return number;
    }    
    
}
