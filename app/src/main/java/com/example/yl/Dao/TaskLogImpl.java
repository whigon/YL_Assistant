package com.example.yl.Dao;

import android.content.Context;

import com.example.yl.Beans.TaskLog;
import com.example.yl.Helper.MyHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class TaskLogImpl extends BaseDao {
    private MyHelper myHelper;
    private Dao<TaskLog, Integer> dao;

    public TaskLogImpl(Context context) {
        myHelper = MyHelper.getInstance(context);
        dao = createTaskLogDao();
    }

    @Override
    public Dao<TaskLog, Integer> getDao() {
        if (dao == null) {
            dao = createTaskLogDao();
        }
        return dao;
    }

    private Dao<TaskLog, Integer> createTaskLogDao() {
        try {
            return myHelper.getDao(TaskLog.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
