package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Isaac on 7/11/2017.
 */
public class Analyses {
    private static Analyses singleton = null;
    private static List<AnalysisMember> analyses;
    private static List<Pair<String, List<String>>> all_gene_sets = new ArrayList<>();
    private AnalysisMember current_analysis = null;

    private Analyses() { analyses = new ArrayList<>(); }
    static Analyses getInstance() {
        if(singleton == null)
            singleton = new Analyses();

        return singleton;
    }

    AnalysisMember getCurrentAnalysis() {return current_analysis;}
    List<AnalysisMember> getAnalyses() {return analyses;}
    int getCount() {return analyses.size();}
    List<Pair<String, List<String>>> getAllGeneSets() {return all_gene_sets;}
    String getCurrentAnalysisName() {
        if(current_analysis != null)
            return current_analysis.getAnalysisName();

        return null;
    }

    void addAnalysis(AnalysisMember member) {
        analyses.add(member);
        current_analysis = member;
    }
    void addGeneSet(Pair<String, List<String>> pr) {all_gene_sets.add(pr);}
    boolean renameSelectedAnalysis(String newAnalysisName) {
        for (AnalysisMember mem : analyses) {
            if (mem.getAnalysisName().equals(getCurrentAnalysisName())) {
                mem.setAnalysisName(newAnalysisName);
                return true;
            }
        }
        return false;
    }

    boolean setCurrentAnalysis(String analysis_name) {
        for(AnalysisMember analysis: analyses) {
            if(analysis_name.equals(analysis.getAnalysisName())) {
                current_analysis = analysis;
                return true;
            }
        }
        return false;
    }

    boolean doesExist(AnalysisMember member) {
        for(AnalysisMember analysis: analyses)
            if(member.equals(analysis)) {return true;}

        return false;
    }
    boolean removeAnalysis(String analysis_name) {
        for(AnalysisMember mem : analyses) {
            if(mem.getAnalysisName().equals(analysis_name)) {
                analyses.remove(mem);
                return true;
            }
        }
        return false;
    }

    void clear() { analyses.clear(); }
}
