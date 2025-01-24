package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Loan;
import model.Book;
import model.Member;
import util.DatabaseConnection;
import java.util.Date;

public class LoanController {
    private BookController bookController;
    private static final int MAX_LOANS_PER_MEMBER = 3;
    
    public LoanController() {
        this.bookController = new BookController();
    }
    
    private boolean isBookReserved(int bookId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM Emprunt WHERE ID_Livre = ? AND Date_Retour_Effective IS NULL";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public void saveLoan(Loan loan) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Verificar limite de empréstimos
            if (getCurrentLoansCount(loan.getMemberId()) >= MAX_LOANS_PER_MEMBER) {
                throw new SQLException("Limite de prêts atteinte pour ce membre");
            }
            
            // Verificar se o livro está reservado
            if (hasReservation(loan.getBookId())) {
                throw new SQLException("Ce livre est déjà réservé");
            }
            
            // Verificar se o livro já está emprestado
            if (isBookReserved(loan.getBookId(), conn)) {
                throw new SQLException("Le livre est déjà emprunté!");
            }
            
            // Verificar período de empréstimo
            if (!loan.isValidLoanPeriod()) {
                throw new SQLException("La période de prêt ne peut pas dépasser 30 jours");
            }
            
            String sql = "INSERT INTO Emprunt (ID_Livre, ID_Membre, Date_Emprunt, Date_Retour_Prevue) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, loan.getBookId());
                stmt.setInt(2, loan.getMemberId());
                stmt.setDate(3, new java.sql.Date(loan.getLoanDate().getTime()));
                stmt.setDate(4, new java.sql.Date(loan.getExpectedReturnDate().getTime()));
                
                stmt.executeUpdate();
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        loan.setId(generatedKeys.getInt(1));
                    }
                }
                
                Book book = bookController.findBookById(loan.getBookId());
                if (book != null) {
                    book.setAvailable(false);
                }
            }
        }
    }

    public void returnBook(int loanId, Date returnDate) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            Loan loan = findLoanById(loanId);
            if (loan != null) {
                String sql = "UPDATE Emprunt SET Date_Retour_Effective = ? WHERE ID_Emprunt = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDate(1, new java.sql.Date(returnDate.getTime()));
                    stmt.setInt(2, loanId);
                    stmt.executeUpdate();
                }
                
                Book book = bookController.findBookById(loan.getBookId());
                if (book != null) {
                    book.setAvailable(true);
                    notifyReservations(loan.getBookId());
                }
            }
        }
    }
    
    private void notifyReservations(int bookId) throws SQLException {
        String sql = "SELECT m.* FROM Membre m " +
                    "JOIN Reservation r ON m.ID_Membre = r.ID_Membre " +
                    "WHERE r.ID_Livre = ?";
                    
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            Book book = bookController.findBookById(bookId);
            while (rs.next() && book != null) {
                Member member = new Member(
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getString("Email"),
                    rs.getDate("Date_Inscription")
                );
                member.setId(rs.getInt("ID_Membre"));
                book.attach(member);
            }
        }
    }
    
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Emprunt";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("ID_Livre"),
                    rs.getInt("ID_Membre"),
                    rs.getDate("Date_Emprunt"),
                    rs.getDate("Date_Retour_Prevue")
                );
                loan.setId(rs.getInt("ID_Emprunt"));
                
                Date actualReturnDate = rs.getDate("Date_Retour_Effective");
                if (actualReturnDate != null) {
                    loan.setActualReturnDate(actualReturnDate);
                }
                
                loans.add(loan);
            }
        }
        return loans;
    }
    
    public List<Loan> getOpenLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Emprunt WHERE Date_Retour_Effective IS NULL";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("ID_Livre"),
                    rs.getInt("ID_Membre"),
                    rs.getDate("Date_Emprunt"),
                    rs.getDate("Date_Retour_Prevue")
                );
                loan.setId(rs.getInt("ID_Emprunt"));
                loans.add(loan);
            }
        }
        return loans;
    }
    
    public Loan findLoanById(int id) throws SQLException {
        String sql = "SELECT * FROM Emprunt WHERE ID_Emprunt = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = new Loan(
                        rs.getInt("ID_Livre"),
                        rs.getInt("ID_Membre"),
                        rs.getDate("Date_Emprunt"),
                        rs.getDate("Date_Retour_Prevue")
                    );
                    loan.setId(rs.getInt("ID_Emprunt"));
                    
                    Date actualReturnDate = rs.getDate("Date_Retour_Effective");
                    if (actualReturnDate != null) {
                        loan.setActualReturnDate(actualReturnDate);
                    }
                    
                    return loan;
                }
            }
        }
        return null;
    }
    
    public int getCurrentLoansCount(int memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Emprunt WHERE ID_Membre = ? AND Date_Retour_Effective IS NULL";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public boolean canRenewLoan(int loanId) throws SQLException {
        String sql = "SELECT e.*, r.ID_Reservation " +
                    "FROM Emprunt e " +
                    "LEFT JOIN Reservation r ON e.ID_Livre = r.ID_Livre " +
                    "WHERE e.ID_Emprunt = ? AND e.Date_Retour_Effective IS NULL";
                    
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getObject("ID_Reservation") == null;
            }
            return false;
        }
    }
    
    public boolean renewLoan(int loanId, Date newReturnDate) throws SQLException {
        if (!canRenewLoan(loanId)) {
            return false;
        }
        
        updateLoanReturnDate(loanId, newReturnDate);
        return true;
    }
    
    public void updateLoanReturnDate(int loanId, Date newDate) throws SQLException {
        String sql = "UPDATE Emprunt SET Date_Retour_Prevue = ? WHERE ID_Emprunt = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(newDate.getTime()));
            stmt.setInt(2, loanId);
            stmt.executeUpdate();
        }
    }
    
    public boolean hasReservation(int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Reservation WHERE ID_Livre = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}