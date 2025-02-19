package tn.esprit.services;

import tn.esprit.entities.Submission;

import java.sql.SQLException;
import java.util.List;

public interface IService <T> {
    void add(T t) throws SQLException;
    void addC(T t);

    void delete(T t);
    void update(T t);
    List<T> returnList() throws SQLException;

}
