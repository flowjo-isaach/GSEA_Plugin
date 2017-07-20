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

    @Override
    public String getVersion() {return "1.0";}

    /**
     * Doesn't get called since promptForOptions returns false
     */
    @Override
    public ExternalAlgorithmResults invokeAlgorithm(SElement fcmlElem, File sampleFile, File outputFolder) {return null;}

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