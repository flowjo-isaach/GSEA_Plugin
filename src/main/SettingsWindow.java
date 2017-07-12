package main;

import com.flowjo.lib.parameters.*;
import com.treestar.lib.PluginHelper;
import com.treestar.lib.xml.SElement;
import javafx.util.Pair;
import org.apache.http.client.methods.HttpPost;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JList;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by Isaac on 6/29/2017.
 */
public class SettingsWindow extends JPanel implements ActionListener {

    private static SortedListModel listModel = new SortedListModel();
    private ParameterSetMgrInterface mParameterSetManager;
    private static JList<String> selected_genesets = new JList<>(listModel);
    private static int current_analysis_index;
    private boolean pending_changes = false;
    private static Analyses analyses;

    private static JFrame myFrame;
    private static JPanel myPanel;
    private static JDialog myWindow;
    private static JLabel l_analysis_name;
    private static JLabel label_selected_gensets;
    private static JScrollPane scrollPane;
    private static JComboBox c_analysisList;
    private static JButton b_select_genesets;
    private static JButton b_load;
    private static JButton b_rename;
    private static JButton b_delete;
    private static JButton b_submit;
    private static JButton b_close;

    SettingsWindow(SElement fcmlQueryElement) {
        myFrame = new JFrame();
        analyses = Analyses.getInstance();
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mParameterSetManager = PluginHelper.getParameterSetMgr(fcmlQueryElement);

        initializeGeneSets();

        //Displays all contents within the frame
        myWindow = new JDialog(myFrame, "Gene Set Enrichment Analysis", true);
        myWindow.setMinimumSize(new Dimension(550,375));
        myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        myPanel.setBorder(BorderFactory.createEmptyBorder());

        l_analysis_name = new JLabel("Analysis name:");
        constraints.insets = new Insets(20,15,15,15);
        constraints.anchor = GridBagConstraints.LINE_END;
        myPanel.add(l_analysis_name, constraints);

        c_analysisList = new JComboBox();
        c_analysisList.addItem("Add Item...");

        c_analysisList.setEditable(true);
        c_analysisList.addActionListener(this);     //what does this do?
        constraints.gridx = 1;
        constraints.insets = new Insets(15,0,0,15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridwidth = 4;
        myPanel.add(c_analysisList, constraints);

        b_load = new JButton("Load");
        b_load.setPreferredSize(new Dimension(100,25));
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(5,0,15,15);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridwidth = 1;
        myPanel.add(b_load, constraints);

        b_rename = new JButton("Rename");
        b_rename.setPreferredSize(new Dimension(100,25));
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.insets = new Insets(5,0,15,15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridwidth = 1;
        myPanel.add(b_rename, constraints);

        b_delete = new JButton("Delete");
        b_delete.setPreferredSize(new Dimension(100,25));
        constraints.gridx = 3;
        myPanel.add(b_delete, constraints);

        label_selected_gensets = new JLabel("Selected Gene Sets:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(15,15,0,15);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LINE_START;
        myPanel.add(label_selected_gensets, constraints);

        scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(250,200));
        scrollPane.setViewportView(selected_genesets);
        selected_genesets.setSelectionModel(new NoSelectionModel());
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0,15,15,15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridwidth = 2;
        myPanel.add(scrollPane, constraints);

        b_select_genesets = new JButton("Select Gene Sets");

        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.ipadx = 20;
        constraints.insets = new Insets(0,0,0,15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridwidth = 3;
        myPanel.add(b_select_genesets, constraints);

        b_close = new JButton("Close");
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.ipadx = 0;
        constraints.insets = new Insets(0,0,15,15);
        constraints.anchor = GridBagConstraints.LAST_LINE_END;
        constraints.gridwidth = 1;
        myPanel.add(b_close, constraints);

        b_submit = new JButton("Submit");
        b_submit.setPreferredSize(new Dimension(100,25));
        constraints.gridx = 3;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0,0,15,15);
        myPanel.add(b_submit, constraints);

        SetupListeners();

        myWindow.add(myPanel);
        myWindow.pack();
        //Set focus on input field on first run
        c_analysisList.requestFocus();
        myWindow.setVisible(true);
    }

    private boolean AddCurrentItem(JComboBox cbox) {
        boolean retval = false;
        String new_analysis_name = (String) cbox.getEditor().getItem();

        //Ensure field is not empty
        if (new_analysis_name != null && !new_analysis_name.isEmpty()) {
            analyses.setNewAnalysisName(new_analysis_name);

            //ensure the name in the text field is not equal to the "Add item..." option
            if(!new_analysis_name.equals(cbox.getItemAt(cbox.getItemCount() - 1))) {
                //if item selected is "add item..." add item
                if (current_analysis_index == cbox.getItemCount() - 1) {
                    cbox.insertItemAt(new_analysis_name, 0);
                    cbox.setEditable(false);
                    retval = true;
                    //else rename current item
                } else {
                    int count = 0;

                    ComboBoxModel<String> list = cbox.getModel();
                    for (int i = 0; count < 2 && i < list.getSize(); i++)
                        if (list.getElementAt(i).equals(new_analysis_name))
                            count++;

                    if (count <= 1) {
                        cbox.removeItemAt(current_analysis_index);
                        cbox.insertItemAt(new_analysis_name, current_analysis_index);
                        cbox.setEditable(false);
                        retval = true;
                    }
                    //this code is not reachable for an unknown reason. It appears JComboBox doesn't allow duplicate entries
//                    else {
//                        new Display_Message("Disclaimer", "\"".concat(new_analysis_name.concat("\" already exists")));
//                    }
                }
                current_analysis_index = 0;
            } else
                new Display_Message("Disclaimer", "\"".concat(new_analysis_name.concat("\" is not valid")));
        }

        return retval;
    }

    static void UpdateSelectedGeneSets(JList<String> list) {
        AnalysisMember analysis = new AnalysisMember();
        analysis.setAnalysisName(analyses.getCurrentAnalysisName());

        Iterator gene_it = analyses.getAllGeneSets().iterator();
        ListModel<String> model = selected_genesets.getModel();
        while(gene_it.hasNext()) {
            Pair<String, List<String>> pr = (javafx.util.Pair)gene_it.next();
            for(int i = 0; i < model.getSize(); i++)
                if(model.getElementAt(i).equals(pr.getKey()))
                    analysis.addGeneSet(pr);
        }

        if(!analyses.doesExist(analysis) )
            analyses.addAnalysis(analysis);

        ListModel element = list.getModel();
        listModel.clear();
        for(int i = 0; i < element.getSize(); i++){
            listModel.add(element.getElementAt(i));
        }
    }

    private void initializeGeneSets() {
        Iterator param_it = this.mParameterSetManager.getAllParameterSets().iterator();

        while(param_it.hasNext()) {
            ParameterSetInterface set = (ParameterSetInterface)param_it.next();
            analyses.addGeneSet(new Pair<>(set.getName(), set.getParameterNames()));
        }
    }

//    private void initializeGeneSets() {
//        ArrayList<String> collections = new ArrayList(this.mParameterSetManager.getParameterSetCollectionNames());
//        collections.add(0, "All");
//
//        Collection<ParameterSetInterface> sets = new ArrayList();
//        ParameterSetInterface allSet = this.mParameterSetManager.getParameterSet("All");
//
//        if(allSet != null) {
//            ((Collection)sets).add(allSet);
//            Iterator var6 = this.mParameterSetManager.getAllParameterSets().iterator();
//
//            while(var6.hasNext()) {
//                ParameterSetInterface set = (ParameterSetInterface)var6.next();
//                if(!set.getName().equals("All")) {
//                    gene_sets.add(new Pair<>(set.getName(), set.getParameterNames()));
//                }
//            }
//
//            System.out.println("sets: ".concat(sets.toString()));
//            System.out.println("genes".concat(gene_sets.toString()));
//        } else {
//            sets = this.mParameterSetManager.getParameterSets();
//        }
//    }

    private void SendEnrichrRequest(List<String> all_genes) {
        Enricher_Request enricher = null;
        try {
            enricher = new Enricher_Request();
        } catch (IOException | UrlUnavailableException e1) {
            e1.printStackTrace();
        }

        //Add genes for enrichr request
        for(String gene : all_genes)
            enricher.add_gene(gene);

        HttpPost request = enricher.prepare_request();
        try {
            if(request != null)
                enricher.send_request(request);
        } catch (IOException | UrlUnavailableException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    private void LoadCSV() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter( "CSV files", "csv");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File("./plugins/GSEA/"));
        int retval = chooser.showOpenDialog(getParent());

        if(retval == JFileChooser.APPROVE_OPTION){
            Pattern analysis_regex = Pattern.compile("\\[(.+)\\]");
            Pattern geneset_regex = Pattern.compile("\\((.+)\\)");
            try {
                String analysis_name;
                String geneset_name;
                String gene_name;
                FileReader reader = new FileReader(new File("./plugins/GSEA/" + chooser.getSelectedFile().getName()));

                BufferedReader in_stream = new BufferedReader(reader);

                while((analysis_name = in_stream.readLine()) != null && (analysis_regex.matcher(analysis_name)).find()) {
                    AnalysisMember analysis = new AnalysisMember();
                    analysis_name = analysis_name.replaceAll("[\\[\\]]", "");
                    analysis.setAnalysisName(analysis_name);

                    while ((geneset_name = in_stream.readLine()) != null && geneset_regex.matcher(geneset_name).find()) {
                        List<String> gene_list = new ArrayList<>();
                        geneset_name = geneset_name.replaceAll("[\\(\\)]", "");

                        while ((gene_name = in_stream.readLine()) != null && !gene_name.equals("---END_GENE_SET---"))
                            gene_list.add(gene_name);

                        analysis.addGeneSet(new Pair<>(geneset_name, gene_list));
                    }
                    analyses.addAnalysis(analysis);
                    SetupComboList();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void SaveCSV(String filename) {
        FileWriter writer = null;
        try {
            File dir = new File("./plugins/GSEA/");

            if(dir.exists())
                dir.mkdirs();

            writer = new FileWriter("./plugins/GSEA/" + filename + ".csv");
            for(AnalysisMember analysis: analyses.getAnalyses()) {
                writer.append("[".concat(analysis.getAnalysisName()).concat("]\n"));
                for(Pair geneset: analysis.getGene_sets()) {
                    writer.append("(".concat((String)geneset.getKey()).concat(")\n"));
                    for(String gene: (List<String>)geneset.getValue()) {
                        writer.append(gene.concat("\n"));
                    }
                    writer.append("---END_GENE_SET---\n");
                }
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

    private void SetupComboList() {
        c_analysisList.removeAllItems();
        c_analysisList.addItem("Add Item...");

        for(AnalysisMember mem : analyses.getAnalyses())
            c_analysisList.insertItemAt(mem.getAnalysisName(),0);

        c_analysisList.setSelectedIndex(0);
        c_analysisList.setEditable(false);
    }

    private void SetupListeners() {
        c_analysisList.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(event.getKeyChar() == KeyEvent.VK_ENTER) {
                    AddCurrentItem(c_analysisList);
                } else if (event.getKeyChar() == KeyEvent.VK_ESCAPE)
                    c_analysisList.setEditable(false);
            }
        });

        c_analysisList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {SwingUtilities.invokeLater(() -> c_analysisList.getEditor().selectAll());}

            @Override
            public void focusLost(FocusEvent e) {}
        });

        c_analysisList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    c_analysisList.setEditable(true);
                    c_analysisList.grabFocus();
                }
            }
        });

        c_analysisList.addActionListener(e -> {
            //if "Add item..." is selected
            if(c_analysisList.getSelectedIndex() == c_analysisList.getItemCount() - 1) {
                c_analysisList.setEditable(true);
                c_analysisList.grabFocus();
            }
            if(c_analysisList.getSelectedIndex() != -1) {
                current_analysis_index = c_analysisList.getSelectedIndex();

                //ensure the current index isn't "Add Item..."
                if(current_analysis_index != c_analysisList.getItemCount() - 1)
                    analyses.setCurrentAnalysis((String)c_analysisList.getSelectedItem());
            }
        });

        b_select_genesets.addActionListener(e -> {
            if(c_analysisList.isEditable())
                AddCurrentItem(c_analysisList);

            pending_changes = true;
            new GeneSelector(analyses);
        });

        b_load.addActionListener(e -> LoadCSV());

        b_rename.addActionListener(e -> {
            c_analysisList.setEditable(true);
            c_analysisList.requestFocus();
            pending_changes = true;
        });

        b_delete.addActionListener(e -> {
            if(c_analysisList.getSelectedIndex() != c_analysisList.getItemCount() - 1) {
                Display_Message message = new Display_Message("prompt", "Are you sure you would like to delete: ".concat((String)c_analysisList.getItemAt(current_analysis_index)));
                if (message.getResponse() == JOptionPane.OK_OPTION) {
                    pending_changes = true;
                    c_analysisList.removeItemAt(current_analysis_index);
                    c_analysisList.setSelectedIndex(0);
                    analyses.removeAnalysis(analyses.getCurrentAnalysisName());
                    if(c_analysisList.getItemCount() != 1)
                        c_analysisList.setEditable(false);
                }
            }
            else
                new Display_Message("disclaimer", "Cannot delete this item");
        });

        b_close.addActionListener(e -> {
            if (pending_changes) {
                Display_Message prompt = new Display_Message("Prompt", "Would you like to save?");

                if (prompt.getResponse() == JOptionPane.OK_OPTION) {
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
                    chooser.setFileFilter(filter);
                    chooser.setCurrentDirectory(new File("./plugins/GSEA/"));
                    int retval = chooser.showSaveDialog(getParent());

                    if (retval == JFileChooser.APPROVE_OPTION)
                        SaveCSV(chooser.getSelectedFile().getName());
                }
            }
            myWindow.dispose();
        });

        b_submit.addActionListener(e -> {
            Iterator gene_it = analyses.getAllGeneSets().iterator();
            List<String> all_genes = new ArrayList<>();
            ListModel<String> model = selected_genesets.getModel();
            while(gene_it.hasNext()) {
                Pair<String, List<String>> pr = (Pair)gene_it.next();
                for(int i = 0; i < model.getSize(); i++) {
                    if(model.getElementAt(i).equals(pr.getKey()))
                        all_genes = Stream.concat(all_genes.stream(), pr.getValue().stream()).collect(Collectors.toList());
                }
            }
            SendEnrichrRequest(all_genes);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}

class NoSelectionModel extends DefaultListSelectionModel {

    @Override
    public void setAnchorSelectionIndex(final int anchorIndex) {}

    @Override
    public void setLeadAnchorNotificationEnabled(final boolean flag) {}

    @Override
    public void setLeadSelectionIndex(final int leadIndex) {}

    @Override
    public void setSelectionInterval(final int index0, final int index1) { }
}