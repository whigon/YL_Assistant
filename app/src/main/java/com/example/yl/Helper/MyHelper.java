package com.example.yl.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.yl.Beans.PaymentRecord;
import com.example.yl.Beans.TaskInfo;
import com.example.yl.Beans.TaskLog;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class MyHelper extends OrmLiteSqliteOpenHelper {
    //定义数据库存放位置，便于以后查看
    private static final String DATABASE_NAME = "Task.db";
    //定义数据库的版本号，当数据库需要升级时进行更改
    private static final int DATABASE_VERSION = 1;
    //创建DetailDataOpenHelper实例
    private static MyHelper instance;

    private MyHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    public static synchronized MyHelper getDatabaseHelper(Context context) {
//        if (instance == null) {
//            synchronized (MyHelper.class) {
//                if (instance == null) {
//                    instance = new MyHelper(context);
//                }
//            }
//        }
//        return instance;
//    }

    public static synchronized MyHelper getInstance(Context context){
        if(instance == null){
            instance = new MyHelper(context);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, TaskInfo.class);
            TableUtils.createTable(connectionSource, TaskLog.class);
            TableUtils.createTable(connectionSource, PaymentRecord.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, TaskInfo.class, false);
            TableUtils.dropTable(connectionSource, TaskLog.class, false);
            TableUtils.dropTable(connectionSource, PaymentRecord.class, false);


            TableUtils.createTable(connectionSource, TaskInfo.class);
            TableUtils.createTable(connectionSource, TaskLog.class);
            TableUtils.createTable(connectionSource, PaymentRecord.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
