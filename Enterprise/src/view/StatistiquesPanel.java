package view;

import javax.swing.*;
import java.awt.*;
import model.StatistiquesModel;
import observer.StatistiquesObserver;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.Map;

public class StatistiquesPanel extends JPanel implements StatistiquesObserver {
    private StatistiquesModel model;
    private JLabel lblTotalEmployes;
    private JLabel lblSalaireMoyen;
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel;
    
    public StatistiquesPanel(StatistiquesModel model) {
        this.model = model;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        updateStatistiques();
    }
    
    private void initComponents() {
        // Panel para estatísticas gerais
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques générales"));
        
        statsPanel.add(new JLabel("Nombre total d'employés:"));
        lblTotalEmployes = new JLabel("0");
        statsPanel.add(lblTotalEmployes);
        
        statsPanel.add(new JLabel("Salaire moyen:"));
        lblSalaireMoyen = new JLabel("0 CAD");
        statsPanel.add(lblSalaireMoyen);
        
        // Criação do gráfico
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
            "Répartition par poste",
            "Poste",
            "Nombre d'employés",
            dataset
        );
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        
        // Panel para o gráfico
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createTitledBorder("Graphique de répartition"));
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        
        // Botão atualizar
        JButton btnRefresh = new JButton("Rafraîchir");
        btnRefresh.addActionListener(e -> updateStatistiques());
        
        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(chartContainer, BorderLayout.CENTER);
        mainPanel.add(btnRefresh, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public void updateStatistiques() {
        model.calculerStatistiques();
        
        // Atualiza labels
        lblTotalEmployes.setText(String.valueOf(model.getTotalEmployes()));
        lblSalaireMoyen.setText(String.format("%.2f CAD", model.getSalaireMoyen()));
        
        // Atualiza o gráfico
        dataset.clear();
        for (Map.Entry<String, Integer> entry : model.getRepartitionParPoste().entrySet()) {
            dataset.addValue(entry.getValue(), "Employés", entry.getKey());
        }
    }
    
    @Override
    public void statistiquesUpdated() {
        updateStatistiques();
    }
}