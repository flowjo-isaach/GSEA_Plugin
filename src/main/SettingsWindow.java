package main;

import com.flowjo.lib.parameters.*;
import com.treestar.lib.PluginHelper;
import com.treestar.lib.xml.SElement;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
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
    private ParameterSetMgrInterface parameterSetManager;
    private static JList<String> selected_genesets = new JList<>(listModel);
    private static int current_analysis_index;
    private boolean pending_changes = false;
    private static Analyses analyses;

    private static JFrame f_main;
    private static JPanel p_main;
    private static JDialog d_main;
    private static JLabel l_analysis_name;
    private static JLabel l_selected_gensets;
    private static JScrollPane sp_main;
    private static JComboBox c_analysisList;
    private static JButton b_select_genesets;
    private static JButton b_load;
    private static JButton b_rename;
    private static JButton b_delete;
    private static JButton b_submit;
    private static JButton b_close;
    private static GridBagConstraints cons_main;

    SettingsWindow(SElement fcmlQueryElement) {
        f_main = new JFrame();
        analyses = Analyses.getInstance();
        f_main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        parameterSetManager = PluginHelper.getParameterSetMgr(fcmlQueryElement);

        initializeGeneSets();

        d_main = new JDialog(f_main, "Gene Set Enrichment Analysis", true);
        d_main.setMinimumSize(new Dimension(550,375));
        p_main = new JPanel();
        p_main.setLayout(new GridBagLayout());

        cons_main = new GridBagConstraints();
        p_main.setBorder(BorderFactory.createEmptyBorder());

        l_analysis_name = new JLabel("Analysis name:");
        cons_main.insets = new Insets(20,15,15,15);
        cons_main.anchor = GridBagConstraints.LINE_END;
        p_main.add(l_analysis_name, cons_main);

        c_analysisList = new JComboBox();
        cons_main.gridx = 1;
        cons_main.insets = new Insets(15,0,0,15);
        cons_main.fill = GridBagConstraints.HORIZONTAL;
        cons_main.anchor = GridBagConstraints.FIRST_LINE_START;
        cons_main.gridwidth = 4;
        p_main.add(c_analysisList, cons_main);

        b_load = new JButton("Load");
        b_load.setPreferredSize(new Dimension(100,25));
        cons_main.gridx = 1;
        cons_main.gridy = 1;
        cons_main.insets = new Insets(5,0,15,15);
        cons_main.fill = GridBagConstraints.NONE;
        cons_main.anchor = GridBagConstraints.LINE_END;
        cons_main.gridwidth = 1;
        p_main.add(b_load, cons_main);

        b_rename = new JButton("Rename");
        b_rename.setPreferredSize(new Dimension(100,25));
        cons_main.gridx = 2;
        cons_main.gridy = 1;
        cons_main.insets = new Insets(5,0,15,15);
        cons_main.fill = GridBagConstraints.HORIZONTAL;
        cons_main.anchor = GridBagConstraints.CENTER;
        cons_main.gridwidth = 1;
        p_main.add(b_rename, cons_main);

        b_delete = new JButton("Delete");
        b_delete.setPreferredSize(new Dimension(100,25));
        cons_main.gridx = 3;
        p_main.add(b_delete, cons_main);

        l_selected_gensets = new JLabel("Selected Gene Sets:");
        cons_main.gridx = 0;
        cons_main.gridy = 1;
        cons_main.insets = new Insets(15,15,0,15);
        cons_main.fill = GridBagConstraints.NONE;
        cons_main.anchor = GridBagConstraints.LINE_START;
        p_main.add(l_selected_gensets, cons_main);

        sp_main = new JScrollPane();
        sp_main.setPreferredSize(new Dimension(250,200));
        sp_main.setViewportView(selected_genesets);
        selected_genesets.setSelectionModel(new NoSelectionModel());
        cons_main.gridy = 2;
        cons_main.weightx = 1;
        cons_main.weighty = 1;
        cons_main.insets = new Insets(0,15,15,15);
        cons_main.fill = GridBagConstraints.BOTH;
        cons_main.anchor = GridBagConstraints.LINE_END;
        cons_main.gridwidth = 2;
        p_main.add(sp_main, cons_main);

        b_select_genesets = new JButton("Select Gene Sets");
        cons_main.gridx = 2;
        cons_main.gridy = 2;
        cons_main.weightx = 0;
        cons_main.weighty = 0;
        cons_main.ipadx = 20;
        cons_main.insets = new Insets(0,0,0,15);
        cons_main.fill = GridBagConstraints.HORIZONTAL;
        cons_main.anchor = GridBagConstraints.PAGE_START;
        cons_main.gridwidth = 3;
        p_main.add(b_select_genesets, cons_main);

        b_close = new JButton("Close");
        cons_main.ipadx = 0;
        cons_main.insets = new Insets(0,0,15,15);
        cons_main.anchor = GridBagConstraints.LAST_LINE_END;
        cons_main.gridwidth = 1;
        p_main.add(b_close, cons_main);

        b_submit = new JButton("Submit");
        b_submit.setPreferredSize(new Dimension(100,25));
        cons_main.gridx = 3;
        cons_main.gridwidth = 2;
        cons_main.insets = new Insets(0,0,15,15);
        p_main.add(b_submit, cons_main);

        UpdateComboList();
        SetupListeners();

        d_main.add(p_main);
        d_main.pack();
        //Set focus on input field for first run
        c_analysisList.requestFocus();
        d_main.setVisible(true);
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

        List<Pair<String, List<String>>> all_genesets = analyses.getAllGeneSets();
        ListModel<String> model = list.getModel();

        for(Pair<String, List<String>> gene_set : all_genesets) {
            for(int i = 0; i < model.getSize(); i++)
                if(model.getElementAt(i).equals(gene_set.getKey()))
                    analysis.addGeneSet(gene_set);
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
        for(ParameterSetInterface set : parameterSetManager.getAllParameterSets()) {
            if(!set.getName().equals("All"))
                analyses.addGeneSet(new Pair<>(set.getName(), set.getParameterNames()));
        }
    }

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

        HttpPost request = enricher.prepare_request(analyses.getCurrentAnalysisName());
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

        if(chooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION){
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

                    while ((geneset_name = in_stream.readLine()) != null && geneset_regex.matcher(geneset_name).find() && !geneset_name.equals("---END_GENE_SET---")) {
                        List<String> gene_list = new ArrayList<>();
                        geneset_name = geneset_name.replaceAll("[\\(\\)]", "");

                        while ((gene_name = in_stream.readLine()) != null && !gene_name.equals("---END_GENE_LIST---"))
                            gene_list.add(gene_name);

                        analysis.addGeneSet(new Pair<>(geneset_name, gene_list));
                    }
                    analyses.addAnalysis(analysis);
                    UpdateComboList();
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

            if(!dir.exists())
                dir.mkdirs();

            writer = new FileWriter("./plugins/GSEA/" + filename + ".csv");
            for(AnalysisMember analysis: analyses.getAnalyses()) {
                writer.append("[".concat(analysis.getAnalysisName()).concat("]\n"));
                for(Pair geneset: analysis.getGeneSets()) {
                    writer.append("(".concat((String)geneset.getKey()).concat(")\n"));
                    for(String gene: (List<String>)geneset.getValue())
                        writer.append(gene.concat("\n"));

                    writer.append("---END_GENE_LIST---\n");
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

    private void UpdateComboList() {
        c_analysisList.removeAllItems();
        c_analysisList.addItem("Add Item...");

        if(analyses.getCount() > 0) {
            for (AnalysisMember mem : analyses.getAnalyses())
                c_analysisList.insertItemAt(mem.getAnalysisName(), 0);
            c_analysisList.setEditable(false);
        }
        else
            c_analysisList.setEditable(true);

        c_analysisList.setSelectedIndex(0);

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
                listModel.clear();
                c_analysisList.setEditable(true);
                c_analysisList.grabFocus();
            }

            //Check if valid item is selected
            if(c_analysisList.getSelectedIndex() != -1) {
                current_analysis_index = c_analysisList.getSelectedIndex();

                //ensure the current index isn't "Add Item..."
                if(current_analysis_index != c_analysisList.getItemCount() - 1) {
                    analyses.setCurrentAnalysis((String) c_analysisList.getSelectedItem());
                    System.out.println((String) c_analysisList.getSelectedItem());
                    AnalysisMember mem = analyses.getCurrentAnalysisMember();
                    listModel.clear();

                    for(Pair<String, List<String>> gene_set : mem.getGeneSets())
                        listModel.add(gene_set.getKey());
                }
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

                    if (retval == JFileChooser.APPROVE_OPTION) {
                        String filename = chooser.getSelectedFile().getName();
                        filename = FilenameUtils.removeExtension(filename);
                        SaveCSV(filename);
                    }
                }
            }
            d_main.dispose();
        });

        b_submit.addActionListener(e -> {
            List<Pair<String, List<String>>> all_genesets = analyses.getAllGeneSets();
            List<String> all_genes = new ArrayList<>();
//            ListModel<String> model = selected_genesets.getModel();

            for(Pair<String, List<String>> geneset: all_genesets) {
                for(int i = 0; i < listModel.getSize(); i++) {
                    if(listModel.getElementAt(i).equals(geneset.getKey()))
                        all_genes = Stream.concat(all_genes.stream(), geneset.getValue().stream()).collect(Collectors.toList());
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