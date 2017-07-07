package main;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.JList;

/**
 * Created by Isaac on 6/29/2017.
 */
public class SettingsWindow extends JPanel implements ActionListener {

    private static JFrame myFrame = new JFrame();
    SortedListModel listModel = new SortedListModel();
    SortedListModel list_selected_sets = new SortedListModel();
    SortedListModel list_all_sets = new SortedListModel();
    private String current_analysis_name = null;
    private static int current_analysis_index;
    private static final int DELETE = 0;

    public SettingsWindow() {
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Displays all contents within the frame
        JDialog myWindow = new JDialog(myFrame, "Gene Set Enrichment Analysis", true);
        myWindow.setMinimumSize(new Dimension(550,375));
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        myPanel.setBorder(BorderFactory.createEmptyBorder());

        JLabel l_analysis_name = new JLabel("Analysis name:");
        constraints.insets = new Insets(20,15,15,15);
        constraints.anchor = GridBagConstraints.LINE_END;
        myPanel.add(l_analysis_name, constraints);

        JComboBox c_analysisList = new JComboBox();
        c_analysisList.addItem("Add Item...");

        c_analysisList.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
           @Override
           public void keyReleased(KeyEvent event) {
               if(event.getKeyChar() == KeyEvent.VK_ENTER) {
                    String analysis_field = (String)c_analysisList.getEditor().getItem();

                    if(analysis_field != null && !analysis_field.isEmpty()) {
                        current_analysis_name = (String) c_analysisList.getEditor().getItem();
                        System.out.println("item count: ".concat(Integer.toString(c_analysisList.getItemCount())));
                        System.out.println("Selected Index: ".concat(Integer.toString(current_analysis_index)));

                        //if item selected is "add item..." add item
                        if (current_analysis_index == c_analysisList.getItemCount() - 1) {
                            c_analysisList.insertItemAt(current_analysis_name, 0);
                            System.out.println("\"add item...\" selected");
                        //else edit currently selected item
                        }else {
                            System.out.println("current index: ".concat(Integer.toString(current_analysis_index)));
                            c_analysisList.removeItemAt(current_analysis_index);
                            c_analysisList.insertItemAt(current_analysis_name, current_analysis_index);
                            System.out.println("update current analysis");
                        }
                    }
                   c_analysisList.setEditable(false);
               } else if (event.getKeyChar() == KeyEvent.VK_ESCAPE)
                   c_analysisList.setEditable(false);
           }
       });

        c_analysisList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        c_analysisList.getEditor().selectAll();
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent e) {}
        });

        c_analysisList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    c_analysisList.setEditable(true);
                    c_analysisList.getEditor().selectAll();
                }
            }
        });

        c_analysisList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if "Add item..." is selected
                if(c_analysisList.getSelectedIndex() == c_analysisList.getItemCount() - 1) {
                    c_analysisList.setEditable(true);
                    c_analysisList.grabFocus();
                }
                if(c_analysisList.getSelectedIndex() != -1)
                    current_analysis_index = c_analysisList.getSelectedIndex();

                System.out.println("current index:: ".concat(Integer.toString(current_analysis_index)));
                //why does this prevent adding an item?
//                else
//                    c_analysisList.setEditable(false);

            }
        });
        c_analysisList.setEditable(true);
        c_analysisList.addActionListener(this);
        constraints.gridx = 1;
        constraints.insets = new Insets(15,0,0,15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridwidth = 4;

        myPanel.add(c_analysisList, constraints);

        JButton b_rename = new JButton("Rename");
        b_rename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c_analysisList.setEditable(true);
                c_analysisList.requestFocus();
            }
        });
        b_rename.setPreferredSize(new Dimension(100,25));
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.insets = new Insets(5,0,15,15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridwidth = 1;
                myPanel.add(b_rename, constraints);

        JButton b_delete = new JButton("Delete");
        b_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(c_analysisList.getSelectedIndex() != c_analysisList.getItemCount() - 1) {
                    Display_Message message = new Display_Message("prompt", "Are you sure you would like to delete: ".concat((String)c_analysisList.getItemAt(current_analysis_index)));
                    if (message.getResponse() == DELETE) {
                        c_analysisList.removeItemAt(current_analysis_index);
                        c_analysisList.setSelectedIndex(0);
                    }
                }
                else
                    new Display_Message("disclaimer", "Cannot delete this item");
            }
        });
        b_delete.setPreferredSize(new Dimension(100,25));
        constraints.gridx = 3;
        myPanel.add(b_delete, constraints);

        JLabel label_selected_gensets = new JLabel("Selected Gene Sets:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(15,15,0,15);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LINE_START;
        myPanel.add(label_selected_gensets, constraints);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(250,200));

        JList<String> selected_genes = new JList<>(listModel);
        scrollPane.setViewportView(selected_genes);
        selected_genes.setSelectionModel(new NoSelectionModel());
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0,15,15,15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridwidth = 2;
        myPanel.add(scrollPane, constraints);

        JButton b_select_genesets = new JButton("Select Gene Sets");
        b_select_genesets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
