package main;

import javafx.util.Pair;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GSEAManager {

    private Analyses analyses;

    GSEAManager(Analyses analyses) { this.analyses = analyses; }

    void SendEnrichrRequest(List<String> all_genes) {
        Enricher_Request enricher = null;
        try {
            enricher = new Enricher_Request();
        } catch (IOException | UrlUnavailableException e1) {
            e1.printStackTrace();
        }

        //Add genes for enrichr request
        for (String gene : all_genes)
            enricher.add_gene(gene);

        HttpPost request = enricher.prepare_request(analyses.getCurrentAnalysisName());
        try {
            if (request != null)
                enricher.send_request(request);
        } catch (IOException | UrlUnavailableException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

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
                }
                return true;

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    void SaveCSV(String filename) {
        FileWriter writer = null;

        try {
            writer = new FileWriter("./plugins/GSEA/" + filename + ".csv");
            for (AnalysisMember analysis : analyses.getAnalyses()) {
                writer.append("[".concat(analysis.getAnalysisName()).concat("]\n"));
                if(analysis.hasGeneSet()) {
                    for (Pair geneset : analysis.getGeneSets()) {
                        writer.append("(".concat((String) geneset.getKey()).concat(")\n"));
                        for (String gene : (List<String>) geneset.getValue())
                            writer.append(gene.concat("\n"));

                        writer.append("---END_GENE_LIST---\n");
                    }
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
