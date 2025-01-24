package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import controller.AuthorController;
import model.Author;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class AuthorPanel extends JPanel {
    
    private JTable authorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;
    private AuthorController controller;
    
    public AuthorPanel() {
        controller = new AuthorController();
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
        
        // Table des auteurs
        String[] columns = {"ID", "Nom", "Prénom", "Date de Naissance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        authorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(authorTable);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Listeners
        addButton.addActionListener(e -> showAddAuthorDialog());
        editButton.addActionListener(e -> showEditAuthorDialog());
        deleteButton.addActionListener(e -> deleteSelectedAuthor());
        
        // Chargement initial des données
        loadAuthors();
    }
    
    private void loadAuthors() {
        try {
            List<Author> authors = controller.getAllAuthors();
            tableModel.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Author author : authors) {
                Object[] row = {
                    author.getId(),
                    author.getNom(),
                    author.getPrenom(),
                    dateFormat.format(author.getDateNaissance())
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des auteurs: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddAuthorDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ajouter un Auteur");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField dateField = new JTextField();
        dateField.setToolTipText("Format: AAAA-MM-JJ");
        
        fieldsPanel.add(new JLabel("Nom:"));
        fieldsPanel.add(nomField);
        fieldsPanel.add(new JLabel("Prénom:"));
        fieldsPanel.add(prenomField);
        fieldsPanel.add(new JLabel("Date de Naissance (AAAA-MM-JJ):"));
        fieldsPanel.add(dateField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateNaissance = dateFormat.parse(dateField.getText());
                
                Author author = new Author(
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    dateNaissance
                );
                
                controller.saveAuthor(author);
                loadAuthors();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                    "Auteur ajouté avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez le format AAAA-MM-JJ",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout de l'auteur: " + ex.getMessage(),
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
    
    private void showEditAuthorDialog() {
        int selectedRow = authorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un auteur à modifier",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) authorTable.getValueAt(selectedRow, 0);
            Author author = controller.findAuthorById(id);
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Modifier l'Auteur");
            dialog.setModal(true);
            dialog.setLayout(new BorderLayout());
            
            JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            JTextField nomField = new JTextField(author.getNom());
            JTextField prenomField = new JTextField(author.getPrenom());
            JTextField dateField = new JTextField(dateFormat.format(author.getDateNaissance()));
            
            fieldsPanel.add(new JLabel("Nom:"));
            fieldsPanel.add(nomField);
            fieldsPanel.add(new JLabel("Prénom:"));
            fieldsPanel.add(prenomField);
            fieldsPanel.add(new JLabel("Date de Naissance (AAAA-MM-JJ):"));
            fieldsPanel.add(dateField);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Enregistrer");
            JButton cancelButton = new JButton("Annuler");
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            saveButton.addActionListener(e -> {
                try {
                    author.setNom(nomField.getText().trim());
                    author.setPrenom(prenomField.getText().trim());
                    author.setDateNaissance(dateFormat.parse(dateField.getText()));
                    
                    controller.updateAuthor(author);
                    loadAuthors();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(this,
                        "Auteur modifié avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Format de date invalide. Utilisez le format AAAA-MM-JJ",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification de l'auteur: " + ex.getMessage(),
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
                "Erreur lors du chargement de l'auteur: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedAuthor() {
        int selectedRow = authorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un auteur à supprimer",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) authorTable.getValueAt(selectedRow, 0);
        String nom = (String) authorTable.getValueAt(selectedRow, 1);
        String prenom = (String) authorTable.getValueAt(selectedRow, 2);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer l'auteur '" + prenom + " " + nom + "' ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                controller.deleteAuthor(id);
                loadAuthors();
                
                JOptionPane.showMessageDialog(this,
                    "Auteur supprimé avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression de l'auteur: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}