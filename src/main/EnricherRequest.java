package main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import java.net.URL;
import java.util.StringJoiner;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
/**
 * Created by Isaac on 7/2/2017.
 */


class Enricher_Request {
    final private static int GOOD_RESPONSE = 200;
    private static CloseableHttpClient httpClient = HttpClients.createDefault();
    final private static String enrichr_url = "http://amp.pharm.mssm.edu/Enrichr/addList";
    private static String resulting_url = "http://amp.pharm.mssm.edu/Enrichr/enrich?dataset=";
    private static StringJoiner genes;

    Enricher_Request() throws IOException, URLException {
        try {
            CheckURL(enrichr_url);
        }
        catch (Exception e) {
            new DisplayMessage("Error", e.getMessage());
        }
        genes = new StringJoiner("\n");
    }

    HttpPost prepare_request(String description) {
        String genelist = genes.toString();
        HttpPost post_request = new HttpPost(enrichr_url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("list", genelist);
        builder.addTextBody("description", description);

        //build multipart/form-data type for request
        HttpEntity multipart = builder.build();
        post_request.setEntity(multipart);

        return post_request;
    }

    boolean send_request(HttpPost post_request) throws IOException, URLException, URISyntaxException {
        String new_url = resulting_url;
        //send request to ENRICHR
        CloseableHttpResponse response = httpClient.execute(post_request);
        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity);

        //parse JSON response to JSON object
        JSON_Response json_response = new Gson().fromJson(responseString, JSON_Response.class);

        //retrieve parameter needed to build URL from shortId
        String shortId = json_response.getshortId();

        //Prepare URL
        new_url = new_url.concat(shortId);
        CheckURL(new_url);

        //Send user to prepared URL using their default browser
        Desktop.getDesktop().browse(new URL(new_url).toURI());

        return true;
    }

    void add_gene(String gene) {
        if (gene != null && !gene.isEmpty())
            genes.add(gene);
    }

    private void CheckURL(String url_string) throws IOException, URLException {
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

        if(!match.find())
            throw new MalformedURLException("Invalid url: ".concat(url_string));
        else if(responseCode != GOOD_RESPONSE) //GOOD_RESPONSE code = 200
            throw new URLException("Website unresponsive: ".concat(url_string));
    }
}

class JSON_Response {
    private String shortId;
    private String userListId;
    String getshortId() { return shortId; }
    String getUserListId() { return userListId; }
}

class URLException extends Exception {
    URLException(String message) { super(message); }
}