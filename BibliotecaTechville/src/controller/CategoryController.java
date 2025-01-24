package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import util.DatabaseConnection;

public class CategoryController {
    
    public void saveCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Categorie (Nom, ID_Categorie_Parent) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getNom());
            if (category.getIdCategoryParent() != null) {
                stmt.setInt(2, category.getIdCategoryParent());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categorie";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Integer parentId = rs.getInt("ID_Categorie_Parent");
                if (rs.wasNull()) {
                    parentId = null;
                }
                Category category = new Category(
                    rs.getString("Nom"),
                    parentId
                );
                category.setId(rs.getInt("ID_Categorie"));
                categories.add(category);
            }
        }
        return categories;
    }
    
    public void updateCategory(Category category) throws SQLException {
        String sql = "UPDATE Categorie SET Nom = ?, ID_Categorie_Parent = ? WHERE ID_Categorie = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getNom());
            if (category.getIdCategoryParent() != null) {
                stmt.setInt(2, category.getIdCategoryParent());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, category.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void deleteCategory(int id) throws SQLException {
        String sql = "DELETE FROM Categorie WHERE ID_Categorie = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public Category findCategoryById(int id) throws SQLException {
        String sql = "SELECT * FROM Categorie WHERE ID_Categorie = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer parentId = rs.getInt("ID_Categorie_Parent");
                    if (rs.wasNull()) {
                        parentId = null;
                    }
                    Category category = new Category(
                        rs.getString("Nom"),
                        parentId
                    );
                    category.setId(rs.getInt("ID_Categorie"));
                    return category;
                }
            }
        }
        return null;
    }
}