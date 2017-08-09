package main;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import javafx.util.Pair;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 07/03/2017
 * Contact: isaach@flowjo.com
 * Description: This class includes all methods needed to load/save sessions via CSV files, and submitting data to
 * Enrichr via their API.
 **********************************************************************************************************************/
class GSEAManager {
    private Analyses analyses;

    private final static int MAX_GENE_LENGTH = 64;
    final private static int GOOD_RESPONSE = 200;
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    private final static String enrichr_url = "http://amp.pharm.mssm.edu/Enrichr/addList";
    private static String resulting_url = "http://amp.pharm.mssm.edu/Enrichr/enrich?dataset=";
    private static CloseableHttpResponse response;
    /**
     * Method: Constructor
     * Description: Copies a reference to the analyses object in the class locally.
     * @param analyses Analyses object used for storing every analysis
     *                 and their gene sets including their genes
     */
    GSEAManager(Analyses analyses) { this.analyses = analyses; }

    /**
     * Method: saveCSV
     * Description: Copies over all analyses data to a return delimited CSV file.
     * @param filename name of file the user would like to save
     * @throws IOException Thrown from FileWriter
     */
    void saveCSV(String filename) throws IOException {
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

    /**
     * Method: loadCSV
     * Description: Prompts user to select a CSV file within a file browser. Once selected, the file will be validated
     * using the ValidateCSV() method before handing it off to the PopulateAnalyses() method, where the analyses object
     * will be repopulated with the new data.
     * @return boolean
     * @throws IOException Thrown from validateCSV and PopulationAnalyses
     */
    boolean loadCSV() throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setCurrentDirectory(new File("./plugins/GSEA/"));

        analyses.clear();
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(chooser.getParent()) == JFileChooser.APPROVE_OPTION) {
            File file = new File("./plugins/GSEA/" + chooser.getSelectedFile().getName());

            if(validateCSV(file)) {
                populateAnalyses(file);
                return true;
            } else
                new DisplayMessage("Error", "The file selected is not valid");
        }
        return false;
    }

    /**
     * Method: validateCSV
     * Description: Ensures the CSV file chosen is valid. If not, it will return false and display an error message.
     * @param file CSV file used for loading previous session
     * @return boolean
     * @throws IOException thrown by FileReader and BufferedReader
     */
    private boolean validateCSV(File file) throws IOException {
        //Checks the file extension to ensure its valid
        if(!FilenameUtils.getExtension(file.getName()).toLowerCase().equals("csv")) return false;
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

    /**
     * Method: populateAnalyses
     * Description: After validation, the analyses object will be repopulated with the new data.
     * @param file CSV file used for loading previous session
     * @throws IOException thrown from BufferedReader
     */
    private void populateAnalyses(File file) throws IOException {
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

    /**
     * Method: performRequest
     * Description: Prepares an Enrichr request by adding all genes into a single string, builds a multipart
     * form-data object with the gene list and description for request. The HttpPost object is then sent to the
     * sendRequest() method to finish the request.
     * @param all_genes List of genes for Enrichr request
     * @return boolean
     * @throws IOException sendRequest throws this exception
     * @throws URISyntaxException sendRequest throws this exception
     */
    boolean performRequest(List<String> all_genes) throws IOException, URISyntaxException {
        StringJoiner genes = new StringJoiner("\n");

        //Add genes for enrichr request
        for (String gene : all_genes)
            genes.add(gene);

        String prepared_genes = genes.toString();

        HttpPost post_request = new HttpPost(enrichr_url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("list", prepared_genes);
        builder.addTextBody("description", analyses.getCurrentAnalysisName());

        //build multipart/form-data type for request
        HttpEntity multipart = builder.build();
        post_request.setEntity(multipart);

        return sendRequest(post_request);
    }

    /**
     * Method: sendRequest
     * Description: Builds a GSON(Google's version of JSON) object with the response from Enrichr. The one value we
     * want from the response is the shortId. This id will append to a prepared URL, then forward the user over to
     * the Enrichr website with their results.
     * @param post_request HttpPost object used for sending request
     * @return boolean
     * @throws IOException httpClient, EntityUtils and checkURL throws this exception
     * @throws URISyntaxException creating a new URL throws this exception
     */
    private boolean sendRequest(HttpPost post_request) throws IOException, URISyntaxException {
        int retval;
        String new_url = resulting_url;
        //send request to ENRICHR

        waitMessage(post_request);

        if (response == null) {
            new DisplayMessage("error", "Enrichr did not respond");
            return false;
        }

        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity);

        //parse JSON response to JSON object
        JSONResponse json_response = new Gson().fromJson(responseString, JSONResponse.class);

        //retrieve parameter needed to build URL from shortId
        String shortId = json_response.getshortId();

        //Prepare URL
        new_url = new_url.concat(shortId);

        if((retval = checkURL(new_url)) < 0) {
            if (retval == -1) new DisplayMessage("Error", "Invalid URL: " + new_url);
            else if (retval == -2) new DisplayMessage("Error", "Cannot reach: " + new_url);

            return false;
        }
        //Send user to prepared URL using their default browser
        Desktop.getDesktop().browse(new URL(new_url).toURI());

        return true;
    }

    /**
     * Method: checkURL
     * Description: Check a given URL for two things. One is the URL passed to it is a valid URL. Two is it checks
     * for a response code 200. Any other response code will be treated as a failure to respond. If the URL is
     * invalid, an error code of -1 will be returned. If their is no response from the URL, an error code of -2
     * will be returned.
     * @param url_string Name of the url to test
     * @return integer
     * @throws IOException If the url_string is malformed, an exception will be raised
     */
    private int checkURL(String url_string) throws IOException {
        final URL url = new URL(url_string);
        final String url_regex_pattern = "\\b(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        int responseCode = 0;

        //Check URL against regular expression
        Pattern url_regex = Pattern.compile(url_regex_pattern);
        Matcher match = url_regex.matcher(url_string);

        //ensure URL responds
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("HEAD");
        responseCode = huc.getResponseCode();

        if (!match.find()) return -1;
            //GOOD_RESPONSE code = 200
        else if (responseCode != GOOD_RESPONSE) return -2;

        return 0;
    }

    /**
     * Method: waitMessage
     * Description: Displays a waiting message until Enrichr responds
     * @param post_request post object
     */
    private void waitMessage(HttpPost post_request) {
        JFrame frame = new JFrame();

        //creates a background thread. Needed for displaying a wait message until Enrichr responds
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws InterruptedException, IOException {
                response = httpClient.execute(post_request);
                return "true";
            }

            @Override
            protected void done() { frame.dispose(); }
        };
        worker.execute();
        JOptionPane.showMessageDialog(frame, "Waiting for Enrichr Response. This may take a few minutes",
                                      null, JOptionPane.INFORMATION_MESSAGE);
        try {
            worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}

/**
 * Description: Used for Gson. Gson().fromJson() populates an object with the json response. Each key for every
 * key value pair within the json object must have an associated string variable with the same name. After fromJson()
 * executes, all strings will be populated with its associated value.
 */
class JSONResponse {
    private String shortId;
    private String userListId;
    String getshortId() { return shortId; }
    String getUserListId() { return userListId; }
}
