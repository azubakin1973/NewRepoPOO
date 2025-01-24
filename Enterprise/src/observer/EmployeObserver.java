package observer;

import model.Employe;

public interface EmployeObserver {
    void employeAjoute(Employe employe);
    void employeModifie(Employe employe);
    void employeSupprime(int id);
}