# -*- coding: utf-8 -*-
"""
Created on Wed Feb 28 15:16:35 2018

@author: Tobias, Franziska, Paul
"""
import sys
import pandas as pd
import re
import numpy as np
import pdb
import os
import copy
import time

'''
Klasse um die Distanzmatrizen zu berechnen
input 
    verbosemode = toggle fuer Konsolenoutput, boolean
    seqs = Sequenzen aus dem MSA, [String]
attributes
    self.seqs = entspricht seqs input, [String]
    self.len = Anzahl der Sequenzen, int
    self.verbose = entspricht verbosemode input, boolean
'''
class Distance:
    def __init__(self, seqs, verbosemode):
        self.seqs = seqs
        self.len = len(seqs)
        self.verbose = verbosemode
        
    '''
    Methode fuer die Berechnung der Hamming Distanzmatrix
    output
        dist_matrix = Distanzmatrix zwischen allen Sequenzen,
            pandas DataFrame[int][int]
    '''
    def hamdist(self):
        if self.verbose:
            print("\nComputing Hamming distances\n...\n...")
            start_time = time.time()
        dist_matrix = pd.DataFrame(np.zeros(shape=(self.len, self.len)))
        #Berechnen der oberen Dreiecksmatrix
        for i in range(self.len):
            for j in range(i+1, self.len):
                #jede Position auf Gleichheit pruefen in beiden Sequenzen
                for k in range(len(self.seqs[j])):
                    if not self.seqs[i][k] == self.seqs[j][k]:
                        #bei Match den jeweiligen Score der Sequenzen erhoehen
                        dist_matrix.loc[i, j] += 1
        #Spiegeln der oberen Dreiecksmatrix
        for j in range(self.len):
            for i in range(j+1, self.len):
                dist_matrix.loc[i, j] = dist_matrix.at[j, i]
        if self.verbose:
            elapsed_time = time.time() - start_time
            print("Done")
            print("Time elapsed while computing Hamming distances: " + time.strftime("%H:%M:%S", time.gmtime(elapsed_time)) + "(HH:MM:SS)")
        return dist_matrix
    
    '''
    Methode fuer die Berechnung der BLOSUM Distanzmatrix
    input
        blosum = BLOSUM62 Distanzmatrix, Werte werden fuer die Minimumssuche 
            invertiert und fuer die Distanzen des Baumes ins positive 
            verschoben (Addition des Maximums)
    output
        dist_matrix = Distanzmatrix zwischen allen Sequenzen,
            pandas DataFrame[float][float]
    '''
    def blosumdist(self):
        if self.verbose:
            print("\nComputing Blosum62 distances\n...\n...")
            start_time = time.time()
        dist_matrix = pd.DataFrame(np.zeros(shape=(self.len, self.len)))
        blosum = pd.read_csv("BLOSUM62.txt",sep="\t", index_col=0, header=0)
        #Maximum der Blusummatrix um die Werte positiv zu machen
        blosummax = blosum.max().max()
        #Berechnen der oberen Dreiecksmatrix
        for i in range(self.len):
            for j in range(i+1, self.len):
                blosumscore = 0 
                for k in range(len(self.seqs[j])):
                    #Invertieren der blosumwerte fuer die Minumumssuche sowie alle Werte ins positive verschieben
                    blosumscore += (-1) * blosum.at[self.seqs[i][k], self.seqs[j][k]] + blosummax
                dist_matrix.loc[i, j] = blosumscore
        #Spiegeln der oberen Dreiecksmatrix
        for j in range(self.len):
            for i in range(j+1, self.len):
                dist_matrix.loc[i, j] = dist_matrix.at[j, i]
        if self.verbose:
            elapsed_time = time.time() - start_time
            print("Done")
            print("Time elapsed while computing Blosum62 distances: " + time.strftime("%H:%M:%S", time.gmtime(elapsed_time)) + "(HH:MM:SS)")
        return dist_matrix
