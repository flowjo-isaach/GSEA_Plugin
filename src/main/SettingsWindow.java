package main;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.JList;
import javax.swing.filechooser.FileNameExtensionFilter;
import javafx.util.Pair;

import org.apache.commons.io.FilenameUtils;

import com.flowjo.lib.parameters.*;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 06/29/2017
 * Contact: isaach@flowjo.com
 * Description: Displays the main window when selecting the GSEA plugin in SeqGeq. The Java library used for the GUI is
 * Swing. In this menu, the user can manage all analyses they have created. If the user would like to submit their
 * analysis data (gene sets) to Enrichr, they can click the Submit button.
 **********************************************************************************************************************/
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

    /**
     * Method: Constructor
     * Description: The Swing layout type for this plugin is called GridBagLayout. This layout was chosen because it'
     * easy to create GUIs programmatically. This layout uses grid coordinates to place elements
     * (e.g labels, buttons, combo-boxes). Each element uses a GridBagConstraints variable to specify the location,
     * padding, weights etc. Each element can use the same GridBagConstraints variable, but may introduce unwanted
     * behavior if done incorrectly. Review the following guide if you're more interested in this layout:
     * https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
     * @param all_genes All gene sets
     */
    SettingsWindow(Collection<ParameterSetInterface> all_genes) {
        analyses = Analyses.getInstance(all_genes);
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

        updateComboList();
        setupListeners();

        d_main.add(p_main);
        d_main.pack();


        c_analysisList.requestFocus();
        d_main.setVisible(true);
    }

    /**
     * Method: modifyCurrentItem
     * Description: Gets called when a user modifies a list item in the combo-box. The analysis name gets validated before
     * any action is carried out. An error message will display if it's invalid.
     * @return true if modification succeeds
     */
    private boolean modifyCurrentItem() {
        String new_analysis_name = (String) c_analysisList.getEditor().getItem();

        if (validateAnalysisName(new_analysis_name)) {
            pending_changes = true;

            //if item selected is "add item..." add item
            if (selected_analysis_index == c_analysisList.getItemCount() - 1)
                addCurrentItem(new_analysis_name);
            else
                renameCurrentItem(new_analysis_name);

            selected_analysis_index = 0;
            return true;
        }
        else
            new DisplayMessage("Error", "Ensure the analysis name is not empty, less than 256 "
                    + "characters and contains valid ASCII characters");

        return false;
    }

    /**
     * Method: renameCurrentItem
     * Description: Called from ModifyCurrentItem() if the analysis name needs to be renamed.
     * @param new_analysis_name new name for the current analysis
     */
    private void renameCurrentItem(String new_analysis_name) {
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

    /**
     * Method: addCurrentItem
     * Description: Called from ModifyCurrentItem() if the analysis name needs to be added.
     * @param new_analysis_name name of analysis to add
     */
    private void addCurrentItem(String new_analysis_name) {
        AnalysisMember member = new AnalysisMember();
        member.setAnalysisName(new_analysis_name);
        analyses.addAnalysis(member);
        c_analysisList.insertItemAt(new_analysis_name, 0);
        c_analysisList.setEditable(false);
    }

    /**
     * Method: validateAnalysisName
     * Description: Returns true if analysis name is valid. Specifically, the analysis name cannot be blank, be less
     * than 256 characters, or have the same name as the last combo-box item: Add Item... The name must contain
     * valid ASCII characters to ensure proper saving/loading behavior.
     * @param name name of analysis to validate
     * @return true if name is valid
     */
    private boolean validateAnalysisName(String name) {
        //Ensure field is not empty
        if (name != null && !name.isEmpty())
            //Ensure length is less than 256 characters and contain valid ascii characters
            if(name.length() < 256 && name.chars().allMatch(c -> c < 256))
                //ensure the name in the text field is not equal to the "Add item..." option
                if (!name.equals(c_analysisList.getItemAt(c_analysisList.getItemCount() - 1)))
                    return true;

        return false;
    }

    /**
     * Method: updateComboList
     * Description: Updates the combo-list after the main window has loaded or the user has chosen to load data from
     * a previous session.
     */
    private void updateComboList() {
        c_analysisList.removeAllItems();
        c_analysisList.addItem("Add Item...");

        if (analyses.getCount() > 0) {
            for (AnalysisMember member : analyses.getAnalyses())
                c_analysisList.insertItemAt(member.getAnalysisName(), 0);

            c_analysisList.setEditable(false);
        } else
            c_analysisList.setEditable(true);

        c_analysisList.setSelectedIndex(0);
    }

    /**
     * Method: setupListeners
     * Description: Sets up all the Event listeners for most GUI elements in the main window.
     */
    private void setupListeners() {
        c_analysisList.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == KeyEvent.VK_ENTER)
                    modifyCurrentItem();
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
                    AnalysisMember member = analyses.setCurrentAnalysis((String)c_analysisList.getSelectedItem());

                    if (member != null && member.hasGeneSet())
                        for (Pair<String, List<String>> gene_set : member.getGeneSets())
                            listModel.add(gene_set.getKey());
                }
                selected_analysis_index = c_analysisList.getSelectedIndex();
            }
        });

        b_select_genesets.addActionListener(e -> {
            if (c_analysisList.isEditable())
                modifyCurrentItem();

            new GeneSetSelector(analyses);
        });

        b_load.addActionListener(e -> {
            try {
                if(gsea_man.loadCSV())
                    updateComboList();
            } catch (FileNotFoundException e1) {
                new DisplayMessage("Error", "Selected file not valid");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        b_rename.addActionListener(e -> {
            c_analysisList.setEditable(true);
            c_analysisList.requestFocus();
        });

        b_delete.addActionListener(e -> {
            int selected_index = c_analysisList.getSelectedIndex();

            if (selected_index != c_analysisList.getItemCount() - 1) {
                DisplayMessage message = new DisplayMessage("prompt", "Are you sure you would like to delete: ".concat((String) c_analysisList.getItemAt(selected_index)));
                if (message.getResponse() == JOptionPane.OK_OPTION) {
                    pending_changes = true;

                    c_analysisList.removeItemAt(selected_index);
                    analyses.removeAnalysis(analyses.getCurrentAnalysisName());
                    c_analysisList.setSelectedIndex(0);

                    if (c_analysisList.getItemCount() != 1)
                        c_analysisList.setEditable(false);
                }
            } else
                new DisplayMessage("disclaimer", "Cannot delete this item");
        });

        b_close.addActionListener(e -> {
            if (pending_changes) {
                DisplayMessage prompt = new DisplayMessage("Prompt", "Would you like to save?");

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
                        try {
                            gsea_man.saveCSV(filename);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
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
            try {
                if(!gsea_man.performRequest(all_genes))
                    new DisplayMessage("Error", "Failed to send Request");
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }

        });
    }

    /**
     * Method: updateSelectedGeneSets
     * Description: Called from the Gene Set Selector window when the genes have been selected. This method updates the
     * gene list in the main window and adds the analysis member and its gene sets to the analyses object.
     * @param list
     */
    static void updateSelectedGeneSets(JList <String> list) {
        AnalysisMember member = analyses.getCurrentAnalysis();
        List<Pair<String, List<String>>> all_genesets = analyses.getAllGeneSets();
        ListModel<String> model = list.getModel();

        if(member.hasGeneSet())
            member.clearGeneSets();

        for (Pair<String, List<String>> gene_set : all_genesets) {
            for (int i = 0; i < model.getSize(); i++)
                if (model.getElementAt(i).equals(gene_set.getKey()))
                    member.addGeneSet(gene_set);
        }

        if (!analyses.doesExist(member))
            analyses.addAnalysis(member);

        ListModel element = list.getModel();
        listModel.clear();
        for (int i = 0; i < element.getSize(); i++) {
            listModel.add(element.getElementAt(i));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}