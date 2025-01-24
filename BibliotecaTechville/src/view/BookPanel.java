package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import controller.BookController;
import model.Book;
import model.PhysicalBook;
import model.RareBook;
import pattern.command.CommandHistory;

public class BookPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private BookController controller;
    private CommandHistory commandHistory;

    public BookPanel(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Painel superior com busca e filtros
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campo de busca
        searchField = new JTextField(20);
        
        // ComboBox para tipo de livro
        typeCombo = new JComboBox<>(new String[]{"Tous les types", "Physique", "Rare"});
        
        // Botões
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        
        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Rechercher:"), gbc);
        
        gbc.gridx = 1;
        topPanel.add(searchField, gbc);
        
        gbc.gridx = 2;
        topPanel.add(new JLabel("Type:"), gbc);
        
        gbc.gridx = 3;
        topPanel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(addButton, gbc);
        
        gbc.gridx = 1;
        topPanel.add(editButton, gbc);
        
        gbc.gridx = 2;
        topPanel.add(deleteButton, gbc);
        
        // Tabela
        String[] columns = {"ID", "Titre", "Type", "ISBN", "Année", "Disponible"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        
        // Listeners
        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> showEditBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        
        // Implementar busca e filtros
        implementSearch();
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame)null, "Ajouter un Livre", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField titleField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField publisherField = new JTextField();
        JTextField categoryField = new JTextField();
        JComboBox<String> typeField = new JComboBox<>(new String[]{"Physique", "Rare"});
        
        fieldsPanel.add(new JLabel("Titre:"));
        fieldsPanel.add(titleField);
        fieldsPanel.add(new JLabel("ISBN:"));
        fieldsPanel.add(isbnField);
        fieldsPanel.add(new JLabel("Année:"));
        fieldsPanel.add(yearField);
        fieldsPanel.add(new JLabel("ID Éditeur:"));
        fieldsPanel.add(publisherField);
        fieldsPanel.add(new JLabel("ID Catégorie:"));
        fieldsPanel.add(categoryField);
        fieldsPanel.add(new JLabel("Type:"));
        fieldsPanel.add(typeField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.addActionListener(e -> {
            try {
                Book book;
                if (typeField.getSelectedItem().equals("Rare")) {
                    book = new RareBook(
                        titleField.getText(),
                        Integer.parseInt(yearField.getText()),
                        isbnField.getText(),
                        Integer.parseInt(publisherField.getText()),
                        Integer.parseInt(categoryField.getText())
                    );
                } else {
                    book = new PhysicalBook(
                        titleField.getText(),
                        Integer.parseInt(yearField.getText()),
                        isbnField.getText(),
                        Integer.parseInt(publisherField.getText()),
                        Integer.parseInt(categoryField.getText())
                    );
                }
                
                controller.saveBook(book);
                loadBooks();
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erreur: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(fieldsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un livre à modifier",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) bookTable.getValueAt(selectedRow, 0);
            Book book = controller.findBookById(id);
            
            JDialog dialog = new JDialog((Frame)null, "Modifier le Livre", true);
            dialog.setLayout(new BorderLayout());
            
            JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField titleField = new JTextField(book.getTitle());
            JTextField isbnField = new JTextField(book.getIsbn());
            JTextField yearField = new JTextField(String.valueOf(book.getPublicationYear()));
            JTextField publisherField = new JTextField(String.valueOf(book.getPublisherId()));
            JTextField categoryField = new JTextField(String.valueOf(book.getCategoryId()));
            JComboBox<String> typeField = new JComboBox<>(new String[]{"Physique", "Rare"});
            typeField.setSelectedItem(book instanceof RareBook ? "Rare" : "Physique");
            
            fieldsPanel.add(new JLabel("Titre:"));
            fieldsPanel.add(titleField);
            fieldsPanel.add(new JLabel("ISBN:"));
            fieldsPanel.add(isbnField);
            fieldsPanel.add(new JLabel("Année:"));
            fieldsPanel.add(yearField);
            fieldsPanel.add(new JLabel("ID Éditeur:"));
            fieldsPanel.add(publisherField);
            fieldsPanel.add(new JLabel("ID Catégorie:"));
            fieldsPanel.add(categoryField);
            fieldsPanel.add(new JLabel("Type:"));
            fieldsPanel.add(typeField);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Enregistrer");
            JButton cancelButton = new JButton("Annuler");
            
            saveButton.addActionListener(e -> {
                try {
                    Book updatedBook;
                    if (typeField.getSelectedItem().equals("Rare")) {
                        updatedBook = new RareBook(
                            titleField.getText(),
                            Integer.parseInt(yearField.getText()),
                            isbnField.getText(),
                            Integer.parseInt(publisherField.getText()),
                            Integer.parseInt(categoryField.getText())
                        );
                    } else {
                        updatedBook = new PhysicalBook(
                            titleField.getText(),
                            Integer.parseInt(yearField.getText()),
                            isbnField.getText(),
                            Integer.parseInt(publisherField.getText()),
                            Integer.parseInt(categoryField.getText())
                        );
                    }
                    updatedBook.setId(book.getId());
                    updatedBook.setAvailable(book.isAvailable());
                    
                    controller.updateBook(updatedBook);
                    loadBooks();
                    dialog.dispose();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Erreur: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            dialog.add(fieldsPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement du livre: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un livre à supprimer",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) bookTable.getValueAt(selectedRow, 0);
        String title = (String) bookTable.getValueAt(selectedRow, 1);
        
        int result = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le livre '" + title + "'?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                controller.deleteBook(id);
                loadBooks();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadBooks() {
        try {
            List<Book> books = controller.getAllBooks();
            tableModel.setRowCount(0);
            for (Book book : books) {
                Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book instanceof RareBook ? "Rare" : "Physique",
                    book.getIsbn(),
                    book.getPublicationYear(),
                    book.isAvailable() ? "Oui" : "Non"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des livres: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void implementSearch() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        
        typeCombo.addActionListener(e -> filterTable());
    }

    private void filterTable() {
        String text = searchField.getText().toLowerCase();
        String type = (String) typeCombo.getSelectedItem();
        
        RowFilter<DefaultTableModel, Object> rf = RowFilter.andFilter(Arrays.asList(
            RowFilter.regexFilter("(?i)" + text, 1), // Filtro por título
            type.equals("Tous les types") ? null : 
                RowFilter.regexFilter(type.equals("Rare") ? "Rare" : "Physique", 2)
        ));
        
        sorter.setRowFilter(rf);
    }

    public void refreshData() {
        loadBooks();
    }
}