// EmployeDAO.java
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Employe;
import observer.EmployeObserver;
import observer.StatistiquesObserver;
import connection.ConnectionFactory;

public class EmployeDAO {
    private List<EmployeObserver> observers = new ArrayList<>();
    private List<StatistiquesObserver> statsObservers = new ArrayList<>();
    
    public void addObserver(EmployeObserver observer) {
        observers.add(observer);
    }
    
    public void addStatistiquesObserver(StatistiquesObserver observer) {
        statsObservers.add(observer);
    }
    
    private void notifyStatistiquesObservers() {
        statsObservers.forEach(StatistiquesObserver::statistiquesUpdated);
    }
    
    public void ajouter(Employe employe) {
        String sql = "INSERT INTO employes (nom, poste, salaire) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, employe.getNom());
            stmt.setString(2, employe.getPoste());
            stmt.setDouble(3, employe.getSalaire());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                employe.setId(rs.getInt(1));
                observers.forEach(o -> o.employeAjoute(employe));
                notifyStatistiquesObservers();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout: " + e.getMessage());
        }
    }
    
    public List<Employe> listerTous() {
        List<Employe> employes = new ArrayList<>();
        String sql = "SELECT * FROM employes";
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employe emp = new Employe();
                emp.setId(rs.getInt("id"));
                emp.setNom(rs.getString("nom"));
                emp.setPoste(rs.getString("poste"));
                emp.setSalaire(rs.getDouble("salaire"));
                employes.add(emp);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage());
        }
        
        return employes;
    }
    
    public void modifier(Employe employe) {
        String sql = "UPDATE employes SET nom = ?, poste = ?, salaire = ? WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employe.getNom());
            stmt.setString(2, employe.getPoste());
            stmt.setDouble(3, employe.getSalaire());
            stmt.setInt(4, employe.getId());
            
            if (stmt.executeUpdate() > 0) {
                observers.forEach(o -> o.employeModifie(employe));
                notifyStatistiquesObservers();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    public void supprimer(int id) {
        String sql = "DELETE FROM employes WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            if (stmt.executeUpdate() > 0) {
                observers.forEach(o -> o.employeSupprime(id));
                notifyStatistiquesObservers();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage());
        }
    }
    
    public List<Employe> rechercher(String type, String terme) {
        List<Employe> employes = new ArrayList<>();
        String sql = "SELECT * FROM employes WHERE ";
        
        if (type.equals("Nom")) {
            sql += "nom LIKE ?";
        } else {
            sql += "poste LIKE ?";
        }
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + terme + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Employe emp = new Employe();
                emp.setId(rs.getInt("id"));
                emp.setNom(rs.getString("nom"));
                emp.setPoste(rs.getString("poste"));
                emp.setSalaire(rs.getDouble("salaire"));
                employes.add(emp);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche: " + e.getMessage());
        }
        
        return employes;
    }
}