package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 07/11/2017
 * Contact: isaach@flowjo.com
 * Description: Used as a container to store a single analysis and its gene sets.
 **********************************************************************************************************************/
class AnalysisMember {
    private String AnalysisName;
    private List<Pair<String, List<String>>> gene_sets;

    /**
     * Method: Constructor
     * Description: Initializes private members
     */
    AnalysisMember() {
        AnalysisName = null;
        gene_sets = new ArrayList<>();
    }

    /**
     * Method: getAnalysisName
     * Description: Returns the analysis name
     * @return Analysis name
     */
    String getAnalysisName() {return AnalysisName;}

    /**
     * Method: getGeneSets
     * Description: Returns the gene sets associated with this analysis
     * @return List of gene sets associated with this analysis
     */
    List<Pair<String, List<String>>> getGeneSets() {return gene_sets;}

    /**
     * Method: setAnalysisName
     * Description: setter
     * @param analysisName new name for analysis
     */
    void setAnalysisName(String analysisName) {AnalysisName = analysisName;}

    /**
     * Method: addGeneSet
     * Description: Add a gene set to the analysis
     * @param gene_set gene set to add
     */
    void addGeneSet(Pair<String, List<String>> gene_set) { gene_sets.add(gene_set); }

    /**
     * Method: hasGeneSet
     * Description: Determines if this analysis has a gene set
     * @return true if analysis has gene set
     */
    boolean hasGeneSet() { return gene_sets.size() != 0; }

    /**
     * Method: clearGeneSets
     * Description: Clears gene sets from this analysis
     */
    void clearGeneSets() {gene_sets.clear();}
}