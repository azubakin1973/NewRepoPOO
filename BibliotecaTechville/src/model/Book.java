package model;

import pattern.observer.Subject;
import java.util.List;
import java.util.ArrayList;

public abstract class Book extends Subject {
    protected int id;
    protected String title;
    protected int publicationYear;
    protected String isbn;
    protected int publisherId;
    protected int categoryId;
    protected boolean available;
    
    public Book(String title, int publicationYear, String isbn, int publisherId, int categoryId) {
        this.title = title;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.publisherId = publisherId;
        this.categoryId = categoryId;
        this.available = true;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public int getPublisherId() {
        return publisherId;
    }
    
    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
        if (available) {
            notifyObservers("Le livre '" + title + "' est maintenant disponible.");
        }
    }
    
    // Método abstrato para cálculo de multa
    public abstract double calculateLateFee(int daysLate);
}