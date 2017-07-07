package main;

import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.lang.String;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, UrlUnavailableException {
        try {
//            call_enricher();
            new SettingsWindow();
        }
        catch (Exception e){new Display_Message("error", e.getMessage());}
    }

    // *****refactor before deployement*****//
    private static void call_enricher() throws IOException, UrlUnavailableException, URISyntaxException {
        Enricher_Request enricher = new Enricher_Request();
        enricher.add_gene("PHF14");
        enricher.add_gene("RBM3");
        HttpPost request = enricher.prepare_request();
        enricher.send_request(request);
    }
}