/*
 * Code, ideas and thoughts of this file, if not stated otherwise,
 * are property of the author(s).
 */
package genomeassembler;

import java.util.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;

/**
 *
 * @author Paul Vogler & Tobias Mechura & Franziska Rau
 */
public class DeBruijnGraph {

    private final ArrayList<String> read = new ArrayList<>();                                                   //gegebene reads
    public final int readCount;                                                                                 //# reads
    private final int readLength;                                                                               //Länge der reads
    private final Map<String, ArrayList<String>> pattern;                                                       //Dictionary mit Prefixen und Liste ihrer Suffixe (Rückgabewert von buildPairs() )
    private Map<String, ArrayList<String>> pairs = new HashMap<>();                                             //==pattern (für außerhalb vno buildPairs() )
    private DirectedPseudograph<String, DefaultEdge> graph = new DirectedPseudograph<>(DefaultEdge.class);      //deBruijnGraph
    private final ArrayList<String> contigs = new ArrayList<>();                                                //fertige Contigliste

    public DeBruijnGraph(ArrayList<String> reads) {
        read.addAll(reads);
        readCount = read.size();
        readLength = read.get(0).length();
        pattern = new HashMap<>();
        pairs = buildPairs();
        graph = buildGraph();
        contigs.addAll(findContigs());
    }
    
    public ArrayList<String> getContigs() {                                     //getter für Contigs
        return contigs;
    }
    
    public Map<String, ArrayList<String>> buildPairs() {                        //erstellt Prefix-Suffix Paare für reads
        String prefix;
        for (int i = 0; i < readCount; i++) {
            prefix = read.get(i).substring(0, readLength - 1);                  //Prefix eines reads
            ArrayList<String> suffix = new ArrayList<>();
            suffix.add(read.get(i).substring(1));                               //Suffix desselben reads
            if (!pattern.containsKey(prefix)) {                                 //wenn Prefix noch nicht in Dictionary:
                pattern.put(prefix, suffix);                                    //Füge Prefix und Suffix hinzu
            } else {                                                            //sonst:
                ArrayList<String> sufs = new ArrayList<>();                     //temporäre Liste
                sufs.addAll(pattern.get(prefix));                               //bekannte Suffixe zu sufs
                sufs.addAll(suffix);                                            //neues Suffix zu sufs
                pattern.put(prefix, sufs);                                      //ersetze alte Suffixliste durch neue
            }
        }
        return pattern;
    }

    public DirectedPseudograph<String, DefaultEdge> buildGraph() {                                  //baut Graphen auf
        for (String source : pairs.keySet()) {                                                      //für alle Prefixe:
            graph.addVertex(source);                                                                //fügt als Knoten hinzu
            for (String target : pairs.get(source)) {                                               //für alle Suffixe:
                graph.addVertex(target);                                                            //fügt ale Knoten hinzu
                graph.addEdge(source, target);                                                      //erstellt Kanten von P. -> S.
                if (target.substring(1).equals(source.substring(0, source.length() - 1))) {         //wenn Suffix == einem Prefix:
                    graph.addEdge(target, source);                                                  //erstellt Kante von S. -> P.
                }
            }
        }
        return graph;
    }

