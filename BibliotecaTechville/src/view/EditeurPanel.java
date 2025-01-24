package view;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import controller.EditeurController;
import model.Editeur;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class EditeurPanel extends JPanel {
    
    private JTable editeurTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;
    private EditeurController controller;
    // Árvore para editores (opcional, mas seguindo o padrão do CategoryPanel)
    private JTree editeurTree;
    private DefaultTreeModel treeModel;
    
    public EditeurPanel() {
        controller = new EditeurController();
        setLayout(new BorderLayout());
        
        // Painel superior com campo de pesquisa e botões
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        
        topPanel.add(new JLabel("Rechercher: "));
        topPanel.add(searchField);
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        
        // Adicionar listener para pesquisa
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        
        // Tabela de editores
        String[] columns = {"ID", "Nom", "Adresse"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        editeurTable = new JTable(tableModel);
        
        // Configurar seleção de linha única
        editeurTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Inicialização da árvore
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Éditeurs");
        treeModel = new DefaultTreeModel(root);
        editeurTree = new JTree(treeModel);
        
        // Criação do SplitPane para dividir a árvore e a tabela
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(editeurTree),
            new JScrollPane(editeurTable));
        splitPane.setDividerLocation(200);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        // Listeners
        addButton.addActionListener(e -> showAddEditeurDialog());
        editButton.addActionListener(e -> showEditEditeurDialog());
        deleteButton.addActionListener(e -> deleteSelectedEditeur());
        
        // Carregamento inicial dos dados
        loadEditeurs();
    }
    
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        try {
            List<Editeur> allEditeurs = controller.getAllEditeurs();
            List<Editeur> filteredEditeurs = allEditeurs.stream()
                .filter(editeur -> 
                    editeur.getNom().toLowerCase().contains(searchText) ||
                    editeur.getAdresse().toLowerCase().contains(searchText)
                )
                .collect(Collectors.toList());
            
            updateTable(filteredEditeurs);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du filtrage des éditeurs : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadEditeurs() {
        try {
            List<Editeur> editeurs = controller.getAllEditeurs();
            // Atualizar tabela
            updateTable(editeurs);
            // Atualizar árvore
            updateEditeurTree(editeurs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des éditeurs : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Editeur> editeurs) {
        tableModel.setRowCount(0);
        for (Editeur editeur : editeurs) {
            Object[] row = {
                editeur.getId(),
                editeur.getNom(),
                editeur.getAdresse()
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateEditeurTree(List<Editeur> editeurs) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Éditeurs");
        Map<Integer, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        
        // Adicionar editores à árvore
        for (Editeur editeur : editeurs) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                editeur.getNom() + " (ID: " + editeur.getId() + ")"
            );
            nodeMap.put(editeur.getId(), node);
            root.add(node);
        }
        
        treeModel.setRoot(root);
        treeModel.reload();
        
        // Expandir todos os nós
        for (int i = 0; i < editeurTree.getRowCount(); i++) {
            editeurTree.expandRow(i);
        }
    }
    
    private void showAddEditeurDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ajouter un Éditeur");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nomField = new JTextField();
        JTextField adresseField = new JTextField();
        
        fieldsPanel.add(new JLabel("Nom :"));
        fieldsPanel.add(nomField);
        fieldsPanel.add(new JLabel("Adresse :"));
        fieldsPanel.add(adresseField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            try {
                // Validação de campos obrigatórios
                if (nomField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Le nom de l'éditeur est obligatoire",
                        "Erreur de Validation",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Editeur editeur = new Editeur(
                    nomField.getText().trim(),
                    adresseField.getText().trim()
                );
                
                controller.saveEditeur(editeur);
                loadEditeurs();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                    "Éditeur ajouté avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout de l'éditeur : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(fieldsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showEditEditeurDialog() {
        int selectedRow = editeurTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Sélectionnez un éditeur à modifier",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) editeurTable.getValueAt(selectedRow, 0);
            Editeur editeur = controller.findEditeurById(id);
            
            if (editeur == null) {
                JOptionPane.showMessageDialog(this,
                    "Éditeur non trouvé",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Modifier l'Éditeur");
            dialog.setModal(true);
            dialog.setLayout(new BorderLayout());
            
            JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField nomField = new JTextField(editeur.getNom());
            JTextField adresseField = new JTextField(editeur.getAdresse());
            
            fieldsPanel.add(new JLabel("Nom :"));
            fieldsPanel.add(nomField);
            fieldsPanel.add(new JLabel("Adresse :"));
            fieldsPanel.add(adresseField);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Enregistrer");
            JButton cancelButton = new JButton("Annuler");
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            saveButton.addActionListener(e -> {
                try {
                    // Validação de campos obrigatórios
                    if (nomField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog,
                            "Le nom de l'éditeur est obligatoire",
                            "Erreur de Validation",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    editeur.setNom(nomField.getText().trim());
                    editeur.setAdresse(adresseField.getText().trim());
                    
                    controller.updateEditeur(editeur);
                    loadEditeurs();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(this,
                        "Éditeur modifié avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification de l'éditeur : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            dialog.add(fieldsPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement de l'éditeur : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedEditeur() {
        int selectedRow = editeurTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Sélectionnez un éditeur à supprimer",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) editeurTable.getValueAt(selectedRow, 0);
        String nom = (String) editeurTable.getValueAt(selectedRow, 1);
        
        int confirmacao = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer l'éditeur '" + nom + "' ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                controller.deleteEditeur(id);
                loadEditeurs();
                
                JOptionPane.showMessageDialog(this,
                    "Éditeur supprimé avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression de l'éditeur : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}