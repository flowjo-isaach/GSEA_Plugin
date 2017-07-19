package main;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GSEAManager {

    private Analyses analyses;

    GSEAManager() { analyses = Analyses.getInstance(); }
    boolean LoadCSV() {
        analyses.clear();
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File("./plugins/GSEA/"));

        if (chooser.showOpenDialog(chooser.getParent()) == JFileChooser.APPROVE_OPTION) {
            Pattern analysis_regex = Pattern.compile("\\[(.+)\\]");
            Pattern geneset_regex = Pattern.compile("\\((.+)\\)");
            try {
                String analysis_name;
                String geneset_name;
                String gene_name;
                FileReader reader = new FileReader(new File("./plugins/GSEA/" + chooser.getSelectedFile().getName()));

                BufferedReader in_stream = new BufferedReader(reader);

                while ((analysis_name = in_stream.readLine()) != null && (analysis_regex.matcher(analysis_name)).find()) {
                    AnalysisMember analysis = new AnalysisMember();
                    analysis_name = analysis_name.replaceAll("[\\[\\]]", "");
                    analysis.setAnalysisName(analysis_name);

                    while ((geneset_name = in_stream.readLine()) != null && geneset_regex.matcher(geneset_name).find() && !geneset_name.equals("---END_GENE_SET---")) {
                        List<String> gene_list = new ArrayList<>();
                        geneset_name = geneset_name.replaceAll("[\\(\\)]", "");

                        while ((gene_name = in_stream.readLine()) != null && !gene_name.equals("---END_GENE_LIST---"))
                            gene_list.add(gene_name);

                        analysis.addGeneSet(new Pair<>(geneset_name, gene_list));
                    }
                    analyses.addAnalysis(analysis);
                    return true;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    void SaveCSV(String filename) {
        FileWriter writer = null;
        try {
            File dir = new File("./plugins/GSEA/");

            if (!dir.exists())
                dir.mkdirs();

            writer = new FileWriter("./plugins/GSEA/" + filename + ".csv");
            for (AnalysisMember analysis : analyses.getAnalyses()) {
                writer.append("[".concat(analysis.getAnalysisName()).concat("]\n"));
                for (Pair geneset : analysis.getGeneSets()) {
                    writer.append("(".concat((String) geneset.getKey()).concat(")\n"));
                    for (String gene : (List<String>) geneset.getValue())
                        writer.append(gene.concat("\n"));

                    writer.append("---END_GENE_LIST---\n");
                }
                writer.append("---END_GENE_SET---\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
