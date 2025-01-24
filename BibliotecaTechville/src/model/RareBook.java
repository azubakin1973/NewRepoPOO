package model;

public class RareBook extends Book {
    public RareBook(String title, int publicationYear, String isbn, int publisherId, int categoryId) {
        super(title, publicationYear, isbn, publisherId, categoryId);
    }
    
    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * 2.0; // 2 euros por dia
    }
}