package com.example.yl.Dao;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public abstract class BaseDao {
    public abstract <T> Dao<T, Integer> getDao();

    public <T> void insert(T t) {
        try {
            Dao<T, Integer> dao = getDao();
            dao.create(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> void update(T t) {
        try {
            Dao<T, Integer> dao = getDao();
            dao.update(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> void delete(T t) {
        try {
            Dao<T, Integer> dao = getDao();
            dao.delete(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> getAll() {
        try {
            Dao<T, Integer> dao = getDao();
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