'''
Klasse fuer die Berechnung des UPGMA Baumes
input
    scoreMatrix = Matrix aus der vorherigen Distanzmatrixberechnung,
        pandas Dataframe[int/float][int/float]
    meta = Liste der zu verwendenden Metadaten als Tupel, hier Name, Land und Jahr,
        [(String,String,String)]
    verbosemode = toggle fuer Konsolenoutput, boolean
attributes
    self.df = entspricht scoreMatrix input, wird in jedem Schritt ueberschrieben,
        pandas Dataframe[int/float][int/float]
    self.meta = entspricht meta input, [(String,String,String)]
    self.evDistanzes = Speicherung der halbierten Distanzen, [int/float]
    self.evCount = indexer fuer self.evDistances, int
    self.posiList = alle linken Untercluster fuer die Baumerstellung, [String]
    self.posjList = alle rechten Untercluster fuer die Baumerstellung, [String]
    self.verbose = entspricht verbosemode input, boolean
'''
class UPGMA:
    def __init__(self, scoreMatrix, meta, verbosemode):
        self.df = copy.deepcopy(scoreMatrix)
        self.meta = meta
        arr = []
        #Bennenung der Spalten mit nummer und Delimiter
        for i in range(scoreMatrix.shape[0]):
            arr += [str(i) + '/']
        self.df.columns = arr
        self.df.index = arr
        self.evDistances = [0]*(len(self.df.columns.tolist()) - 1)
        self.evCount = 0
        self.posiList = []
        self.posjList = []
        self.verbose = verbosemode
    '''
    Funktion zum wiederholen der UPGMA Schritte und zum generieren der Outputfiles
    input
        outname = Name des Outputfiles, String
    '''
    def runUPGMA(self, outname):
        if self.verbose:
            print("\nRunning UPGMA for '{}'\n...\n...".format(outname))
            start_time = time.time()
        self.evCount = 0
        #Wiederholung bis die Scorematrix auf 1x1 reduziert ist
        while not self.df.shape[0] == 1:
            self.calcUPGMA()
        if self.verbose:
            elapsed_time = time.time() - start_time
            print("Done")
            print("Time elapsed while running UPGMA for '{}'".format(outname) + ": " + time.strftime("%H:%M:%S", time.gmtime(elapsed_time)) + "(HH:MM:SS)")
            print("\nGenerating NeXuS file '{}'\n...\n...".format(outname))
            start_time = time.time()
        #Liste der hardgecodeten Metadatentitel
        metaList = ["country", "year"]
        for k in range(len(metaList)):
            self.evCount = 0
            #Dictionary zum Speichern vorheriger Clusterschritte
            nexusDict = {}
            lastkey = ""
            for i, j in zip(self.posiList, self.posjList):
                #Fall fuer zwei existente Cluster
                if nexusDict.has_key(i) and nexusDict.has_key(j):
                    nexusDict['(' + i + j + ')'] = ('(' + nexusDict.get(i)[0] + ':' + str(self.evDistances[self.evCount] - nexusDict.get(i)[1]) + ",\n\t" + nexusDict.get(j)[0] + ':' + str(self.evDistances[self.evCount] - nexusDict.get(j)[1]) + ')', self.evDistances[self.evCount])
                #Fall fuer einen rechten Einzelwert an ein bestehendes Cluster
                elif nexusDict.has_key(i):
                    metatwo = self.meta[int(j.split("/")[0])]
                    nexusDict['(' + i + j + ')'] = ('(' + nexusDict.get(i)[0] + ':' + str(self.evDistances[self.evCount] - nexusDict.get(i)[1]) + ",\n\t{}[&{}='{}']:{})".format(metatwo[0],metaList[k], metatwo[k+1], self.evDistances[self.evCount]), self.evDistances[self.evCount])
                #Fall fuer einen linken Einzelwert an ein bestehendes Cluster
                elif nexusDict.has_key(j):
                    metaone = self.meta[int(i.split("/")[0])]
                    nexusDict['(' + i + j + ')'] = ("({}[&{}='{}']:{},\n\t".format(metaone[0], metaList[k], metaone[k+1], self.evDistances[self.evCount]) + nexusDict.get(j)[0] + ':' + str(self.evDistances[self.evCount] - nexusDict.get(j)[1]) +')', self.evDistances[self.evCount])
                #Fall fuer zwei nicht-Cluster
                else:
                    metaone = self.meta[int(i.split("/")[0])]
                    metatwo = self.meta[int(j.split("/")[0])]
                    nexusDict['(' + i + j + ')'] = ("({}[&{}='{}']:{},\n\t{}[&{}='{}']:{})".format(metaone[0], metaList[k], metaone[k+1], self.evDistances[self.evCount], metatwo[0], metaList[k], metatwo[k+1], self.evDistances[self.evCount]), self.evDistances[self.evCount])
                lastkey = '(' + i + j + ')'
                self.evCount += 1
            #Schreiben des Ausgabefiles
            nexusList = "#NEXUS\n\nBEGIN TREES;\n\tTREE TREE1 =\n\t" + nexusDict.get(lastkey)[0] + "\n\t;\nEND;"
            file = open("{}.nexus".format(outname + metaList[k]),"w") 
            file.write(nexusList)
            file.close() 
        if self.verbose:
            elapsed_time = time.time() - start_time
            print("Done")
            print("Time elapsed while generating NeXuS file '{}'".format(outname) + ": " + time.strftime("%H:%M:%S", time.gmtime(elapsed_time)) + "(HH:MM:SS)")

    '''
    Funktion fuer den einzelnen UPGMA Clusterschritt, die Minimumsberechnung
    und die Neuberechnung der Distanzen
    '''
    def calcUPGMA(self):
        minimum = sys.maxint
        posi = ""
        posj = ""
        #Berechnung des Minimums der Matrix
        for i in self.df.columns:
            for j in self.df.index:
                if (minimum > self.df[i][j]) and not (i == j):
                    minimum = self.df[i][j]
                    posi = i
                    posj = j
        #Zwischenspeichern des Clusterschrittes
        self.posiList += [posi]
        self.posjList += [posj]
        self.evDistances[self.evCount] = (self.df[posi][posj] / 2)
        self.evCount += 1
        #neue Matrix mit den neuen Distanzen erzeugen
        tmpMatrix = copy.deepcopy(self.df[self.df.columns.difference([posi,posj])])
        tmpMatrix = tmpMatrix.drop([posi,posj], axis=0)
        tmpMatrix['(' + posi + posj + ')'] = [0]*(self.df.shape[0] - 2)
        x = pd.Series([0]*(self.df.shape[0] - 1))
        x.name = '(' + posi + posj + ')'
        x.index = tmpMatrix.columns.tolist()
        tmpMatrix = tmpMatrix.append(x)
        for i in tmpMatrix.columns:
            for j in tmpMatrix.index:
                if ((i == ('(' + posi + posj + ')')) ^ (j == ('(' + posi + posj + ')'))):
                    isplit = re.findall(r"[\w']+", posi)
                    jsplit = re.findall(r"[\w']+", posj)
                    if i == ('(' + posi + posj + ')'):
                        tmpMatrix.loc[i, j] = (len(isplit) * self.df.at[posi,j] + len(jsplit) * self.df.at[posj,j]) / (len(isplit) + len(jsplit))
                    else:
                        tmpMatrix.loc[i, j] = (len(isplit) * self.df.at[posi,i] + len(jsplit) * self.df.at[posj,i]) / (len(isplit) + len(jsplit)) 
        self.df = copy.deepcopy(tmpMatrix)
                
            
