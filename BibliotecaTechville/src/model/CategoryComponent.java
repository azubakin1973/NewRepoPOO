package model;

import java.util.List;

public interface CategoryComponent {
    void add(CategoryComponent component);
    void remove(CategoryComponent component);
    CategoryComponent getChild(int index);
    List<CategoryComponent> getChildren();
    String getNom();
    int getId();
    void display();
}