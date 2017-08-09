package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.util.Pair;

import com.flowjo.lib.parameters.ParameterSetInterface;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 07/11/2017
 * Contact: isaach@flowjo.com
 * Description: Used as a container to hold all analyses the user has created along with their gene sets.
 **********************************************************************************************************************/
class Analyses {
    private static Analyses singleton = null;
    private static List<AnalysisMember> analyses;
    private static List<Pair<String, List<String>>> all_gene_sets;
    private AnalysisMember current_analysis;

    /**
     * Method: Constructor
     * Description: Private constructor to enforce singleton pattern.
     * @param allGeneSets A collection of all genesets
     */
    private Analyses(Collection<ParameterSetInterface> allGeneSets) {
        analyses = new ArrayList<>();
        all_gene_sets = new ArrayList<>();
        current_analysis = null;
        initializeGeneSets(allGeneSets);
    }

    /**
     * Method: getInstance
     * Description: returns the analysis object
     * @param allGeneSets A collection of all genesets
     * @return Analyses object
     */
    static Analyses getInstance(Collection<ParameterSetInterface> allGeneSets) {
        if (singleton == null)
            singleton = new Analyses(allGeneSets);

        return singleton;
    }

    /**
     * Method: getCount
     * Description: returns the number of analyses
     * @return int, number of analyses
     */
    int getCount() {return analyses.size();}

    /**
     * Method: getAnalyses
     * Description: returns the list of analysis members
     * @return list of analysis members
     */
    List<AnalysisMember> getAnalyses() {return analyses;}

    /**
     * Method: getCurrentAnalysis
     * Description: returns the current analysis member
     * @return current analysis member
     */
    AnalysisMember getCurrentAnalysis() {return current_analysis;}

    /**
     * Method: getAllGeneSets
     * Description: returns a list of all genesets including its name and their genes
     * @return a list of all genesets including its name and their genes
     */
    List<Pair<String, List<String>>> getAllGeneSets() {return all_gene_sets;}

    /**
     * Method: getCurrentAnalysisName
     * Description: returns the current analysis name
     * @return current analysis name
     */
    String getCurrentAnalysisName() {return (current_analysis != null) ? current_analysis.getAnalysisName() : null;}

    /**
     * Method: getCurrentAnalysisName
     * Description: adds an analysis to this analyses object
     * @param member analysis member
     */
    void addAnalysis(AnalysisMember member) {
        analyses.add(member);
        current_analysis = member;
    }

    /**
     * Method: renameSelectedAnalysis
     * Description: renames the selected analysis
     * @param newAnalysisName new name the analysis should be renamed to
     * @return true if name has been renamed successfully
     */
    boolean renameSelectedAnalysis(String newAnalysisName) {
        for (AnalysisMember mem : analyses) {
            if (mem.getAnalysisName().equals(getCurrentAnalysisName())) {
                mem.setAnalysisName(newAnalysisName);
                return true;
            }
        }
        return false;
    }

    /**
     * Method: setCurrentAnalysis
     * Description: Sets the current analysis given an analysis name
     * @param analysis_name currently existing analysis the current
     *                      analysis object should be set to
     * @return null if analysis set failed or the analysis object if it succeeded
     */
    AnalysisMember setCurrentAnalysis(String analysis_name) {
        for(AnalysisMember analysis: analyses) {
            if(analysis_name.equals(analysis.getAnalysisName())) {
                current_analysis = analysis;
                return analysis;
            }
        }
        return null;
    }

    /**
     * Method: doesExist
     * Description: Returns true if analysis currently exists
     * @param member analysis member
     * @return true if the analysis member exists
     */
    boolean doesExist(AnalysisMember member) {
        for(AnalysisMember analysis: analyses)
            if(member.equals(analysis))
                return true;

        return false;
    }

    /**
     * Method: removeAnalysis
     * Description: Removes the analysis specified from the parameter
     * @param analysis_name name of analysis to remove
     * @return true if analysis was removed successfully
     */
    boolean removeAnalysis(String analysis_name) {
        for(AnalysisMember mem : analyses) {
            if(mem.getAnalysisName().equals(analysis_name)) {
                analyses.remove(mem);
                return true;
            }
        }
        return false;
    }

    /**
     * Method: clear
     * Description: Clears the analyses object
     */
    void clear() { analyses.clear(); }

    /**
     * Method: addGeneSet
     * Description: adds a geneset to the all_gene_sets list
     * @param pr a pair including the geneSet name and a list of genes
     */
    private void addGeneSet(Pair<String, List<String>> pr) {all_gene_sets.add(pr);}

    /**
     * Method: initializeGeneSets
     * Description: Inserts all genesets from the collection to this object
     * @param allGeneSets all gene sets
     */
    private void initializeGeneSets(Collection<ParameterSetInterface> allGeneSets) {

        //ensure program is in it's first run
        if(this.getCount() == 0) {
            for (ParameterSetInterface set : allGeneSets) {
                if (!set.getName().equals("All"))
                    this.addGeneSet(new Pair<>(set.getName(), set.getParameterNames()));
            }
        }
    }
}
