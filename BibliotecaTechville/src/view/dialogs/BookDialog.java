
package view.dialogs;

import javax.swing.JTextField;

public class BookDialog extends JDialog {
    private JTextField titleField;
    private JTextField isbnField;
    private JSpinner yearSpinner;
    private JComboBox<Category> categoryCombo;
    private Book book;
    
    public BookDialog(Frame owner, Book book) {
        super(owner, book == null ? "Nouveau Livre" : "Modifier Livre", true);
        this.book = book;
        
        createUI();
        if (book != null) {
            loadBookData();
        }
    }
    
    private void createUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campos do formulário
        addFormField(panel, gbc, "Titre:", titleField = new JTextField(30));
        addFormField(panel, gbc, "ISBN:", isbnField = new JTextField(15));
        addFormField(panel, gbc, "Année:", yearSpinner = new JSpinner(
            new SpinnerNumberModel(2024, 1800, 2024, 1)));
        addFormField(panel, gbc, "Catégorie:", categoryCombo = new JComboBox<>());
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Layout
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Listeners
        saveButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> dispose());
        
        pack();
        setLocationRelativeTo(getOwner());
    }
} 