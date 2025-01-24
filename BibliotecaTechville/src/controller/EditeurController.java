package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Editeur;
import util.DatabaseConnection;

public class EditeurController {
    
    public void saveEditeur(Editeur editeur) throws SQLException {
        String sql = "INSERT INTO Editeur (Nom, Adresse) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, editeur.getNom());
            stmt.setString(2, editeur.getAdresse());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    editeur.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public List<Editeur> getAllEditeurs() throws SQLException {
        List<Editeur> editeurs = new ArrayList<>();
        String sql = "SELECT * FROM Editeur";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Editeur editeur = new Editeur(
                    rs.getString("Nom"),
                    rs.getString("Adresse")
                );
                editeur.setId(rs.getInt("ID_Editeur"));
                editeurs.add(editeur);
            }
        }
        return editeurs;
    }
    
    public void updateEditeur(Editeur editeur) throws SQLException {
        String sql = "UPDATE Editeur SET Nom = ?, Adresse = ? WHERE ID_Editeur = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, editeur.getNom());
            stmt.setString(2, editeur.getAdresse());
            stmt.setInt(3, editeur.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void deleteEditeur(int id) throws SQLException {
        String sql = "DELETE FROM Editeur WHERE ID_Editeur = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public Editeur findEditeurById(int id) throws SQLException {
        String sql = "SELECT * FROM Editeur WHERE ID_Editeur = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Editeur editeur = new Editeur(
                        rs.getString("Nom"),
                        rs.getString("Adresse")
                    );
                    editeur.setId(rs.getInt("ID_Editeur"));
                    return editeur;
                }
            }
        }
        return null;
    }
}