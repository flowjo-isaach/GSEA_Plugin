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
    private static int current_analysis_index;

    protected Analyses() {
        analyses = new ArrayList<>();
    }
    public static Analyses getInstance() {
        if(singleton == null)
            singleton = new Analyses();

        return singleton;
    }

    public static AnalysisMember getCurrentAnalysis() {return analyses.get(current_analysis_index);}

    public static boolean setCurrentAnalysis(String analysis_name) {
        for(AnalysisMember analysis: analyses) {
            if(analysis_name.equals(analysis.getAnalysisName())) {
                current_analysis_index = analyses.indexOf(analysis);
                return true;
            }
        }
        return false;
    }

    public static List<AnalysisMember> getAnalyses() {return analyses;}
    public static void addAnalysis(AnalysisMember member) {analyses.add(member);}
    public static boolean doesExist(AnalysisMember member) {
        for(AnalysisMember analysis: analyses)
            if(member.equals(analysis)) {return true;}

        return false;
    }
    public static boolean removeAnalysis(String analysis_name) {
        for(AnalysisMember mem : analyses) {
            if(mem.getAnalysisName().equals(analysis_name)) {
                analyses.remove(mem);
                return true;
            }
        }
        return false;
    }

    public static void addGeneSet(Pair<String, List<String>> pr) {
        all_gene_sets.add(pr);
    }

    public static List<Pair<String, List<String>>> getAllGeneSets() {return all_gene_sets;}
}
