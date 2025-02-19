package tn.esprit.services;
import tn.esprit.entities.Client;
import tn.esprit.entities.User;

import java.util.List;

public interface IService<T> {

    void add(T entity);
    boolean updateAccount(int id, T t);
    void delete(int idUser);

    T getById(int id);
    List<T> getAll();

    User login(String email, String password);




   // User login(String email, String password);

}
