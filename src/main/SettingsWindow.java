package main;

import com.flowjo.lib.parameters.*;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JList;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by Isaac on 6/29/2017.
 */
public class SettingsWindow extends JPanel implements ActionListener {

    private static SortedListModel listModel = new SortedListModel();
    private static JList<String> selected_genesets = new JList<>(listModel);
    private static int selected_analysis_index;
    private static boolean pending_changes = false;
    private static Analyses analyses;
    private static GSEAManager gsea_man;

    //Swing components
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

    SettingsWindow(Collection<ParameterSetInterface> allParams) {
        analyses = Analyses.getInstance(allParams);
        gsea_man = new GSEAManager(analyses);
        f_main = new JFrame();
        f_main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        d_main = new JDialog(f_main, "Gene Set Enrichment Analysis", true);
        d_main.setMinimumSize(new Dimension(550, 375));
        p_main = new JPanel();
        p_main.setLayout(new GridBagLayout());

        cons_main = new GridBagConstraints();
        p_main.setBorder(BorderFactory.createEmptyBorder());

        l_analysis_name = new JLabel("Analysis name:");
        cons_main.insets = new Insets(20, 15, 15, 15);
        cons_main.anchor = GridBagConstraints.LINE_END;
        p_main.add(l_analysis_name, cons_main);

        c_analysisList = new JComboBox();

        cons_main.gridx = 1;
        cons_main.insets = new Insets(15, 0, 0, 15);
        cons_main.fill = GridBagConstraints.HORIZONTAL;
        cons_main.anchor = GridBagConstraints.FIRST_LINE_START;
        cons_main.gridwidth = 4;
        p_main.add(c_analysisList, cons_main);

        b_load = new JButton("Load");
        b_load.setPreferredSize(new Dimension(100, 25));
        cons_main.gridx = 1;
        cons_main.gridy = 1;
        cons_main.insets = new Insets(5, 0, 15, 15);
        cons_main.fill = GridBagConstraints.NONE;
        cons_main.anchor = GridBagConstraints.LINE_END;
        cons_main.gridwidth = 1;
        p_main.add(b_load, cons_main);

        b_rename = new JButton("Rename");
        b_rename.setPreferredSize(new Dimension(100, 25));
        cons_main.gridx = 2;
        cons_main.gridy = 1;
        cons_main.insets = new Insets(5, 0, 15, 15);
        cons_main.fill = GridBagConstraints.HORIZONTAL;
        cons_main.anchor = GridBagConstraints.CENTER;
        cons_main.gridwidth = 1;
        p_main.add(b_rename, cons_main);

        b_delete = new JButton("Delete");
        b_delete.setPreferredSize(new Dimension(100, 25));
        cons_main.gridx = 3;
        p_main.add(b_delete, cons_main);

        l_selected_gensets = new JLabel("Selected Gene Sets:");
        cons_main.gridx = 0;
        cons_main.gridy = 1;
        cons_main.insets = new Insets(15, 15, 0, 15);
        cons_main.fill = GridBagConstraints.NONE;
        cons_main.anchor = GridBagConstraints.LINE_START;
        p_main.add(l_selected_gensets, cons_main);

        sp_main = new JScrollPane();
        sp_main.setPreferredSize(new Dimension(250, 200));
        sp_main.setViewportView(selected_genesets);
        selected_genesets.setSelectionModel(new NoSelectionModel());
        cons_main.gridy = 2;
        cons_main.weightx = 1;
        cons_main.weighty = 1;
        cons_main.insets = new Insets(0, 15, 15, 15);
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
        cons_main.insets = new Insets(0, 0, 0, 15);
        cons_main.fill = GridBagConstraints.HORIZONTAL;
        cons_main.anchor = GridBagConstraints.PAGE_START;
        cons_main.gridwidth = 3;
        p_main.add(b_select_genesets, cons_main);

        b_close = new JButton("Close");
        cons_main.ipadx = 0;
        cons_main.insets = new Insets(0, 0, 15, 15);
        cons_main.anchor = GridBagConstraints.LAST_LINE_END;
        cons_main.gridwidth = 1;
        p_main.add(b_close, cons_main);

        b_submit = new JButton("Submit");
        b_submit.setPreferredSize(new Dimension(100, 25));
        cons_main.gridx = 3;
        cons_main.gridwidth = 2;
        cons_main.insets = new Insets(0, 0, 15, 15);
        p_main.add(b_submit, cons_main);

        UpdateComboList();
        SetupListeners();

        d_main.add(p_main);
        d_main.pack();


        c_analysisList.requestFocus();
        d_main.setVisible(true);
    }

