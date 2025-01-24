package model;

import pattern.observer.Observer;
import java.util.Date;

public class Member implements Observer {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private Date dateInscription;
    // Constructor
    public Member(String nom, String prenom, String email, Date dateInscription) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateInscription = dateInscription;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }
    @Override
    public void update(String message) {
        // Aqui implementaremos a notificação ao membro
        System.out.println("Notification pour " + prenom + " " + nom + ": " + message);
    }
    
}