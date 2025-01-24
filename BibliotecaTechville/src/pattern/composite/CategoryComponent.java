package pattern.composite;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

public interface CategoryComponent {
    void add(CategoryComponent component);
    void remove(CategoryComponent component);
    void display();
    String getName();
    List<CategoryComponent> getChildren();
}

public class Category implements CategoryComponent {
    private String name;
    private List<CategoryComponent> children = new ArrayList<>();
    
    public Category(String name) {
        this.name = name;
    }
    
    @Override
    public void add(CategoryComponent component) {
        children.add(component);
    }
    
    @Override
    public void remove(CategoryComponent component) {
        children.remove(component);
    }
    
    @Override
    public void display() {
        System.out.println("Catégorie: " + name);
        for (CategoryComponent child : children) {
            child.display();
        }
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public List<CategoryComponent> getChildren() {
        return children;
    }
}

public class BookItem implements CategoryComponent {
    private Book book;
    
    public BookItem(Book book) {
        this.book = book;
    }
    
    @Override
    public void add(CategoryComponent component) {
        throw new UnsupportedOperationException("Cannot add to a book");
    }
    
    @Override
    public void remove(CategoryComponent component) {
        throw new UnsupportedOperationException("Cannot remove from a book");
    }
    
    @Override
    public void display() {
        System.out.println("Livre: " + book.getTitle());
    }
    
    @Override
    public String getName() {
        return book.getTitle();
    }
    
    @Override
    public List<CategoryComponent> getChildren() {
        return Collections.emptyList();
    }
}