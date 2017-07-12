package main;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Isaac on 7/11/2017.
 */
public class GeneSelector {
    private static SortedListModel list_selected_sets;
    private static SortedListModel list_all_sets;
    private static JList<String> selected_genesets;
    private static JList<String> all_genesets;
    private Analyses analyses;

    private static JFrame myFrame;
    private static JDialog myWindow;
    private static JPanel myPanel;
    private static JLabel l_analysis_name;
    private static JLabel l_enrichement_analysis_name;
    private static JLabel l_all_genes;
    private static JLabel l_selected_gene_sets;
    private static GridBagConstraints con_main;
    private static GridBagConstraints con_controls;
    private static JScrollPane sp_all_sets;
    private static JScrollPane sp_selected_sets;
    private static JPanel p_controls;
    private static JPanel p_submit_cancel;
    private static JButton b_move_right;
    private static JButton b_move_all_right;
    private static JButton b_move_all_left;
    private static JButton b_move_left;
    private static JButton b_submit;
    private static JButton b_cancel;

    GeneSelector(Analyses analyses) {
        this.analyses = analyses;
        myFrame = new JFrame();
        myWindow = new JDialog(myFrame, "Gene Set Selector", true);
        myWindow.setMinimumSize(new Dimension(700,400));
        myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());

        con_main = new GridBagConstraints();
        myPanel.setBorder(BorderFactory.createEmptyBorder());

        l_analysis_name = new JLabel("Analysis name:");
        con_main.insets = new Insets(15,15,15,0);
        myPanel.add(l_analysis_name, con_main);

        l_enrichement_analysis_name = new JLabel(analyses.getCurrentAnalysisName());
        con_main.gridx = 1;
        con_main.anchor = GridBagConstraints.LINE_START;
        myPanel.add(l_enrichement_analysis_name, con_main);

        l_all_genes = new JLabel("All Gene Sets:");
        con_main.gridx = 0;
        con_main.gridy = 1;
        con_main.insets = new Insets(0,15,15,15);
        con_main.fill = GridBagConstraints.NONE;
        con_main.anchor = GridBagConstraints.LAST_LINE_START;
        myPanel.add(l_all_genes, con_main);

        sp_all_sets = new JScrollPane();
        sp_all_sets.setPreferredSize(new Dimension(250,200));

        list_selected_sets = new SortedListModel();
        list_all_sets = new SortedListModel();

        list_all_sets.clear();              //are these needed since I'm newing an instance beforehand?
        list_selected_sets.clear();

        all_genesets = new JList<>(list_all_sets);
        sp_all_sets.setViewportView(all_genesets);
        con_main.gridx = 0;
        con_main.gridy = 2;
        con_main.weightx = 1;
        con_main.weighty = 1;
        con_main.fill = GridBagConstraints.BOTH;
        con_main.anchor = GridBagConstraints.LINE_END;
        con_main.gridheight = 6;
        con_main.gridwidth = 2;
        myPanel.add(sp_all_sets, con_main);

        sp_selected_sets = new JScrollPane();
        sp_selected_sets.setPreferredSize(new Dimension(250,200));

        selected_genesets = new JList<>(list_selected_sets);
        sp_selected_sets.setViewportView(selected_genesets);
        con_main.gridx = 3;
        con_main.gridy = 2;
        con_main.weightx = 1;
        con_main.weighty = 1;
        con_main.gridwidth = 2;
        con_main.gridheight = 6;
        con_main.fill = GridBagConstraints.BOTH;
        con_main.anchor = GridBagConstraints.LINE_END;
        myPanel.add(sp_selected_sets, con_main);

        p_controls = new JPanel();
        p_controls.setLayout(new GridBagLayout());

        b_move_right = new JButton("►");
        b_move_right.setPreferredSize(new Dimension(58,25));

        con_controls = new GridBagConstraints();
        con_controls.insets = new Insets(0,0,15,0);
        con_main.fill = GridBagConstraints.HORIZONTAL;
        p_controls.add(b_move_right, con_controls);

        b_move_all_right = new JButton("►►");
        con_controls.gridy = 1;
        con_controls.insets = new Insets(0,0,15,0);
        p_controls.add(b_move_all_right, con_controls);

