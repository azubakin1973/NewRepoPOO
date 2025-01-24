package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Book;
import model.PhysicalBook;
import model.RareBook;
import util.DatabaseConnection;

import service.interfaces.IBookValidator;
import exception.ValidationException;
import view.BookPanel;

public class BookController {
    private final IBookRepository bookRepository;
    private final IBookValidator validator;
    private List<BookPanel> views;
    
    public BookController(IBookRepository bookRepository, IBookValidator validator) {
        this.bookRepository = bookRepository;
        this.validator = validator;
        this.views = new ArrayList<>();
    }
    
    public void registerView(BookPanel view) {
        views.add(view);
    }

    public void saveBook(Book book) throws ValidationException {
        validator.validate(book);
        bookRepository.save(book);
        notifyViews();
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Livre";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Book book = createBookFromResultSet(rs);
                books.add(book);
            }
        }
        return books;
    }

    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        Book book;
        
        if ("RARE".equals(type)) {
            book = new RareBook(
                rs.getString("Titre"),
                rs.getInt("Annee_Publication"),
                rs.getString("ISBN"),
                rs.getInt("ID_Editeur"),
                rs.getInt("ID_Categorie")
            );
        } else {
            book = new PhysicalBook(
                rs.getString("Titre"),
                rs.getInt("Annee_Publication"),
                rs.getString("ISBN"),
                rs.getInt("ID_Editeur"),
                rs.getInt("ID_Categorie")
            );
        }
        
        book.setId(rs.getInt("ID_Livre"));
        book.setAvailable(rs.getBoolean("disponible"));
        return book;
    }

    public void updateBook(Book book) throws ValidationException {
        try {
            validator.validate(book);
            String sql = "UPDATE Livre SET Titre = ?, Annee_Publication = ?, ISBN = ?, " +
                        "ID_Editeur = ?, ID_Categorie = ?, type = ?, disponible = ? " +
                        "WHERE ID_Livre = ?";

            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, book.getTitle());
                stmt.setInt(2, book.getPublicationYear());
                stmt.setString(3, book.getIsbn());
                stmt.setInt(4, book.getPublisherId());
                stmt.setInt(5, book.getCategoryId());
                stmt.setString(6, book instanceof RareBook ? "RARE" : "PHYSICAL");
                stmt.setBoolean(7, book.isAvailable());
                stmt.setInt(8, book.getId());
                
                stmt.executeUpdate();
                notifyViews();
            }
        } catch (SQLException e) {
            throw new ValidationException("Erreur lors de la mise à jour du livre: " + e.getMessage());
        }
    }

    public void deleteBook(int id) throws ValidationException {
        try {
            String sql = "DELETE FROM Livre WHERE ID_Livre = ?";
            
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, id);
                stmt.executeUpdate();
                notifyViews();
            }
        } catch (SQLException e) {
            throw new ValidationException("Erreur lors de la suppression du livre: " + e.getMessage());
        }
    }

    public Book findBookById(int id) throws SQLException {
        String sql = "SELECT * FROM Livre WHERE ID_Livre = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createBookFromResultSet(rs);
            }
        }
        return null;
    }

    private void notifyViews() {
        for (BookPanel view : views) {
            view.refreshData();
        }
    }
    
    public double calculateLateFee(int bookId, int daysLate) throws SQLException {
        Book book = findBookById(bookId);
        if (book != null) {
            return book.calculateLateFee(daysLate);
        }
        throw new SQLException("Livre non trouvé");
    }
}