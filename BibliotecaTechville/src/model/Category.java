package model;

import java.util.ArrayList;
import java.util.List;

public class Category implements CategoryComponent {
    private int id;
    private String nom;
    private Integer idCategoryParent;
    private List<CategoryComponent> children;
    
    public Category(String nom, Integer idCategoryParent) {
        this.nom = nom;
        this.idCategoryParent = idCategoryParent;
        this.children = new ArrayList<>();
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
    public CategoryComponent getChild(int index) {
        return children.get(index);
    }
    
    @Override
    public List<CategoryComponent> getChildren() {
        return children;
    }
    
    @Override
    public String getNom() {
        return nom;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void display() {
        System.out.println("Catégorie: " + nom);
        for (CategoryComponent child : children) {
            child.display();
        }
    }
    
    // Getters and Setters adicionais
    public void setId(int id) {
        this.id = id;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public Integer getIdCategoryParent() {
        return idCategoryParent;
    }
    
    public void setIdCategoryParent(Integer idCategoryParent) {
        this.idCategoryParent = idCategoryParent;
    }
}