        b_move_all_left = new JButton("◄◄");
        con_controls.gridy = 2;
        con_controls.anchor = GridBagConstraints.PAGE_START;
        p_controls.add(b_move_all_left, con_controls);

        b_move_left = new JButton("◄");
        b_move_left.setPreferredSize(new Dimension(58,25));
        con_controls.gridy = 3;
        p_controls.add(b_move_left, con_controls);

        con_main.gridx = 2;
        con_main.gridy = 2;
        con_main.gridheight = 4;
        con_main.gridwidth = 1;
        con_main.weightx = 0;
        con_main.weighty = 1;
        con_main.fill = GridBagConstraints.NONE;
        con_main.insets = new Insets(0,0,0,0);
        con_main.anchor = GridBagConstraints.CENTER;
        myPanel.add(p_controls, con_main);

        l_selected_gene_sets = new JLabel("Selected Gene Sets:");
        con_main.gridx = 4;
        con_main.gridy = 1;
        con_main.gridheight = 1;
        con_main.weighty = 0;
        con_main.insets = new Insets(0,15,15,15);
        con_main.anchor = GridBagConstraints.LAST_LINE_START;
        myPanel.add(l_selected_gene_sets, con_main);

        p_submit_cancel = new JPanel();
        p_submit_cancel.setLayout(new GridBagLayout());

        b_cancel = new JButton("Cancel");
        b_cancel.setPreferredSize(new Dimension(100,25));
        con_controls.gridy = 0;
        con_controls.insets = new Insets(0,0,0,15);

        p_submit_cancel.add(b_cancel, con_controls);

        b_submit = new JButton("Submit");
        b_submit.setPreferredSize(new Dimension(100,25));
        con_controls.gridx = 1;
        con_controls.insets = new Insets(0,0,0,0);
        p_submit_cancel.add(b_submit, con_controls);

        con_main.gridx = 3;
        con_main.gridy = 8;
        con_main.gridwidth = 2;
        con_main.insets = new Insets(0,0,15,15);
        con_main.anchor = GridBagConstraints.LAST_LINE_END;
        myPanel.add(p_submit_cancel, con_main);

        PopulatePanels();
        SetupListeners();

        myWindow.add(myPanel);
        myWindow.pack();
        myWindow.setVisible(true);
    }

    private void PopulatePanels() {
        boolean selected;

        for(Pair<String, java.util.List<String>> geneset : analyses.getAllGeneSets()) {
            selected = false;
            if(analyses.getCurrentAnalysisMember() != null) {
                for (Pair<String, java.util.List<String>> set : analyses.getCurrentAnalysisMember().getGene_sets()) {
                    if (geneset.getKey().equals(set.getKey()))
                        selected = true;
                }
                if (selected)
                    list_selected_sets.add(geneset.getKey());
                else
                    list_all_sets.add(geneset.getKey());
            }
            else
                list_all_sets.add(geneset.getKey());
        }
    }

    private void SetupListeners() {
        b_submit.addActionListener(e -> {
            SettingsWindow.UpdateSelectedGeneSets(selected_genesets);
            myWindow.dispose();
        });
        b_cancel.addActionListener(e -> myWindow.dispose());
        b_move_right.addActionListener(e -> {
            for(String selected: all_genesets.getSelectedValuesList()){
                list_selected_sets.add(selected);
                list_all_sets.removeElement(selected);
            }
            all_genesets.clearSelection();
        });
        b_move_all_right.addActionListener(e -> {
            ListModel element = all_genesets.getModel();

            for(int i = 0; all_genesets.getModel().getSize() > 0;){
                list_selected_sets.add(element.getElementAt(i));
                list_all_sets.removeElement(list_all_sets.getElementAt(i));
            }
        });
        b_move_all_left.addActionListener(e -> {
            ListModel element = selected_genesets.getModel();

            for(int i = 0; selected_genesets.getModel().getSize() > 0;){
                list_all_sets.add(element.getElementAt(i));
                list_selected_sets.removeElement(list_selected_sets.getElementAt(i));
            }
        });
        b_move_left.addActionListener(e -> {
            for(String selected: selected_genesets.getSelectedValuesList()) {
                list_all_sets.add(selected);
                list_selected_sets.removeElement(selected);
            }
            selected_genesets.clearSelection();
        });
    }
}
