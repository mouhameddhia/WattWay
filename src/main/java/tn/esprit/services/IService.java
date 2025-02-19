package tn.esprit.services;

import java.sql.SQLException;
import java.util.List;

public interface IService <T> {
    void add(T t) throws SQLException;
    void addP(T t) throws SQLException;

    void delete(T t) throws SQLException;
    void update(T t) throws SQLException;
    List<T> returnList() throws SQLException;

}
