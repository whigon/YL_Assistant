package com.example.yl.Dao;

import android.content.Context;

import com.example.yl.Beans.TaskInfo;
import com.example.yl.Helper.MyHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class TaskInfoImpl extends BaseDao {
    private MyHelper myHelper;
    private Dao<TaskInfo, Integer> dao;

    public TaskInfoImpl(Context context) {
        myHelper = MyHelper.getInstance(context);
        dao = createTaskInfoDao();
    }

    @Override
    public Dao<TaskInfo, Integer> getDao() {
        if (dao == null) {
            dao = createTaskInfoDao();
        }
        return dao;
    }

    private Dao createTaskInfoDao() {
        try {
            return myHelper.getDao(TaskInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