//                UpdateLists();

            String analysis_name = (String)c_analysisList.getSelectedItem();

                if (analysis_name != null && !analysis_name.isEmpty()) {
                    boolean update = true;
                    current_analysis_name = analysis_name;

                    ComboBoxModel<String> list = c_analysisList.getModel();
                    for (int i = 0; update && i < list.getSize(); i++)
                        if (list.getElementAt(i).equals(current_analysis_name))
                            update = false;

                    // add item to list if it doesn't already exist
                    if (update || list.getSize() == 0)
                        c_analysisList.addItem(current_analysis_name);

                    c_analysisList.setEditable(false);
                    GeneSelector();
                } else
                    new Display_Message("disclaimer", "Select/Create an analysis name first");
            }
        });
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

        JButton b_cancel = new JButton("Cancel");
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.ipadx = 0;
        constraints.insets = new Insets(0,0,15,15);
        constraints.anchor = GridBagConstraints.LAST_LINE_END;
        constraints.gridwidth = 1;
        b_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){myFrame.dispose();}
        });
        myPanel.add(b_cancel, constraints);

        JButton b_submit = new JButton("Submit");
        b_submit.setPreferredSize(new Dimension(100,25));
        constraints.gridx = 3;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0,0,15,15);
        myPanel.add(b_submit, constraints);


        myWindow.add(myPanel);
        myWindow.pack();

        //Set focus on input field on first run
        c_analysisList.requestFocus();

        myWindow.setVisible(true);
    }

    private boolean GeneSelector() {
        JDialog myWindow = new JDialog(myFrame, "Gene Set Selector", true);
        myWindow.setMinimumSize(new Dimension(700,400));
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        myPanel.setBorder(BorderFactory.createEmptyBorder());

        JLabel l_analysis_name = new JLabel("Analysis name:");
        constraints.insets = new Insets(15,15,15,0);
        myPanel.add(l_analysis_name, constraints);

        JLabel l_enrichement_analysis_name = new JLabel(current_analysis_name);
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        myPanel.add(l_enrichement_analysis_name, constraints);

        JLabel l_all_genes = new JLabel("All Gene Sets:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(0,15,15,15);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        myPanel.add(l_all_genes, constraints);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(250,200));

        list_all_sets.add("GeneSet_A");
        list_all_sets.add("GeneSet_B");
        list_all_sets.add("GeneSet_C");
        list_all_sets.add("GeneSet_D");
        list_all_sets.add("GeneSet_E");
        list_all_sets.add("GeneSet_F");
        list_all_sets.add("GeneSet_G");
        list_all_sets.add("GeneSet_H");
        list_all_sets.add("GeneSet_I");
        list_all_sets.add("GeneSet_J");
        list_all_sets.add("GeneSet_K");
        list_all_sets.add("GeneSet_L");
        list_all_sets.add("GeneSet_M");
        list_all_sets.add("GeneSet_N");
        list_all_sets.add("GeneSet_O");
        list_all_sets.add("GeneSet_P");
        list_all_sets.add("GeneSet_Q");

        JList<String> select_genesets = new JList<>(list_all_sets);
        scrollPane.setViewportView(select_genesets);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridheight = 6;
        constraints.gridwidth = 2;
        myPanel.add(scrollPane, constraints);

        JScrollPane scroll_selected_sets = new JScrollPane();
        scroll_selected_sets.setPreferredSize(new Dimension(250,200));

        JList<String> selected_genesets = new JList<>(list_selected_sets);
        scroll_selected_sets.setViewportView(selected_genesets);
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 2;
        constraints.gridheight = 6;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.LINE_END;
        myPanel.add(scroll_selected_sets, constraints);

        JPanel p_controls = new JPanel();
        p_controls.setLayout(new GridBagLayout());
        JButton b_move_right = new JButton("►");
        b_move_right.setPreferredSize(new Dimension(58,25));
        b_move_right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(String selected:select_genesets.getSelectedValuesList()){
                    list_selected_sets.add(selected);
                    list_all_sets.removeElement(selected);
                }
                select_genesets.clearSelection();
            }
        });

        GridBagConstraints controls_constraints = new GridBagConstraints();

        controls_constraints.gridx = 0;
        controls_constraints.gridy = 0;
        controls_constraints.insets = new Insets(0,0,15,0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        p_controls.add(b_move_right, controls_constraints);

        JButton b_move_all_right = new JButton("►►");
        b_move_all_right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListModel element = select_genesets.getModel();

                for(int i = 0; select_genesets.getModel().getSize() > 0;){
                    list_selected_sets.add(element.getElementAt(i));
                    list_all_sets.removeElement(list_all_sets.getElementAt(i));
                }
            }
        });
        controls_constraints.gridy = 1;
        controls_constraints.insets = new Insets(0,0,15,0);
        p_controls.add(b_move_all_right, controls_constraints);

        JButton b_move_all_left = new JButton("◄◄");
        controls_constraints.gridy = 2;
        controls_constraints.anchor = GridBagConstraints.PAGE_START;

        b_move_all_left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListModel element = selected_genesets.getModel();

                for(int i = 0; selected_genesets.getModel().getSize() > 0;){
                    list_all_sets.add(element.getElementAt(i));
                    list_selected_sets.removeElement(list_selected_sets.getElementAt(i));
                }
            }
        });

        p_controls.add(b_move_all_left, controls_constraints);

        JButton b_move_left = new JButton("◄");
        b_move_left.setPreferredSize(new Dimension(58,25));

        b_move_left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for(String selected: selected_genesets.getSelectedValuesList()) {
                    System.out.println(selected);
                    list_all_sets.add(selected);
                    list_selected_sets.removeElement(selected);
                }
                selected_genesets.clearSelection();
            }
        });

        controls_constraints.gridy = 3;
        p_controls.add(b_move_left, controls_constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridheight = 4;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0,0,0,0);
        constraints.anchor = GridBagConstraints.CENTER;
        myPanel.add(p_controls, constraints);

        JLabel l_selected_gene_sets = new JLabel("Selected Gene Sets:");
        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.weighty = 0;
        constraints.insets = new Insets(0,15,15,15);
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        myPanel.add(l_selected_gene_sets, constraints);

        JPanel p_submit_cancel = new JPanel();
        p_submit_cancel.setLayout(new GridBagLayout());


        JButton b_cancel = new JButton("Cancel");
        b_cancel.setPreferredSize(new Dimension(100,25));
        controls_constraints.gridy = 0;
        controls_constraints.insets = new Insets(0,0,0,15);
        b_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){ myWindow.dispose(); }
        });
        p_submit_cancel.add(b_cancel, controls_constraints);

        JButton b_submit = new JButton("Submit");
        b_submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateSelectedGeneSets(selected_genesets);
                myWindow.dispose();
            }
        });
        b_submit.setPreferredSize(new Dimension(100,25));
        controls_constraints.gridx = 1;
        controls_constraints.insets = new Insets(0,0,0,0);
        p_submit_cancel.add(b_submit, controls_constraints);

        constraints.gridx = 3;
        constraints.gridy = 8;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0,0,15,15);
        constraints.anchor = GridBagConstraints.LAST_LINE_END;
        myPanel.add(p_submit_cancel, constraints);

        myWindow.add(myPanel);
        myWindow.pack();
        myWindow.setVisible(true);
        return true;
    }

    private void UpdateSelectedGeneSets(JList<String> list) {
        ListModel element = list.getModel();
        listModel.clear();
        for(int i = 0; i < element.getSize(); i++){
            listModel.add(element.getElementAt(i));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
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