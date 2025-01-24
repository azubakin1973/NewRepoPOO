
package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import model.Employe;
import model.StatistiquesModel;
import dao.EmployeDAO;
import observer.EmployeObserver;
import java.util.List;

public class EmployeFrame extends JFrame implements EmployeObserver {
    private JPanel mainPanel;
    private JTextField txtNom, txtPoste, txtSalaire, txtRecherche;
    private JComboBox<String> comboTypeRecherche;
    private JTable tableEmployes;
    private DefaultTableModel tableModel;
    private JButton btnAjouter, btnModifier, btnSupprimer;
    private EmployeDAO employeDAO;
    private Employe employeSelectionne;
    private StatistiquesPanel statistiquesPanel;
    
    public EmployeFrame() {
        employeDAO = new EmployeDAO();
        employeDAO.addObserver(this);
        
        setTitle("Gestion des Employés");
        setLayout(new BorderLayout(10, 10));
        initComponents();
        chargerEmployes();
        
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    private void initComponents() {
        // Painel Principal com Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Painel de Gestão
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Painel do Formulário
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations de l'employé"));
        
        formPanel.add(new JLabel("Nom:"));
        txtNom = new JTextField();
        formPanel.add(txtNom);
        
        formPanel.add(new JLabel("Poste:"));
        txtPoste = new JTextField();
        formPanel.add(txtPoste);
        
        formPanel.add(new JLabel("Salaire:"));
        txtSalaire = new JTextField();
        formPanel.add(txtSalaire);

        // Painel de Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);

        // Painel de Pesquisa
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche"));

        comboTypeRecherche = new JComboBox<>(new String[]{"Nom", "Poste"});
        txtRecherche = new JTextField(20);
        JButton btnRechercher = new JButton("Rechercher");

        searchPanel.add(new JLabel("Type:"));
        searchPanel.add(comboTypeRecherche);
        searchPanel.add(new JLabel("Terme:"));
        searchPanel.add(txtRecherche);
        searchPanel.add(btnRechercher);

        // Tabela
        String[] columns = {"ID", "Nom", "Poste", "Salaire"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableEmployes = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableEmployes);
        
        // Organizando os painéis de gestão
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Adiciona a aba de gestão
        tabbedPane.addTab("Gestion", mainPanel);
        
        // Cria e adiciona a aba de estatísticas
        StatistiquesModel statsModel = new StatistiquesModel(employeDAO);
        statistiquesPanel = new StatistiquesPanel(statsModel);
        employeDAO.addStatistiquesObserver(statistiquesPanel);
        tabbedPane.addTab("Statistiques", statistiquesPanel);
        
        // Adiciona o TabbedPane ao frame
        add(tabbedPane);
        
        // Adiciona os listeners
        addListeners();
        
        // Adiciona listener para pesquisa
        btnRechercher.addActionListener(e -> rechercherEmployes());
        txtRecherche.addActionListener(e -> rechercherEmployes());
    }
    
    private void addListeners() {
        btnAjouter.addActionListener(e -> ajouterEmploye());
        btnModifier.addActionListener(e -> modifierEmploye());
        btnSupprimer.addActionListener(e -> supprimerEmploye());
        
        tableEmployes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableEmployes.getSelectedRow();
                if (selectedRow >= 0) {
                    employeSelectionne = new Employe();
                    employeSelectionne.setId((int) tableModel.getValueAt(selectedRow, 0));
                    employeSelectionne.setNom((String) tableModel.getValueAt(selectedRow, 1));
                    employeSelectionne.setPoste((String) tableModel.getValueAt(selectedRow, 2));
                    employeSelectionne.setSalaire((double) tableModel.getValueAt(selectedRow, 3));
                    
                    txtNom.setText(employeSelectionne.getNom());
                    txtPoste.setText(employeSelectionne.getPoste());
                    txtSalaire.setText(String.valueOf(employeSelectionne.getSalaire()));
                }
            }
        });
    }
    
    private void chargerEmployes() {
        tableModel.setRowCount(0);
        List<Employe> employes = employeDAO.listerTous();
        for (Employe emp : employes) {
            ajouterLigneTable(emp);
        }
    }
    
    private void ajouterLigneTable(Employe emp) {
        tableModel.addRow(new Object[]{
            emp.getId(),
            emp.getNom(),
            emp.getPoste(),
            emp.getSalaire()
        });
    }
    
    private void ajouterEmploye() {
        try {
            String nom = txtNom.getText().trim();
            String poste = txtPoste.getText().trim();
            double salaire = Double.parseDouble(txtSalaire.getText().trim());
            
            if (nom.isEmpty() || poste.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires");
                return;
            }
            
            Employe emp = new Employe(nom, poste, salaire);
            employeDAO.ajouter(emp);
            clearFields();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le salaire doit être un nombre valide");
        }
    }
    
    private void modifierEmploye() {
        if (employeSelectionne == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un employé à modifier");
            return;
        }
        
        try {
            employeSelectionne.setNom(txtNom.getText().trim());
            employeSelectionne.setPoste(txtPoste.getText().trim());
            employeSelectionne.setSalaire(Double.parseDouble(txtSalaire.getText().trim()));
            
            employeDAO.modifier(employeSelectionne);
            clearFields();
            employeSelectionne = null;
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le salaire doit être un nombre valide");
        }
    }
    
    private void supprimerEmploye() {
        if (employeSelectionne == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un employé à supprimer");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer cet employé ?",
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            employeDAO.supprimer(employeSelectionne.getId());
            clearFields();
            employeSelectionne = null;
        }
    }
    
    private void rechercherEmployes() {
        String terme = txtRecherche.getText().trim();
        String type = (String) comboTypeRecherche.getSelectedItem();
        
        if (!terme.isEmpty()) {
            tableModel.setRowCount(0);
            List<Employe> resultats = employeDAO.rechercher(type, terme);
            for (Employe emp : resultats) {
                ajouterLigneTable(emp);
            }
        } else {
            chargerEmployes();
        }
    }
    
    private void clearFields() {
        txtNom.setText("");
        txtPoste.setText("");
        txtSalaire.setText("");
        tableEmployes.clearSelection();
    }
    
    @Override
    public void employeAjoute(Employe employe) {
        ajouterLigneTable(employe);
    }
    
    @Override
    public void employeModifie(Employe employe) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == employe.getId()) {
                tableModel.setValueAt(employe.getNom(), i, 1);
                tableModel.setValueAt(employe.getPoste(), i, 2);
                tableModel.setValueAt(employe.getSalaire(), i, 3);
                break;
            }
        }
    }
    
    @Override
    public void employeSupprime(int id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == id) {
                tableModel.removeRow(i);
                break;
            }
        }
    }
}