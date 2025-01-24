package view;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.ArrayList;
import pattern.composite.CategoryComponent;
import pattern.composite.Category;
import pattern.composite.BookItem;

public class CategoryTreePanel extends JPanel {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    
    public CategoryTreePanel() {
        setLayout(new BorderLayout());
        
        root = new DefaultMutableTreeNode("Bibliothèque");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(e -> handleSelection());
        
        add(new JScrollPane(tree), BorderLayout.CENTER);
        createButtonPanel();
    }
    
    private void handleSelection() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
            tree.getLastSelectedPathComponent();
            
        if (node == null) return;
        
        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof CategoryComponent) {
            // Tratamento da seleção
            ((CategoryComponent) nodeInfo).display();
        }
    }
    
    public DefaultMutableTreeNode getRootNode() {
        return root;
    }
    
    public void addCategory(CategoryComponent category, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(category);
        treeModel.insertNodeInto(node, parent, parent.getChildCount());
        
        for (CategoryComponent child : category.getChildren()) {
            addCategory(child, node);
        }
        
        tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    private void addNewCategory() {
        String name = JOptionPane.showInputDialog(this, 
            "Nom de la nouvelle catégorie:", 
            "Nouvelle Catégorie", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (name != null && !name.trim().isEmpty()) {
            Category category = new Category(name.trim());
            addCategory(category, root);
        }
    }
    
    private void addNewSubCategory() {
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) 
            tree.getLastSelectedPathComponent();
            
        if (parentNode == null || !(parentNode.getUserObject() instanceof Category)) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une catégorie parent",
                "Erreur",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String name = JOptionPane.showInputDialog(this,
            "Nom de la sous-catégorie:",
            "Nouvelle Sous-catégorie",
            JOptionPane.QUESTION_MESSAGE);
            
        if (name != null && !name.trim().isEmpty()) {
            Category subCategory = new Category(name.trim());
            addCategory(subCategory, parentNode);
        }
    }
    
    private void deleteSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();
            
        if (node != null && node.getParent() != null) {
            treeModel.removeNodeFromParent(node);
        }
    }
    
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addCategoryBtn = new JButton("Nouvelle Catégorie");
        JButton addSubCategoryBtn = new JButton("Nouvelle Sous-catégorie");
        JButton deleteBtn = new JButton("Supprimer");
        
        addCategoryBtn.addActionListener(e -> addNewCategory());
        addSubCategoryBtn.addActionListener(e -> addNewSubCategory());
        deleteBtn.addActionListener(e -> deleteSelected());
        
        buttonPanel.add(addCategoryBtn);
        buttonPanel.add(addSubCategoryBtn);
        buttonPanel.add(deleteBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
}