package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Isaac on 7/11/2017.
 */
class AnalysisMember {
    private String AnalysisName = null;
    private List<Pair<String, List<String>>> gene_sets = new ArrayList<>();

    String getAnalysisName() {return AnalysisName;}
    List<Pair<String, List<String>>> getGeneSets() {return gene_sets;}

    void setAnalysisName(String analysisName) {AnalysisName = analysisName;}
    void addGeneSet(Pair<String, List<String>> gene_set) {gene_sets.add(gene_set);}
}