package tn.esprit.services;
import java.sql.SQLException;
import java.util.List;
public interface Iservice<T> {

    public void add(T t)throws SQLException;
    public void update(T t)throws SQLException;
    public void delete(int i)throws SQLException;
    public List<T> retrieve()throws SQLException;
}