if __name__ == "__main__":
    prog_start_time = time.time()
    verbose_mode = True
    fname = "MSA_FASTA.fa"
    sname = "swine_roh_FASTA.fa"
    #Einlesen der Files
    if os.path.isfile(fname) and os.path.isfile(sname):
        with open(fname) as f:
            msa = f.readlines()
        raw_name = [x.strip() for x in msa if '>' in x]
        msa = [x.strip() for x in msa if not '>' in x]
        with open(sname) as f:
            raw_meta = f.readlines()
        raw_meta = [x.strip() for x in raw_meta if '>' in x]
    else:
        print("Error: one of the files not found.")
        msa = None
        raw_meta = None
    #Distanzmatrix Berechnung
    dist = Distance(msa, verbose_mode)
    scoreMatrix = dist.hamdist()
    blosumMatrix = dist.blosumdist()
    new_meta = [(y.split("|")[1].split("[")[0].replace(" ", ""), x.split(" ")[1], x.split(" ")[3]) for x,y in zip(raw_meta, raw_name)]
    elapsed_time = time.time() - prog_start_time
    print("\nTotal time elapsed while processing distance matrices: " + time.strftime("%H:%M:%S", time.gmtime(elapsed_time)) + "(HH:MM:SS)")
    #UPGMA Berechnung
    upgma = UPGMA(scoreMatrix, new_meta, verbose_mode)
    upgma.runUPGMA("HammingNeXuS")
    upgma2 = UPGMA(blosumMatrix, new_meta, verbose_mode)
    upgma2.runUPGMA("BlosumNeXuS")
    elapsed_time = time.time() - prog_start_time
    print("\nTotal time elapsed while running programm: " + time.strftime("%H:%M:%S", time.gmtime(elapsed_time)) + "(HH:MM:SS)")