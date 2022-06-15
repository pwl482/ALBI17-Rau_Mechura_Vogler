package frequentwordsvariants;

import java.time.Duration;
import java.time.Instant;
import java.io.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtilities; 

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class FrequentWordsVariants {

    public static void main(String[] args) throws IOException {     //jfreeChart verlangt eine IOException
        
        //Liste der verschiedenen Sequenzen
        String [] oris = new String [7];
        //V.Cholerae Chr.2
        oris[1] = "CCTGAACGAGTTTCCATAAGGGCTTTAGACATTGGAGGAAAATGCTCATCACGGCGCTCCTCTAATACAATCTCAATTCGATCGGCCTGCACTTGTTGGGTAAGTTGTGCAAGCTGGTAGAGATTCTCTATCGTTTGTTCTCTTTTCATTGCCAATTTCCGTTATTAGGGCTTTAAACAGATTGTACAGCATAGTTTATTTAAAACAACAAAAAGGTGAACATAAAACAATGAATCAAAATCACACATATTGGAGTATTAACAGAAAATTGATACCAAACGAACAAAGTTAAGTATAAAAACCGCGTTTAAATAACCCACATATTCTTCGATAAGGAGAAAACATTTTAAATATTACAGTGTCACTTATTTACAATGTAAAGCCACGTTTTGAAGTGATGATGAATAAATAAAAGCGAGCCGTAAGCGGAACGATTAAACCGAGCCACTAAGTTACGGTGAATGCCATTCTGATTGAAATGATGCGCAGGATTCAAGCAA";
        //V.Cholerae Chr.1
        oris[0] = "CCTGAACGAGTTTCCATAAGGGCTTTAGACATTGGAGGAAAATGCTCATCACGGCGCTCCTCTAATACAATCTCAATTCGATCGGCCTGCACTTGTTGGGTAAGTTGTGCAAGCTGGTAGAGATTCTCTATCGTTTGTTCTCTTTTCATTGCCAATTTCCGTTATTAGGGCTTTAAACAGATTGTACAGCATAGTTTATTTAAAACAACAAAAAGGTGAACATAAAACAATGAATCAAAATCACACATATAGGGTCATTAAATATATATAAAGATCTATATAGAGATCTTTTTATTAGATCTACTATTAAGGAGCAGGATCTTTGTGGATAAGTGAAAAATGATCAACAAGATCATGCGATTCAGAAGGATCAGATCGTGTGATCAACCACTGATCTGTTCAAGGATTAGCTGGGATCAAAAACCTATGTTATACACAGCCACCTTGGGATCTAAAACTTGTTATATGGATAACTATAGGAAGATCACCGGATAATCGTA";
        //T.petrophila
        oris[2] = "AACTCCAGAGTGGGTAGATAAAGAACACATCTCGCTGCTCCACGAAATGGGAGTTGTTTTGAGAAAGAAAAAGGGCATTCAACCTGCGCAGAATCCTATGGAACAGGCCTTTCTCACACTCAGAATAGGATACGATCAGTTCTTCGAAGAAGACTTTGACATGGAATCTTTTGTGAAAGATTTTATGGAAAAATTGAAAAGAATGTATGAGGTCCTCGTTTCAATGTTATAATAAATACCGTGCAAAAACAGTTGGACGAAGGTTCTGATCCCTACAGAACACCTGCCCTGAAATGGTCCCTCAGGAGAATCATCGAGGAGTTAACCGCTCAGGGCTCTTTTTGAAATCTCTCCTATCACTTCATCGATCAGGCTCTTCAGTTGTTTGTTCCCTTTGAGGAGGGAATCTTTGACTCTTTTGACACTGTCTAAAACAACAGGGTGTGACCTGTTGAATTTCTCCGCTATCGTTCTCAGAGAACTATTCAGATGGTTCTTGG";
        //extra examples for Runtime
        oris[3] = "ACGTTGCATGTCGCATGATGCATGAGAGCT";
        oris[4] = "GGATCTCTTTGATCGTGGAAAAACTCGTGCTGTGTTCATGACCGCTGGAGAGTGGTATCAATTCCAGGCCGAATTCCTTGAAGAAGTACGTAAAACTCGGATGAGCCATCACCACTGTTTTTCCGGTGTAGGGAAGGAGAGTTTCGAAAGATTCTCTGATCACGGTGTCCAGTCCGCTTACGATCTCTTCTGCTTTCTGT";
        oris[5] = "CACAGACTTGGAAGATATGGTAGCCACAGAAAAAGTCTCTTGAGAAACCTTTCAAGAGAAATCGTTGAACACGGTTCGATAGTTACCACAACAGCAAAAGCAAAAGCTCTGAAGATATTAATGGATAAACTTGTCAGCAAGGCCATAGAAGCCGCCACAACTGATGATAAAGCCAAAAGCGTTCATCTGAGGAGGCAGATAAACGCTGTGCTTGGAGACAGACGATTGACAAACAAACTTGTGGATGAAATAGCGAAGAACTACGTTGGGAGACACGGAGGCTATGTGAGGGTTCTGAGGATAGGTTTCAGAAGAGGAGATGCCGCTGAGATGTCGCTCGTTCAGCTTGTGGAGGCATCCAGTCAGGAAGGCTGATGCCTCCTTTTTCTTTCCCTCCCTTGTTGCCTTCCATCAGTAGGAGGTGAAAAATTGACAGAGGAACAGAAAGCCATTAGCATATCAGAGCTGGAATCCATGAACATAAAACAGCTCTACGAGATAGCAAAATCCCTGGGTATTCCTCGATACACTTCTATGCGAAAAAGAGACTTGATTTTTGCCATTTTGAAAGCCCAAACCGAATCCACCGGTTATTTTTTCGGTGAAGGTGTACTGGAAATTCATCCTGAAGGTTTTGGTTTTTTGAGAAGGCTCGAGGACAACCTTCTTCCAAGCAACGATGATATATACATATCCCCTTCTCAGATAAGGAAATTCAACCTGAACACGGGAGATATAATTTCCGGGGTTATAAGGAAACCGAAAGAAGGCGAAAAGTACTTCGCCATGATAAAAATCGAAGCGATAAATTATCGACCAGTCGAAGCGGTCAACGACAGAGTAAACTTTGACAACTTGACTCCGGATTACCCCAGAGAACGCTTCATCCTTGAAACCAACCCAAAGATATATTCCACCAGATTAATAGATCTTTTCGCTCCTATTGGGAAGGGACAAAGAGGAATGATCGTGGCACCTCCAAAAGCCGGTAAAACGACTATCCTCAAAGAAATAGCGAACGGTATCGCTGAAAACCACCCGGATACCATAAGAATAATCTTACTGATCGATGAAAGACCGGAAGAGGTTACGGATATAAAAGAGTCAACGAATGCAATCGTCATAGCGGCTCCCTTTGACATGCCCCCAGACAAACAGGTAAAGGTCGCCGAGCTCACCCTGGAAATGGCAAAGAGACTG";
        oris[6] = "TTTTTTGTTTTTGAGAATATGATAGTCGTGATTTCCCACCACGTAATACACGTTGTGTCGTCTGGAAAACTTCTTGAGAGTCTCGAAGACTTCAGAGTGCTTTTTCTCGATTTCGTCGATCACCGTCTCATCGAGTTTTTCCACAACCTCATCGAAAGAGACGAGTCCAATTTCCCTCACCGCGTGACTTTCGAGTATTTCAAATCCATCGCCAACGATGAAGAGTTCAACGTCTTCAGTTTGAGATATGTCTTCGATGAAATTCACAAGCTCTTTATCGAAGAAAAAATCGTCTTTCGCTGAACCATCTCCTATGTGAAGATCGCTGATGAACACCCTCTTTATTTTTTATCACCCCACTGTTCAAAAAACTTTCCGATTCTCATAACCAGAGCCGGTGCCTGAAGTTTCGCTTTCTCACCTTTTGGTACGATGAACGTTTCAAATGGTAAAACTCGTCTGTTGTCCAGTACACCGTTTTCGAGAATCACAATCGCGCAGAATCCTTTCAATTCTCCCTCTCTCAGTTTCTCTATCTTGAAGTATTCGCACTCAAAGTCTTTAAAGTTCTTCTCTATCAAATCTTCGACTTTCCTTTTTTTCATCACTTTGAAAGCCTTCTCTATGTGGAGCTCCCTGCCACGTCCCCAGTCGTAAACCCTGTAGGTGAGGTCGGACGCCTGCTGTACCTCCACGAGAAGCCCACCGGGTCCAAGGGCGTGAACCGTTCCAGCGGGTAAAAAAACGAAATTTCCAGGTTCTATCTCCACCTTCTTCAACGCTTCATTCCAACTGTTGTCTTCAAGTGCCTTCCTGATCTTCTCCGGATCTTCTCCTATGGCGATCTGACCTTTTTCTACGAAATACCACGCTTCTGTTTTACCCCAGGGTTCACTCTCCAGTTCCCGAGCCTCCTCATCGTTTGGATGAACCTGAACAGAAAGCCAGTCCTCAGCAGAAATGAGTTTCACGAGGAGTGGAAAGCGTGGCAATTTCTTTCCAATGAGCTTCTCCATATCCTCGTTGAGATCGAGGCCATTTTCCGCCTCCGTAATGAAAAGTGGATGCCCGGACAAAAGCCATACTTCTCCTATTCTCTCATCTGAACCGAATATCTTGCCAAGACGATAACTACCCCATATCTGCTCTCGCAACTTTGGAAAGACCTTTATCGTCATTCTGAATACCCTCCTACTCTTATTTCACCGTCTCTGTAGTAAAAAACCGCTTTTGTGAGTTCCTTTTCCACACGAAACACCCTCTCGAACATAACAATTTCAACACCGGGATAAACAACCTCCTTTACAACAACGGAGGCGTTTTTAGCCATTTCTTCAGCTACTGTCTTAAGTTTTCTCAATTCCTCTTCATTCTTCTGTATTGAATCTTTGAGATTTATCAAAGTGTTGTTCACCTTCTTCAGAAGCGCTTCTTTGTCAGGTGGGAATTTGTCTTTCAACATTCCCTGGAGTTTTCTGAGTTCAACTAGCAGTTTCGTCAACTTCTGAACGTTCTCTCTGTCGAGAGAGATCTGAGCGAAAAGGAGTTTTATTTTCTCGTTGATCTCAGGGTCAATTCCAACCTCCACCCTCGTCTTAACACCTATGGGAGAACCCAAAAAATAGGTTTCCACCTTCACCCGAGCTATGGTTATGCCACCTCTTATACTTCCTCCTTTACCAGGAACGGTGACATTGATGGCTTTAACCGTGGAGTTCTCTATGCTCTTTTCCACCTGAACTTCTTCGGCCTCTACTTCTGCGTTCTCTATAAAAACGGCTTTGACGGTTTTTTTCGCCTTTACGAACGCCTTCCCTCTTCCTTTTATTCCTCCAGCCGTGATACTTCCATTGAAAGAAATCACCGTTGCCGCTTCTATTACACCTTTCACTGTAATATCTTCCTGAGCTTTTACAACGAAATCTGGTTTCACGTCACCCTTCACAATCACCTTGCCGGGAAAATCTATATTTCCAGTAGAATAGTCCACATTGTCAACT";
                
        int k = 12;
        
        double [] x = new double [oris.length];                     //Länge x- und y- Achse der Laufzeitgraphen
        double [] yfw2 = new double[x.length];
        double [] yffw = new double[x.length];
        double [] yfws = new double[x.length];
        
        for (int i = 0; i < oris.length; i++) {                     //Wertinitialisierung der x-Achse
            x[i] = oris[i].length();
        }
        
        Instant starttime;
        Instant endtime;
        
        String [] names = new String [7];                           //Namen der Sequenzen
        names[1] = "oriC of V. Cholerae Chr.2";
        names[0] = "oriC of V. Cholerae Chr.1";
        names[2] = "oriC of T. Petrophila";
        names[3] = "Rosalind example";
        names[4] = "200b String";
        names[5] = "1200b String";
        names[6] = "2000b String";
        
        
        for (int j = 1; j <= k; j++) {                                                                                                              //Schleife zu Erstellung von Laufzeitgraphen für jedes k

            for (int i = 0; i < oris.length; i++) {                                                                                                 //Algorithmen durchlaufen für jede Sequenz in oris

                System.out.println("Algorithms for the " + names[i] + " with k = " + j + ":\n");

                starttime = Instant.now();                                                                                                          //Timer starten
                FrequentWords2 fw2 = new FrequentWords2();                                                                                          //Objekt fw2 der Klasse FrequentWords2 erstellen
                System.out.println(fw2.findFrequentWords(oris[i], j));                                                                              //fw2 ruft Algrotihmus auf
                endtime = Instant.now();                                                                                                            //Timer stoppen
                Duration durationfw2 = Duration.between(starttime, endtime);                                                                        //Laufzeit speichern
                System.out.println("The FrequentWords Algorithm took " + durationfw2.toMillis() + " Milliseconds to find a solution.\n");           //Laufzeit ausgeben
                yfw2[i] = (double) durationfw2.toMillis();                                                                                          //Laufzeit als y-Wert für Graph speichern

                starttime = Instant.now();                                                                                                          //Timer starten
                FastFrequentWords ffw = new FastFrequentWords();                                                                                    //Objekt ffw der Klasse FrequentWords2 erstellen
                System.out.println(ffw.fastFrequentWords(oris[i], j));                                                                              //ffw ruft Algrotihmus auf
                endtime = Instant.now();                                                                                                            //Timer stoppen
                Duration durationffw = Duration.between(starttime, endtime);                                                                        //Laufzeit speichern
                System.out.println("The FastFrequentWords Algorithm took " + durationffw.toMillis() + " Milliseconds to find a solution.\n");       //Laufzeit ausgeben
                yffw[i] = (double) durationffw.toMillis();                                                                                          //Laufzeit als y-Wert für Graph speichern

                starttime = Instant.now();                                                                                                          //Timer starten
                FrequentWordsSort fws = new FrequentWordsSort();                                                                                    //Objekt fws der Klasse FrequentWords2 erstellen
                System.out.println(fws.frequentWordsSort(oris[i], j));                                                                              //fws ruft Algrotihmus auf
                endtime = Instant.now();                                                                                                            //Timer stoppen
                Duration durationfws = Duration.between(starttime, endtime);                                                                        //Laufzeit speichern
                System.out.println("The FrequentWordsSort Algorithm took " + durationfws.toMillis() + " Milliseconds to find a solution.\n\n");     //Laufzeit ausgeben
                yfws[i] = (double) durationfws.toMillis();                                                                                          //Laufzeit als y-Wert für Graph speichern
            }

            final XYSeries seriesfw2 = new XYSeries("FrequentWords");                   //Objekt der Klasse XYSeries erstellen
            for (int i = 0; i < x.length; i++) {
                seriesfw2.add(x[i], yfw2[i]);                                           //x- und y-Werte hinzufügen
            }

            final XYSeries seriesffw = new XYSeries("FastFrequentWords");               //Objekt der Klasse XYSeries erstellen
            for (int i = 0; i < x.length; i++) {
                seriesffw.add(x[i], yffw[i]);                                           //x- und y-Werte hinzufügen
            }

            final XYSeries seriesfws = new XYSeries("FrequentWordsSort");               //Objekt der Klasse XYSeries erstellen
            for (int i = 0; i < x.length; i++) {
                seriesfws.add(x[i], yfws[i]);                                           //x- und y-Werte hinzufügen
            }

            final XYSeriesCollection dataset = new XYSeriesCollection();                //Objekt dataset der Klasse XYSeriesCollection erstellen und vorherige XYSeries hinzufügen
            dataset.addSeries(seriesfw2);
            dataset.addSeries(seriesffw);
            dataset.addSeries(seriesfws);

            JFreeChart xylineChart = ChartFactory.createXYLineChart(                    //Graphen plotten
                    "FrequentWordsVariants Runtime Development",                        //Überschrift
                    "Length of Sequence (#b)",                                          //x-Achsen Beschriftung
                    "Runtime (Milliseconds)",                                           //y-Achsen Beschriftung
                    dataset,                                                            //Werte aus dataset benutzen
                    PlotOrientation.VERTICAL,
                    true, true, false);

            int width = 640;                                                            //Breite des Bildes
            int height = 480;                                                           //Höhe des Bildes
            File XYChart = new File("FWVRuntime k = " + j + ".jpeg");                   //Names der Datei
            ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);        //als .jpeg speichern

        }
    }
}
