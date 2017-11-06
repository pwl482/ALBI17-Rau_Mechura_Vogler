package frequentwordsvariants;

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class Patterncount {

    private final String text;
    private final String pattern;
    
    public Patterncount(String text, String pattern) {
        this.text = text;
        this.pattern = pattern;
    }
    
    public int patCount(){
        int count = 0;
        for (int i = 0; i < (this.text.length()-this.pattern.length()); i++) {
            if (this.text.substring(i, (this.pattern.length())).equals(this.pattern)) {
                count++;
            }
        }
        return count;
    }
}
