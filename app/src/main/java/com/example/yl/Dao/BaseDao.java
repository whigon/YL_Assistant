package com.example.yl.Dao;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public abstract class BaseDao<T> {
    public abstract <T> Dao<T, Integer> getDao();

    public void insert(T t) {
        try {
            getDao().create(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(T t) {
        try {
            getDao().update(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(T t) {
        try {
            getDao().delete(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<T> getAll() {
        try {
            return (List<T>) getDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
