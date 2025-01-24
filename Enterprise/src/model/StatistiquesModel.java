package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.EmployeDAO;

public class StatistiquesModel {
    private EmployeDAO employeDAO;
    private int totalEmployes;
    private double salaireMoyen;
    private Map<String, Integer> repartitionParPoste;
    
    public StatistiquesModel(EmployeDAO employeDAO) {
        this.employeDAO = employeDAO;
        this.repartitionParPoste = new HashMap<>();
        calculerStatistiques();
    }
    
    public void calculerStatistiques() {
        List<Employe> employes = employeDAO.listerTous();
        
        // Total d'employés
        totalEmployes = employes.size();
        
        // Salaire moyen
        double totalSalaires = 0;
        repartitionParPoste.clear();
        
        for (Employe emp : employes) {
            totalSalaires += emp.getSalaire();
            
            // Répartition par poste
            String poste = emp.getPoste();
            repartitionParPoste.put(poste, repartitionParPoste.getOrDefault(poste, 0) + 1);
        }
        
        salaireMoyen = totalEmployes > 0 ? totalSalaires / totalEmployes : 0;
    }
    
    // Getters
    public int getTotalEmployes() { return totalEmployes; }
    public double getSalaireMoyen() { return salaireMoyen; }
    public Map<String, Integer> getRepartitionParPoste() { return repartitionParPoste; }
}