    private boolean AddCurrentItem() {
        String new_analysis_name = (String) c_analysisList.getEditor().getItem();

        if (ValidateAnalysisName(new_analysis_name)) {
            pending_changes = true;

            //if item selected is "add item..." add item
            if (selected_analysis_index == c_analysisList.getItemCount() - 1) {
                AnalysisMember analysis = new AnalysisMember();
                analysis.setAnalysisName(new_analysis_name);
                analyses.addAnalysis(analysis);
                c_analysisList.insertItemAt(new_analysis_name, 0);
                c_analysisList.setEditable(false);
                //else rename current item
            } else {
                int count = 0;

                ComboBoxModel<String> list = c_analysisList.getModel();
                for (int i = 0; count < 2 && i < list.getSize(); i++)
                    if (list.getElementAt(i).equals(new_analysis_name))
                        count++;

                if (count <= 1) {
                    c_analysisList.removeItemAt(selected_analysis_index);
                    c_analysisList.insertItemAt(new_analysis_name, selected_analysis_index);
                    analyses.renameSelectedAnalysis(new_analysis_name);
                    c_analysisList.setEditable(false);

                }
            }
            selected_analysis_index = 0;
            return true;
        }
        else
            new Display_Message("Error", "Ensure the analysis name is not empty, less than 256 "
                    + "characters and contains valid ASCII characters");

        return false;
    }

    boolean ValidateAnalysisName(String name) {
        //Ensure field is not empty
        if (name != null && !name.isEmpty())
            //Ensure length is less than 256 characters and contain valid ascii characters
            if(name.length() < 256 && name.chars().allMatch(c -> c < 256))
                //ensure the name in the text field is not equal to the "Add item..." option
                if (!name.equals(c_analysisList.getItemAt(c_analysisList.getItemCount() - 1)))
                    return true;

        return false;
    }

    static void UpdateSelectedGeneSets(JList <String> list) {
        AnalysisMember analysis = analyses.getCurrentAnalysis();
        List<Pair<String, List<String>>> all_genesets = analyses.getAllGeneSets();
        ListModel<String> model = list.getModel();

        if(analysis.hasGeneSet())
            analysis.clear();

        for (Pair<String, List<String>> gene_set : all_genesets) {
            for (int i = 0; i < model.getSize(); i++)
                if (model.getElementAt(i).equals(gene_set.getKey()))
                    analysis.addGeneSet(gene_set);
        }

        if (!analyses.doesExist(analysis))
            analyses.addAnalysis(analysis);

        ListModel element = list.getModel();
        listModel.clear();
        for (int i = 0; i < element.getSize(); i++) {
            listModel.add(element.getElementAt(i));
        }
    }

    private void UpdateComboList() {
        c_analysisList.removeAllItems();
        c_analysisList.addItem("Add Item...");

        if (analyses.getCount() > 0) {
            for (AnalysisMember mem : analyses.getAnalyses())
                c_analysisList.insertItemAt(mem.getAnalysisName(), 0);

            c_analysisList.setEditable(false);
        } else
            c_analysisList.setEditable(true);

        c_analysisList.setSelectedIndex(0);
    }

