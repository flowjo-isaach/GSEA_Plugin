package main;

import java.lang.String;
import java.io.File;
import java.util.Collection;
import java.util.List;
import javax.swing.*;

import com.flowjo.lib.parameters.ParameterSetInterface;
import com.flowjo.lib.parameters.ParameterSetMgrInterface;
import com.treestar.lib.PluginHelper;
import com.treestar.lib.core.ExportFileTypes;
import com.treestar.lib.core.ExternalAlgorithmResults;
import com.treestar.lib.core.PopulationPluginInterface;
import com.treestar.lib.xml.SElement;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 07/11/2017
 * Contact: isaach@flowjo.com
 * Description: GSEA is the main class that inherits from PopulationPluginInterface. The only method used is
 * promptForOptions(). Normally, the promptForOptions() method would return true, create a node under the selected
 * population and call the invokeAlgorithm() method. Since there are no calculations sent to SeqGeq, promptForOptions()
 * returns false. This tricks SeqGeq into thinking the plugin failed so it prevents creating a node and doesn't call
 * invokeAlgorithm().
 *
 * Note: Even though this plugin is not related to populations in SeqGeq, as of version 1.0.1, there wasn't a proper
 * plugin interface for GSEA. To compensate, the developer chose the best interface suited for the plugin. Because
 * it's a Population Plugin, the user must choose an arbitrary population in SeqGeq to enable the GSEA plugin.
 **********************************************************************************************************************/
public class GSEA implements PopulationPluginInterface
{
    @Override
    public SElement getElement() {return new SElement(getClass().getSimpleName());}

    @Override
    public Icon getIcon() {return null;}

    @Override
    public String getName() {return null;}

    @Override
    public List<String> getParameters() {return null;}

    /**
     * Method: getVersion
     * Description: returns the version number of the application
     */
    @Override
    public String getVersion() {return "1.0.1";}

    /**
     * Method: ExternalAlgorithmResults
     * Description: Doesn't get called since promptForOptions returns false
     */
    @Override
    public ExternalAlgorithmResults invokeAlgorithm(SElement fcmlElem, File sampleFile, File outputFolder) {return null;}

    /**
     * Method: promptForOptions
     * Description: This method executes when the user selects the GSEA plugin in SeqGeq. This method will return false
     * to prevent unnecessary node creation in the main application.
     */
    @Override
    public boolean promptForOptions(SElement fcmlQueryElement, List<String> parameterNames) {

        //collects all the genes from the workstation
        ParameterSetMgrInterface parameterSetManager = PluginHelper.getParameterSetMgr(fcmlQueryElement);
        Collection<ParameterSetInterface> allGenes = parameterSetManager.getAllParameterSets();

        new SettingsWindow(allGenes);

        //returns false to prevent creating node in SeqGeq. This also prevents calling invokeAlgorithm()
        return false;
    }

    @Override
    public void setElement(SElement elem) {}

    @Override
    public ExportFileTypes useExportFileType() {return null;}
}