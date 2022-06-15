/*
 * Code, ideas and thoughts of this file, if not stated otherwise,
 * are property of the author(s).
 */
package genomeassembler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class GenomeAssembler {

    private static final String pathname = "C:\\Users\\Tobias\\Dropbox\\Uni\\Uniskripte\\5. Semester\\Algorithmische Bioinformatik\\Praktika\\ALBI17-Rau_Mechura_Vogler\\praktikum2\\code\\GenomeAssembler\\src\\genomeassembler\\n_deltocephalinicola__reads__100.txt";
//    Rosalind example(funktioniert):
//    private static final String pathname = "C:\\Users\\Tobias\\Desktop\\rosalindinputexample.txt";
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        ArrayList<String> x = new ArrayList<>();
        ArrayList<String> kReads = new ArrayList<>();
        ArrayList<String> contig = new ArrayList<>();
        
        x.addAll(readFile(GenomeAssembler.pathname));                           //x speichert alle reads
        for (int i = 0; i < x.size(); i++) {                                    //kReads speichert k-mer Kompositionen der reads
            String read = x.get(i);
            int order = 89;                                                     //k
            for (int j = 0; j <= read.length() - order; j++) {                  //Kompositions k-mere für jeden read in kReads speichern
                kReads.add(read.substring(j, j + order));
            }
        }
        
        DeBruijnGraph graph = new DeBruijnGraph(kReads);                        //neues Objekt der Klasse DeBruijnGraph, kReads wir ddirekt übergeben
        contig.addAll(graph.getContigs());                                      //alle Contigs von Objekt übergeben
        
        PrintWriter writer = new PrintWriter("contigs.txt", "UTF-8");           //Contigs in FASTA-Format in eine .txt-Datei schreiben
        for (int i = 0; i < contig.size(); i++) {
            writer.println(">CONTIG" + i + "\n" + contig.get(i));
        }
        writer.close();

    }

    private static ArrayList<String> readFile(String pathname) throws IOException {
        File file = new File(pathname);
        Scanner scanner = new Scanner(file);
        ArrayList<String> fileContents = new ArrayList<>();
        boolean firstFlag = true;
        try {
            while (scanner.hasNextLine()) {
                if (firstFlag) {                                                //erste Zeile der Datei wird ignoriert
                    scanner.nextLine();
                    firstFlag = false;
                } 
                else {
                    fileContents.add(scanner.nextLine());
                }
            }
            return fileContents;
        } 
        finally {
            scanner.close();
        }
    }

}
