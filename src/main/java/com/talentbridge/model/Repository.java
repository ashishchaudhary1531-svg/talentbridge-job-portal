package com.talentbridge.model;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
public interface Repository<T,ID> {
    Optional<T> findById(ID id) throws SQLException;
    List<T> findAll() throws SQLException;
}
