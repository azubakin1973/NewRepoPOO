package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Author;
import util.DatabaseConnection;

public class AuthorController {
    
    public void saveAuthor(Author author) throws SQLException {
        String sql = "INSERT INTO Auteur (Nom, Prenom, Date_Naissance) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, author.getNom());
            stmt.setString(2, author.getPrenom());
            stmt.setDate(3, new java.sql.Date(author.getDateNaissance().getTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    author.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public List<Author> getAllAuthors() throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM Auteur";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Author author = new Author(
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getDate("Date_Naissance")
                );
                author.setId(rs.getInt("ID_Auteur"));
                authors.add(author);
            }
        }
        return authors;
    }
    
    public void updateAuthor(Author author) throws SQLException {
        String sql = "UPDATE Auteur SET Nom = ?, Prenom = ?, Date_Naissance = ? WHERE ID_Auteur = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, author.getNom());
            stmt.setString(2, author.getPrenom());
            stmt.setDate(3, new java.sql.Date(author.getDateNaissance().getTime()));
            stmt.setInt(4, author.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void deleteAuthor(int id) throws SQLException {
        String sql = "DELETE FROM Auteur WHERE ID_Auteur = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public Author findAuthorById(int id) throws SQLException {
        String sql = "SELECT * FROM Auteur WHERE ID_Auteur = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Author author = new Author(
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getDate("Date_Naissance")
                    );
                    author.setId(rs.getInt("ID_Auteur"));
                    return author;
                }
            }
        }
        return null;
    }
}