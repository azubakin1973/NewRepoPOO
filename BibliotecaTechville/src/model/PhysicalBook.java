package model;

public class PhysicalBook extends Book {
    public PhysicalBook(String title, int publicationYear, String isbn, int publisherId, int categoryId) {
        super(title, publicationYear, isbn, publisherId, categoryId);
    }
    
    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * 1.0; // 1 euro por dia
    }
}