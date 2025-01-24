package model;

public class Editeur {
    private int id;
    private String nom;
    private String adresse;

    // Construtor para novo editeur (sem ID)
    public Editeur(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    // Método toString para facilitar debug
    @Override
    public String toString() {
        return "Editeur{" +
               "id=" + id +
               ", nom='" + nom + '\'' +
               ", adresse='" + adresse + '\'' +
               '}';
    }
}