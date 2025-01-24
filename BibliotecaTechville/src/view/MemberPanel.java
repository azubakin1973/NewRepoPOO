package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import controller.MemberController;
import model.Member;

public class MemberPanel extends JPanel {
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private MemberController controller;
    
    public MemberPanel() {
        controller = new MemberController();
        setLayout(new BorderLayout());
        
        // Painel superior com busca
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Campo de busca
        searchField = new JTextField(20);
        searchField.setToolTipText("Rechercher par nom ou email");
        
        // Botões
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton clearButton = new JButton("Effacer la recherche");
        
        // Layout dos componentes
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Rechercher:"), gbc);
        
        gbc.gridx = 1;
        topPanel.add(searchField, gbc);
        
        gbc.gridx = 2;
        topPanel.add(clearButton, gbc);
        
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        topPanel.add(addButton, gbc);
        
        gbc.gridx = 1;
        topPanel.add(editButton, gbc);
        
        gbc.gridx = 2;
        topPanel.add(deleteButton, gbc);
        
        // Tabela com sorter
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Date d'inscription"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        sorter = new TableRowSorter<>(tableModel);
        memberTable = new JTable(tableModel);
        memberTable.setRowSorter(sorter);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);
        
        // Implementar busca
        implementSearch();
        
        // Carregar dados iniciais
        loadMembers();
        
        // Ações dos botões
        clearButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });
    }
    
    private void implementSearch() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
    }
    
    private void filterTable() {
        String text = searchField.getText().toLowerCase();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            RowFilter<DefaultTableModel, Object> rf = RowFilter.orFilter(Arrays.asList(
                RowFilter.regexFilter("(?i)" + text, 1), // Nome
                RowFilter.regexFilter("(?i)" + text, 2), // Sobrenome
                RowFilter.regexFilter("(?i)" + text, 3)  // Email
            ));
            sorter.setRowFilter(rf);
        }
    }
    
    private void loadMembers() {
        try {
            List<Member> members = controller.getAllMembers();
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            for (Member member : members) {
                Object[] row = {
                    member.getId(),
                    member.getNom(),
                    member.getPrenom(),
                    member.getEmail(),
                    sdf.format(member.getDateInscription())
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des membres: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}