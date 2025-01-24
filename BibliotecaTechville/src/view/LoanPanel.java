package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import controller.LoanController;
import controller.BookController;
import controller.MemberController;
import model.Loan;
import pattern.command.BorrowCommand;
import command.ReturnCommand;
import pattern.command.CommandHistory;
import pattern.command.Command;
import java.util.List;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.SQLException;

public class LoanPanel extends JPanel {
    
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, returnButton, undoButton, redoButton;
    private LoanController controller;
    private BookController bookController;
    private MemberController memberController;
    private CommandHistory commandHistory;
    private JDatePicker startDate, endDate;
    
    public LoanPanel(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
        controller = new LoanController();
        bookController = new BookController();
        memberController = new MemberController();
        setLayout(new BorderLayout());
        
        // Painel de filtros
        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        searchField = new JTextField(20);
        startDate = new JDatePicker();
        endDate = new JDatePicker();
        
        // Adiciona componentes com GridBagLayout
        // ...
        
        // Painel superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Nouveau Prêt");
        returnButton = new JButton("Retour");
        undoButton = new JButton("Annuler");
        redoButton = new JButton("Refaire");
        
        topPanel.add(new JLabel("Rechercher: "));
        topPanel.add(searchField);
        topPanel.add(addButton);
        topPanel.add(returnButton);
        topPanel.add(undoButton);
        topPanel.add(redoButton);
        
        // Configuração inicial dos botões undo/redo
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        
        // Tabela de empréstimos
        String[] columns = {"ID", "Livre", "Membre", "Date Emprunt", "Date Retour Prévue", "Statut"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        loanTable = new JTable(model);
        
        add(filterPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(loanTable), BorderLayout.CENTER);
        
        // Adiciona os listeners
        addButton.addActionListener(e -> showAddLoanDialog());
        returnButton.addActionListener(e -> showReturnBookDialog());
        
        undoButton.addActionListener(e -> {
            try {
                commandHistory.undo();
                loadLoans();
                updateUndoRedoButtons();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'annulation: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        redoButton.addActionListener(e -> {
            try {
                commandHistory.redo();
                loadLoans();
                updateUndoRedoButtons();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la répétition: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Carrega os dados iniciais
        loadLoans();
    }
    
    private void updateUndoRedoButtons() {
        undoButton.setEnabled(commandHistory.canUndo());
        redoButton.setEnabled(commandHistory.canRedo());
    }
    
    private void executeLoanCommand(Command command) {
        try {
            commandHistory.executeCommand(command);
            loadLoans();
            updateUndoRedoButtons();
        } catch (SQLException ex) {
            if (ex.getMessage().equals("Le livre est déjà emprunté!")) {
                JOptionPane.showMessageDialog(this,
                    "Ce livre est déjà emprunté!",
                    "Livre non disponible",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'exécution: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'exécution: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    void loadLoans() {
        try {
            List<Loan> loans = controller.getAllLoans();
            tableModel.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Loan loan : loans) {
                String status = loan.getActualReturnDate() == null ? "En cours" : "Retourné";
                Object[] row = {
                    loan.getId(),
                    loan.getBookId(),
                    loan.getMemberId(),
                    dateFormat.format(loan.getLoanDate()),
                    dateFormat.format(loan.getExpectedReturnDate()),
                    loan.getActualReturnDate() == null ? "-" : dateFormat.format(loan.getActualReturnDate()),
                    status
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des prêts: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddLoanDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nouveau Prêt");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField bookIdField = new JTextField();
        JTextField memberIdField = new JTextField();
        JTextField returnDateField = new JTextField();
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        returnDateField.setText(dateFormat.format(cal.getTime()));
        
        fieldsPanel.add(new JLabel("ID du Livre:"));
        fieldsPanel.add(bookIdField);
        fieldsPanel.add(new JLabel("ID du Membre:"));
        fieldsPanel.add(memberIdField);
        fieldsPanel.add(new JLabel("Date de Retour Prévue (AAAA-MM-JJ):"));
        fieldsPanel.add(returnDateField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(fieldsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        saveButton.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(bookIdField.getText());
                int memberId = Integer.parseInt(memberIdField.getText());
                Date returnDate = dateFormat.parse(returnDateField.getText());
                
                Loan loan = new Loan(
                    bookId,
                    memberId,
                    new Date(), // Data atual
                    returnDate
                );
                
                try {
                    controller.saveLoan(loan);
                    loadLoans();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(this,
                        "Prêt enregistré avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    if (ex.getMessage().equals("Le livre est déjà emprunté!")) {
                        JOptionPane.showMessageDialog(this,
                            "Ce livre est déjà emprunté!",
                            "Livre non disponible",
                            JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Erreur lors de l'enregistrement du prêt: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez le format AAAA-MM-JJ",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Les IDs doivent être des nombres valides",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showReturnBookDialog() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un prêt à retourner",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        if ("Retourné".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "Ce livre a déjà été retourné",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int loanId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Confirmez-vous le retour de ce livre?",
            "Confirmation de retour",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                controller.returnBook(loanId, new Date());
                loadLoans();
                
                JOptionPane.showMessageDialog(this,
                    "Retour enregistré avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement du retour: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleBorrow() {
        Book selectedBook = getSelectedBook();
        Member selectedMember = getSelectedMember();
        
        if (selectedBook != null && selectedMember != null) {
            // PADRÃO COMMAND: Cria e executa o comando
            Command borrowCommand = new BorrowCommand(selectedBook, selectedMember);
            commandHistory.executeCommand(borrowCommand);
            refreshView();
        }
    }
    
    private void handleReturn() {
        Book selectedBook = getSelectedBook();
        Member selectedMember = getSelectedMember();
        
        if (selectedBook != null && selectedMember != null) {
            // PADRÃO COMMAND: Cria e executa o comando
            Command returnCommand = new ReturnCommand(selectedBook, selectedMember);
            commandHistory.executeCommand(returnCommand);
            refreshView();
        }
    }
    
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        
        JButton borrowButton = new JButton("Emprunter");
        JButton returnButton = new JButton("Retourner");
        JButton undoButton = new JButton("Annuler");
        JButton redoButton = new JButton("Refaire");
        
        borrowButton.addActionListener(e -> handleBorrow());
        returnButton.addActionListener(e -> handleReturn());
        
        // PADRÃO COMMAND: Botões para desfazer/refazer
        undoButton.addActionListener(e -> {
            commandHistory.undo();
            refreshView();
        });
        
        redoButton.addActionListener(e -> {
            commandHistory.redo();
            refreshView();
        });
        
        buttonPanel.add(borrowButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(redoButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
}