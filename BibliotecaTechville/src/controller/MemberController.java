package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Member;
import util.DatabaseConnection;

public class MemberController {
    
    public void saveMember(Member member) throws SQLException {
        String sql = "INSERT INTO Membre (Nom, Prenom, Email, Date_Inscription) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, member.getNom());
            stmt.setString(2, member.getPrenom());
            stmt.setString(3, member.getEmail());
            stmt.setDate(4, new java.sql.Date(member.getDateInscription().getTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Membre";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Member member = new Member(
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getString("Email"),
                    rs.getDate("Date_Inscription")
                );
                member.setId(rs.getInt("ID_Membre"));
                members.add(member);
            }
        }
        return members;
    }
    
    public void updateMember(Member member) throws SQLException {
        String sql = "UPDATE Membre SET Nom = ?, Prenom = ?, Email = ?, Date_Inscription = ? WHERE ID_Membre = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, member.getNom());
            stmt.setString(2, member.getPrenom());
            stmt.setString(3, member.getEmail());
            stmt.setDate(4, new java.sql.Date(member.getDateInscription().getTime()));
            stmt.setInt(5, member.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM Membre WHERE ID_Membre = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public Member findMemberById(int id) throws SQLException {
        String sql = "SELECT * FROM Membre WHERE ID_Membre = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member(
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getString("Email"),
                        rs.getDate("Date_Inscription")
                    );
                    member.setId(rs.getInt("ID_Membre"));
                    return member;
                }
            }
        }
        return null;
    }
}