package main;

import java.lang.String;
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
        new SettingsWindow(fcmlQueryElement);
        return false;
    }

    @Override
    public void setElement(SElement elem) {}

    @Override
    public ExportFileTypes useExportFileType() {return null;}
}