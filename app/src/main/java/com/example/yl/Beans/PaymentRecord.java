package com.example.yl.Beans;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "payment_record")
public class PaymentRecord implements Serializable {
    @DatabaseField(generatedId = true, unique = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "contract_no", canBeNull = false)
    private String contractNo;

    @DatabaseField(columnName = "amount", canBeNull = false)
    private float amount;

    @DatabaseField(columnName = "date", canBeNull = false, dataType = DataType.DATE)
    private Date date;

    public PaymentRecord(){

    }

    public PaymentRecord(String contractNo, float amount, Date date) {
        this.contractNo = contractNo;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PaymentRecord{" +
                "id=" + id +
                ", contractNo='" + contractNo + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