    public ArrayList<String> findContigs() {                                                        //== MaximalNonBranchingPath aus der VL
        ArrayList<ArrayList<String>> paths = new ArrayList<>();                                     //Contigliste (Liste von Readlisten)
        for (String v : graph.vertexSet()) {                                                        //für jeden Knoten im Graph:
            if (graph.inDegreeOf(v) != 1 || graph.outDegreeOf(v) != 1) {                            //ist v kein 1-in-1-out Knoten:
                if (graph.outDegreeOf(v) > 0) {                                                     //ist outdegree > 0:
                    for (DefaultEdge e : graph.outgoingEdgesOf(v)) {                                //für jede ausgehende Kante von v:
                        String w = "";
                        w = graph.getEdgeTarget(e);                                                 //w = Kantenziel
                        int iDe = graph.inDegreeOf(w);
                        int oDe = graph.outDegreeOf(w);
                        String edgeRead = v.substring(0) + w.substring(w.length() - 1);             //read ist v + letztes Zeichen von w
                        ArrayList<String> nonBranchingPath = new ArrayList<>();                     //neuer nonBranchingPath/Contig
                        nonBranchingPath.add(edgeRead);                                             //füge read hinzu
                        while (iDe == 1 && oDe == 1) {                                              //solange w 1-in-1-out
                            String u = "";
                            for (DefaultEdge kante : graph.outgoingEdgesOf(w)) {                    //outgoingEdgesOf ist ein set mit 1 element
                                u = graph.getEdgeTarget(kante);                                     //getEdgeTarget braucht aber einen String
                            }                                                                       //deshalb die for-schleife
                            String nextEdgeRead = w.substring(0) + u.substring(u.length() - 1);     //nächster read
                            nonBranchingPath.add(nextEdgeRead);                                     //hinzufügen
                            iDe = graph.inDegreeOf(u);                                              //iDe und oDe aktualisieren
                            oDe = graph.outDegreeOf(u);
                            w = u;                                                                  //u ist neues w
                        }
                        paths.add(nonBranchingPath);                                                //neuen Contig hinzufügen
                    }
                }
            }
        }
        String knot = (String) graph.vertexSet().toArray()[0];                  //erster Knoten im Graph(kann beliebiger sein)
        ArrayList<ArrayList<String>> tarjan = tarjan(knot, graph);              //Aufruf Tarjan-Algorithmus
        System.out.println(tarjan.size());                                      //Ausgabe #Contigs
        paths.addAll(tarjan);                                                   //alle gefundenen Contigs des Tarjan-Algorithmus zu paths hinzufügen

        ArrayList<String> c = new ArrayList<>();
        boolean firstFlag;
        String co = "";
        for (ArrayList<String> cont : paths) {                                  //für jede Contigliste in paths:
            firstFlag = true;
            for (String con : cont) {                                           //für 1.Element con in Liste:
                if (firstFlag) {
                    co = con;                                                   //speichern in Variable
                    firstFlag = false;
                } else {                                                        //für weitere Elemente:
                    co += con.substring(con.length() - 1);                      //füge letzten Buchstaben an Contig an
                }
            }
            c.add(co);                                                          //füge Contig in Liste hinzu
        }
        Collections.sort(c);                                                    //Sortierung
        return c;
    }

    public ArrayList<ArrayList<String>> tarjan(String knot, DirectedPseudograph<String, DefaultEdge> g) {       //Tarjan-Algorithmus nach https://github.com/c00kiemon5ter/Graphs/blob/master/Algos/Tarjan.java
        int index = 0;
        ArrayList<String> stack = new ArrayList<>();                                                            //neuer Stack
        ArrayList<ArrayList<String>> SCC = new ArrayList<>();                                                   //Strongly Connected Components
        Map<String, int[]> vertices = new HashMap<>();                                                          //Knoten-Dictionary
        int[] inLink = new int[2];
        for (String vertex : g.vertexSet()) {
            inLink[0] = index;                                                                                  //1.Element von inLink ist index
            inLink[1] = index;                                                                                  //2.Element von inLink ist lowLink
            vertices.put(vertex, inLink);                                                                       //speicher Kante und inLink
            stack.add(0, vertex);                                                                               //speichert Knoten in stack
        }
        index++;
        for (DefaultEdge edge : g.outgoingEdgesOf(knot)) {                                                      //alle ausgehenden Kanten von knot
            String n = g.getEdgeTarget(edge);
            if (vertices.get(n)[0] == -1) {                                                                     //wenn index ==-1:
                tarjan(n, g);                                                                                   //Tarjan-Algorithmus von(n, g)
                inLink[0] = vertices.get(knot)[0];
                inLink[1] = Math.min(vertices.get(knot)[1], vertices.get(n)[1]);                                //lowLink auf Min. von (knot und n)
                vertices.put(knot, inLink);
            } else if (stack.contains(n)) {
                inLink[0] = vertices.get(knot)[0];
                inLink[1] = Math.min(vertices.get(knot)[1], vertices.get(n)[0]);                                //lowLink auf Min. (von lowLink von knot und index von n)
                vertices.put(knot, inLink);
            }
        }
        if (vertices.get(knot)[1] == vertices.get(knot)[0]) {
            String n;
            ArrayList<String> component = new ArrayList<>();
            do {
                n = stack.remove(0);
                component.add(n);
            } while (n.equals(knot));
            SCC.add(component);
        }
        return SCC;
    }

}
