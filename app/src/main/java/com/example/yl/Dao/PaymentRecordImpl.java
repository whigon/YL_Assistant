package com.example.yl.Dao;

import android.content.Context;

import com.example.yl.Beans.PaymentRecord;
import com.example.yl.Helper.MyHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class PaymentRecordImpl extends BaseDao {
    private MyHelper myHelper;
    private Dao<PaymentRecord, Integer> dao;

    public PaymentRecordImpl(Context context){
        myHelper = MyHelper.getInstance(context);
        dao = createPaymentRecordDao();
    }

    @Override
    public Dao<PaymentRecord, Integer> getDao() {
        if (dao == null) {
            dao = createPaymentRecordDao();
        }
        return dao;
    }

    private Dao createPaymentRecordDao() {
        try {
            return myHelper.getDao(PaymentRecord.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
