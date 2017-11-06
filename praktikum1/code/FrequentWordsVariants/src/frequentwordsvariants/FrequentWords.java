package frequentwordsvariants;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class FrequentWords {

    private final String text;
    private final int k;

    public FrequentWords(String text, int k) {
        this.text = text;
        this.k = k;
    }
    
    public List<String> findPattern(){
        int[] count = new int[5];
        List<String> frequentPatterns = new ArrayList<>();
        for (int i = 0; i < (this.text.length()-k); i++) {
            String pattern = this.text.substring(i, this.k);
            Patterncount c = new Patterncount(this.text, pattern);
            count[i] = c.patCount();
        }
        int max = count[0];
        for (int i = 1; i < count.length; i++) {
            if (count[i] > max) {
                max = count[i];
            }
        }
        for (int i = 0; i < (this.text.length()-k); i++) {
            if (count[i] == max) {
                frequentPatterns.add(this.text.substring(i, k));
            }
        }
        Object[] st = frequentPatterns.toArray();
        for (Object s : st) {
            if (frequentPatterns.indexOf(s) != frequentPatterns.lastIndexOf(s)) {
                frequentPatterns.remove(frequentPatterns.lastIndexOf(s));
             }
        }
        return frequentPatterns;
    }
}
