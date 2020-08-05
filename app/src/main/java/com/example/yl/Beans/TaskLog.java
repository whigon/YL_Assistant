package com.example.yl.Beans;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "task_log")
public class TaskLog implements Serializable {
    @DatabaseField(columnName = "id", generatedId = true, unique = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "contract_no", unique = true, canBeNull = false)
    private String contractNo;

    @DatabaseField(columnName = "contract_name", canBeNull = false)
    private String contractName;

    @DatabaseField(columnName = "total_amount", canBeNull = false)
    private float totalAmount;

    @DatabaseField(columnName = "remittee", canBeNull = false)
    private String remittee;

    @DatabaseField(columnName = "department", canBeNull = false)
    private String department;

    @DatabaseField(columnName = "operator")
    private String operator;

    @DatabaseField(columnName = "date", canBeNull = false, dataType = DataType.DATE)
    private Date date;

    @DatabaseField(columnName = "deadline", canBeNull = false, dataType = DataType.DATE)
    private Date deadline;

    @DatabaseField(columnName = "comment")
    private String comment;

    @DatabaseField(columnName = "state", canBeNull = false)
    private int state;

    public TaskLog() {

    }

    public TaskLog(String contractNo, String contractName, float totalAmount, String remittee, String department, String operator, Date date, Date deadline, String comment, int state) {
        this.contractNo = contractNo;
        this.contractName = contractName;
        this.totalAmount = totalAmount;
        this.remittee = remittee;
        this.department = department;
        this.operator = operator;
        this.date = date;
        this.deadline = deadline;
        this.comment = comment;
        this.state = state;
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

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRemittee() {
        return remittee;
    }

    public void setRemittee(String remittee) {
        this.remittee = remittee;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "TaskLog{" +
                "id=" + id +
                ", contractNo='" + contractNo + '\'' +
                ", contractName='" + contractName + '\'' +
                ", totalAmount=" + totalAmount +
                ", remittee='" + remittee + '\'' +
                ", department='" + department + '\'' +
                ", operator='" + operator + '\'' +
                ", date=" + date +
                ", deadline=" + deadline +
                ", comment='" + comment + '\'' +
                ", state=" + state +
                '}';
    }
}
