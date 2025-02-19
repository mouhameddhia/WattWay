package services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    void add(T t) throws SQLException;

    List<T> returnList();
    void delete(int id);  // Modify to accept ID
    void update(T t);  // Modify to accept ID
}
