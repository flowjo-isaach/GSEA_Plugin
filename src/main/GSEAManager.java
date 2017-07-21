package main;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javafx.util.Pair;

import org.apache.http.client.methods.HttpPost;

class GSEAManager {
    private Analyses analyses;
    private final static int MAX_GENE_LENGTH = 64;

    GSEAManager(Analyses analyses) { this.analyses = analyses; }

    void SendEnrichrRequest(List<String> all_genes) throws IOException, URLException, URISyntaxException {
        Enricher_Request enricher = new Enricher_Request();

        //Add genes for enrichr request
        for (String gene : all_genes)
            enricher.add_gene(gene);

        HttpPost request = enricher.prepare_request(analyses.getCurrentAnalysisName());

        if (request != null)
            enricher.send_request(request);
    }

    boolean LoadCSV() throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setCurrentDirectory(new File("./plugins/GSEA/"));

        analyses.clear();
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(chooser.getParent()) == JFileChooser.APPROVE_OPTION) {
            File file = new File("./plugins/GSEA/" + chooser.getSelectedFile().getName());

            if(ValidateCSV(file)) {
                PopulateAnalyses(file);
                return true;
            } else
                new DisplayMessage("Error", "The file selected is not valid");
        }
        return false;
    }

    private void PopulateAnalyses(File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader in_stream = new BufferedReader(reader);
        Pattern analysis_regex = Pattern.compile("\\[(.+)\\]");
        Pattern geneset_regex = Pattern.compile("\\((.+)\\)");
        String line = null;
        String geneset_name = null;
        AnalysisMember analysis = null;
        List<String> gene_list = null;

        while ((line = in_stream.readLine()) != null) {
            //if an analysis is read e.g. [analysisname], store the previous analysis before adding the next one
            if (analysis_regex.matcher(line).find()) {
                if (analysis != null) {
                    if (geneset_name != null) {
                        analysis.addGeneSet(new Pair<>(geneset_name, gene_list));
                        geneset_name = null;
                        gene_list = null;
                    }
                    analyses.addAnalysis(analysis);
                }

                analysis = new AnalysisMember();
                line = line.replaceAll("[\\[\\]]", "");
                analysis.setAnalysisName(line);

            //else if a gene set is read e.g. (geneset), store the previous gene set before adding the next one
            } else if (geneset_regex.matcher(line).find()) {
                if (analysis != null && gene_list != null)
                    analysis.addGeneSet(new Pair<>(geneset_name, gene_list));
                gene_list = new ArrayList<>();
                geneset_name = line.replaceAll("[\\(\\)]", "");
            //else add a gene to its gene set
            } else {
                if(analysis != null && gene_list != null)
                    gene_list.add(line);
            }
        }

        //Add the last analysis after the file is completely parsed.
        if (analysis != null) {
            if (geneset_name != null)
                analysis.addGeneSet(new Pair<>(geneset_name, gene_list));

            analyses.addAnalysis(analysis);
        }

        reader.close();
    }

    void SaveCSV(String filename) throws IOException {
        FileWriter writer = null;

        writer = new FileWriter("./plugins/GSEA/" + filename + ".csv");
        for (AnalysisMember analysis : analyses.getAnalyses()) {
            writer.append("[" + analysis.getAnalysisName() + "]\n");
            if(analysis.hasGeneSet()) {
                for (Pair geneset : analysis.getGeneSets()) {
                    writer.append("(" + geneset.getKey() + ")\n");
                    for (String gene : (List<String>) geneset.getValue())
                        writer.append(gene.concat("\n"));
                }
            }
        }

        writer.flush();
        writer.close();
    }

    private boolean ValidateCSV(File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader in_stream = new BufferedReader(reader);
        Pattern analysis_regex = Pattern.compile("\\[(.+)\\]");
        Pattern geneset_regex = Pattern.compile("\\((.+)\\)");
        String line = null;

        if((line = in_stream.readLine()) != null) {
            //check to make sure the first line is an analysis name e.g [analysisname]
            if (!analysis_regex.matcher(line).find()) return false;

            do {
                if (line.length() > MAX_GENE_LENGTH) return false;

                if (analysis_regex.matcher(line).find()) {
                    if ((line = in_stream.readLine()) != null) {
                        if (line.length() > MAX_GENE_LENGTH) return false;
                        //if an analysis name was parsed and a geneset exists ensure it's the very next line
                        if (!geneset_regex.matcher(line).find()) return false;
                    }
                }
            } while ((line = in_stream.readLine()) != null);
        }

        return true;
    }
}