    private void SetupListeners() {
        c_analysisList.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == KeyEvent.VK_ENTER)
                    AddCurrentItem();
                else if (event.getKeyChar() == KeyEvent.VK_ESCAPE)
                    c_analysisList.setEditable(false);
            }
        });

        c_analysisList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> c_analysisList.getEditor().selectAll());
            }

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

        c_analysisList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && c_analysisList.getSelectedIndex() != -1) {
                listModel.clear();
                //if "Add item..." is selected
                if (c_analysisList.getSelectedIndex() == c_analysisList.getItemCount() - 1) {
                    c_analysisList.setEditable(true);
                } else {
                    c_analysisList.setEditable(false);
                    AnalysisMember mem = analyses.setCurrentAnalysis((String)c_analysisList.getSelectedItem());

                    if (mem != null && mem.hasGeneSet())
                        for (Pair<String, List<String>> gene_set : mem.getGeneSets())
                            listModel.add(gene_set.getKey());
                }
                selected_analysis_index = c_analysisList.getSelectedIndex();
            }
        });

        b_select_genesets.addActionListener(e -> {
            if (c_analysisList.isEditable())
                AddCurrentItem();

            new GeneSelector(analyses);
        });

        b_load.addActionListener(e -> {
            if(gsea_man.LoadCSV())
                UpdateComboList();
        });

        b_rename.addActionListener(e -> {
            c_analysisList.setEditable(true);
            c_analysisList.requestFocus();
        });

        b_delete.addActionListener(e -> {
            int selected_index = c_analysisList.getSelectedIndex();

            if (selected_index != c_analysisList.getItemCount() - 1) {
                Display_Message message = new Display_Message("prompt", "Are you sure you would like to delete: ".concat((String) c_analysisList.getItemAt(selected_index)));
                if (message.getResponse() == JOptionPane.OK_OPTION) {
                    pending_changes = true;

                    c_analysisList.removeItemAt(selected_index);
                    analyses.removeAnalysis(analyses.getCurrentAnalysisName());
                    c_analysisList.setSelectedIndex(0);

                    if (c_analysisList.getItemCount() != 1)
                        c_analysisList.setEditable(false);
                }
            } else
                new Display_Message("disclaimer", "Cannot delete this item");
        });

        b_close.addActionListener(e -> {
            if (pending_changes) {
                Display_Message prompt = new Display_Message("Prompt", "Would you like to save?");

                if (prompt.getResponse() == JOptionPane.OK_OPTION) {

                    File dir = new File("./plugins/GSEA/");
                    if (!dir.exists())
                        dir.mkdirs();

                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
                    chooser.setFileFilter(filter);
                    chooser.setCurrentDirectory(new File("./plugins/GSEA/"));

                    if (chooser.showSaveDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
                        String filename = chooser.getSelectedFile().getName();
                        filename = FilenameUtils.removeExtension(filename);
                        gsea_man.SaveCSV(filename);
                    }
                }
            }
            d_main.dispose();
        });

        b_submit.addActionListener(e -> {
            List<Pair<String, List<String>>> all_genesets = analyses.getAllGeneSets();
            List<String> all_genes = new ArrayList<>();

            for (Pair<String, List<String>> geneset : all_genesets) {
                for (int i = 0; i < listModel.getSize(); i++) {
                    if (listModel.getElementAt(i).equals(geneset.getKey()))
                        all_genes = Stream.concat(all_genes.stream(), geneset.getValue().stream()).collect(Collectors.toList());
                }
            }
            gsea_man.SendEnrichrRequest(all_genes);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}

class NoSelectionModel extends DefaultListSelectionModel {

    @Override
    public void setAnchorSelectionIndex(final int anchorIndex) {
    }

    @Override
    public void setLeadAnchorNotificationEnabled(final boolean flag) {
    }

    @Override
    public void setLeadSelectionIndex(final int leadIndex) {
    }

    @Override
    public void setSelectionInterval(final int index0, final int index1) {
    }
}