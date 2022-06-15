package frequentwordsvariants;


import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class FastFrequentWords {                                                //Laufzeit: O(4^k + |Text| * k), schnell für kleine k, bei großen k => doof
    
    
    public ArrayList<String> fastFrequentWords(String text, int k) {
        
        ArrayList<String> frequentPatterns = new ArrayList<>();                 //frequentPatterns(ausgabe) erstellt
        ArrayList<Integer> frequencyArray = new ArrayList<>();                  //frequencyArray-Länge erstellt
        frequencyArray.addAll(computingFrequencies(text, k));                   //frequencyArray wird beschrieben
        
        int maxCount = Collections.max(frequencyArray);                         //maxCount
        
        for (int i = 0; i < Math.pow(4,k); i++) {                               //einfügen der häufigsten pattern in frequentpatterns, Laufzeit: O(4^k)
            if (frequencyArray.get(i) == maxCount) {                            //wenn Nummer von pattern das an i beginnt max. Count hat, dann..., Laufzeit: O(k)
                String pattern = numberToPattern(i, k);                         //...k-mer pattern an Stelle i bis i+k-1 (mit NTP-Fkt. bestimmt), Laufzeit: O(k)
                if (!frequentPatterns.contains(pattern)) {                      //Duplikate verhindern
                    frequentPatterns.add(pattern);                              //pattern einfügen
                }
            }              
        }    
        return frequentPatterns;                                                //Ausgabe der Methode
    }
    
    public ArrayList<Integer> computingFrequencies(String text, int k) {        //Laufzeit: O(4^k + |Text| * k)
                
        ArrayList<Integer> frequencies = new ArrayList<>();                     //frequencies erstellt
        for (int i = 0; i < Math.pow(4, k); i++) {                              //frequencies initialisert mit 0, Laufzeit: O(4^k)
            frequencies.add(0);
        }
        for (int i = 0; i < text.length() - k + 1; i++) {                       //for O(|Text|), ganze Schleife: O(|Text| * k)
            String pattern = text.substring(i , i + k);                         //pattern = subsstring i bis i+k-1
            int j = patternToNumber(pattern);                                   //PatternToNumber, Nummer = j, Laufzeit: O(k)
            frequencies.set(j, frequencies.get(j) + 1);                         //frequencies an Stelle j wird um 1 hochgezählt
        }
        return frequencies;
    }
    
    
    
    
    public String numberToPattern(int index, int k) {                           //Laufzeit: O(k)
        if (k == 1) {
            return numberToSymbol(index);                                       //Abbruchbedingung für rekursive Benutzung von NumberToPattern
        }
        int prefixIndex = index / 4;                                            //neuer Index (eine Base "weniger")
        int r = index % 4;                                                      //Rest zum Übersetzen in NumberToSymbol
        String symbol = numberToSymbol(r);                                      //Übersetzung
        String prefixPattern = numberToPattern(prefixIndex, k - 1);             //rekursiver Aufruf der Fkt.
        String pattern = prefixPattern + symbol;                                //Zusammensetzen der einzelstrings
    return pattern;
    }  
    
    public static String numberToSymbol(int k) {                                //Ausgabe immer in Großbuchstaben
        String base;                                                            //switch-statement zur Übersetzung
        switch (k) {
            case 0: base = "A";
                break;
            case 1: base ="C";
                break;
            case 2: base ="G";
                break;
            case 3: base = "T";
                break;
            default: base = "Invalid Input";                                    //Default-Wert für base-string
                break;
        }
        return base;    
    }  
    
    
    public int patternToNumber(String pattern) {                                //Laufzeit: O(k)
        
        if (pattern.equals("")) {                                               //0-Ausgabe, wenn Pattern leer
            return 0;
        }
        
        pattern = pattern.toLowerCase();                                        //Groß-/Kleinbuchstaben egal in Eingabe, Laufzeit: O(k)
        
        String symbol = pattern.substring(pattern.length() - 1);                //letzte Base im pattern
        String prefix = pattern.substring(0, pattern.length() - 1);             //prefix des patterns
        return 4 * patternToNumber(prefix) + symbolToNumber(symbol);            //Nummer der letzten Base + rekursiver Aufruf der Fkt.
        }
 
    
    public int symbolToNumber(String sym) {
        int number;                                                             //switch-statement zur Übersetzung
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