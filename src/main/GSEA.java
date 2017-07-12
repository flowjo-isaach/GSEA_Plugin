package main;

import com.flowjo.lib.parameters.ParameterSetMgrInterface;
import com.treestar.lib.PluginHelper;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.lang.String;
import java.net.URISyntaxException;

import java.io.File;
import java.util.List;

import javax.swing.*;

import com.treestar.lib.core.ExportFileTypes;
import com.treestar.lib.core.ExternalAlgorithmResults;
import com.treestar.lib.core.PopulationPluginInterface;
import com.treestar.lib.xml.SElement;

public class GSEA implements PopulationPluginInterface
{
    @Override
    public SElement getElement() { return new SElement(getClass().getSimpleName()); }

    @Override
    public Icon getIcon() { return null; }

    @Override
    public String getName() { return "This_is_my_plugin_name"; }

    @Override
    public List<String> getParameters() {return null;}

    @Override
    public String getVersion()
    {
        return "1.0";
    }

    /**
     * Invokes the algorithm and returns the results.
     */
    @Override
    public ExternalAlgorithmResults invokeAlgorithm(SElement fcmlElem, File sampleFile, File outputFolder) {
        ExternalAlgorithmResults results = new ExternalAlgorithmResults();
//        try {
//            call_enricher();
//        } catch (IOException | URISyntaxException | UrlUnavailableException e) {
//            e.printStackTrace();
//        }
        return results;
    }

    @Override
    public boolean promptForOptions(SElement fcmlQueryElement, List<String> parameterNames) {
        SettingsWindow settings = new SettingsWindow(fcmlQueryElement);
        return false;
    }

    @Override
    public void setElement(SElement elem) { }

    @Override
    public ExportFileTypes useExportFileType()
    {
        return ExportFileTypes.CSV_SCALE;   //CSV_PIR_SCALE ???
    }

    private static void call_enricher() throws IOException, UrlUnavailableException, URISyntaxException {
        Enricher_Request enricher = new Enricher_Request();
        enricher.add_gene("PHF14");
        enricher.add_gene("RBM3");
        HttpPost request = enricher.prepare_request();
        enricher.send_request(request);
    }
}