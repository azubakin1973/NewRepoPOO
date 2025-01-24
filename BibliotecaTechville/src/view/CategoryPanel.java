package view;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import controller.CategoryController;
import model.Category;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

public class CategoryPanel extends JPanel {
    
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;
    private CategoryController controller;
    // Novos componentes para a árvore
    private JTree categoryTree;
    private DefaultTreeModel treeModel;
    
    public CategoryPanel() {
        controller = new CategoryController();
        setLayout(new BorderLayout());
        
        // Panel supérieur avec champ de recherche et boutons
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
        
        // Table des catégories
        String[] columns = {"ID", "Nom", "Catégorie Parent"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        categoryTable = new JTable(tableModel);
        
        // Inicialização da árvore
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Catégories");
        treeModel = new DefaultTreeModel(root);
        categoryTree = new JTree(treeModel);
        
        // Criação do SplitPane para dividir a árvore e a tabela
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(categoryTree),
            new JScrollPane(categoryTable));
        splitPane.setDividerLocation(200);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        // Listeners
        addButton.addActionListener(e -> showAddCategoryDialog());
        editButton.addActionListener(e -> showEditCategoryDialog());
        deleteButton.addActionListener(e -> deleteSelectedCategory());
        
        // Chargement initial des données
        loadCategories();
    }
    
    private void loadCategories() {
        try {
            List<Category> categories = controller.getAllCategories();
            // Atualizar tabela
            updateTable(categories);
            // Atualizar árvore
            updateCategoryTree(categories);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des catégories: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Category> categories) {
        tableModel.setRowCount(0);
        for (Category category : categories) {
            Object[] row = {
                category.getId(),
                category.getNom(),
                category.getIdCategoryParent() == null ? "Aucune" : category.getIdCategoryParent()
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateCategoryTree(List<Category> categories) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Catégories");
        Map<Integer, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        
        // Primeiro, adicionar categorias principais (sem pai)
        for (Category category : categories) {
            if (category.getIdCategoryParent() == null) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(category.getNom() + " (ID: " + category.getId() + ")");
                nodeMap.put(category.getId(), node);
                root.add(node);
            }
        }
        
        // Depois, adicionar subcategorias
        for (Category category : categories) {
            if (category.getIdCategoryParent() != null) {
                DefaultMutableTreeNode parentNode = nodeMap.get(category.getIdCategoryParent());
                if (parentNode != null) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(category.getNom() + " (ID: " + category.getId() + ")");
                    nodeMap.put(category.getId(), node);
                    parentNode.add(node);
                }
            }
        }
        
        treeModel.setRoot(root);
        treeModel.reload();
        
        // Expandir todos os nós
        for (int i = 0; i < categoryTree.getRowCount(); i++) {
            categoryTree.expandRow(i);
        }
    }
    
    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ajouter une Catégorie");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nomField = new JTextField();
        JTextField parentIdField = new JTextField();
        
        fieldsPanel.add(new JLabel("Nom:"));
        fieldsPanel.add(nomField);
        fieldsPanel.add(new JLabel("ID Catégorie Parent (optionnel):"));
        fieldsPanel.add(parentIdField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            try {
                Integer parentId = null;
                if (!parentIdField.getText().trim().isEmpty()) {
                    parentId = Integer.parseInt(parentIdField.getText().trim());
                }
                
                Category category = new Category(
                    nomField.getText().trim(),
                    parentId
                );
                
                controller.saveCategory(category);
                loadCategories();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                    "Catégorie ajoutée avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "L'ID de la catégorie parent doit être un nombre",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout de la catégorie: " + ex.getMessage(),
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
    
    private void showEditCategoryDialog() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une catégorie à modifier",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) categoryTable.getValueAt(selectedRow, 0);
            Category category = controller.findCategoryById(id);
            
            if (category == null) {
                JOptionPane.showMessageDialog(this,
                    "Catégorie non trouvée",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Modifier la Catégorie");
            dialog.setModal(true);
            dialog.setLayout(new BorderLayout());
            
            JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField nomField = new JTextField(category.getNom());
            JTextField parentIdField = new JTextField(
                category.getIdCategoryParent() == null ? "" : 
                category.getIdCategoryParent().toString()
            );
            
            fieldsPanel.add(new JLabel("Nom:"));
            fieldsPanel.add(nomField);
            fieldsPanel.add(new JLabel("ID Catégorie Parent (optionnel):"));
            fieldsPanel.add(parentIdField);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Enregistrer");
            JButton cancelButton = new JButton("Annuler");
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            saveButton.addActionListener(e -> {
                try {
                    Integer parentId = null;
                    if (!parentIdField.getText().trim().isEmpty()) {
                        parentId = Integer.parseInt(parentIdField.getText().trim());
                    }
                    
                    category.setNom(nomField.getText().trim());
                    category.setIdCategoryParent(parentId);
                    
                    controller.updateCategory(category);
                    loadCategories();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(this,
                        "Catégorie modifiée avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "L'ID de la catégorie parent doit être un nombre",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification de la catégorie: " + ex.getMessage(),
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
                "Erreur lors du chargement de la catégorie: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une catégorie à supprimer",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) categoryTable.getValueAt(selectedRow, 0);
        String nom = (String) categoryTable.getValueAt(selectedRow, 1);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer la catégorie '" + nom + "' ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                controller.deleteCategory(id);
                loadCategories();
                
                JOptionPane.showMessageDialog(this,
                    "Catégorie supprimée avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression de la catégorie: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}