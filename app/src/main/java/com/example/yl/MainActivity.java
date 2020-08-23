package com.example.yl;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.yl.Adapter.MyAdapter;
import com.example.yl.Beans.TaskInfo;
import com.example.yl.Dao.BaseDao;
import com.example.yl.Dao.TaskInfoImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<TaskInfo> taskInfoList = new ArrayList<>();
    private MyAdapter myAdapter;
    private GridView gridView;
    private Toolbar toolbar;

    private BaseDao taskInfoDao = new TaskInfoImpl(MainActivity.this);
//    private BaseDao paymentRecordDao = new PaymentRecordImpl(MainActivity.this);

    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    private Date pickedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInfoList.clear();
        taskInfoList.addAll(taskInfoDao.getAll());

        initView();
        myAdapter = new MyAdapter(MainActivity.this, taskInfoList);
        gridView.setAdapter(myAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_1:
                // 数据变化了才能影响notifyDataSetChanged，改变内存
                taskInfoList.clear();
                taskInfoList.addAll(taskInfoDao.getAll());
                taskInfoList.sort((o1, o2) -> {
                    if (o1.getTotalAmount() < o2.getTotalAmount())
                        return 1;
                    else if (o1.getTotalAmount() > o2.getTotalAmount())
                        return -1;
                    return 0;
                });
                myAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_2:
                taskInfoList.clear();
                taskInfoList.addAll(taskInfoDao.getAll());
                taskInfoList.sort((o1, o2) -> {
                    if (o1.getDeadline().before(o2.getDeadline()))
                        return 1;
                    else if (o1.getDeadline().after(o2.getDeadline()))
                        return -1;
                    return 0;
                });
                myAdapter.notifyDataSetChanged();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridView = findViewById(R.id.grid_view);
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            // TODO: add function: modify and delete item

            return false;
        });
        // TODO: add function: add new task
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_add, null, false);
            Dialog dialog = makeDialog(view);
            // 点击视图外不会触发返回
            dialog.setCanceledOnTouchOutside(false);
            // TODO: 魔幻现实主义代码
            final View finalView = view;
            dialog.setOnCancelListener(dialog1 -> {
                Log.d(TAG, "onCancel: ");
                Button btn = finalView.findViewById(R.id.btn_confirm);
                // 默认的日期是当日
                pickedDate = new Date();
                btn.callOnClick();
            });
            createOrUpdate(dialog, false, new Bundle());
        });
    }

    private Dialog makeDialog(View view) {
        Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
        dialog.getWindow().setContentView(view);
        dialog.show();

        return dialog;
    }

    private void createOrUpdate(final Dialog dialog, final boolean isUpdate, final Bundle bundle) {
        final View view = Objects.requireNonNull(dialog.getWindow()).getDecorView();
        TextView titleText = view.findViewById(R.id.title);

        final EditText edtContractName = view.findViewById(R.id.edt_contract_name);
        final EditText edtContractNo = view.findViewById(R.id.edt_contract_no);
        final EditText edtRemittee = view.findViewById(R.id.edt_remittee);
        final EditText edtTotalAmount = view.findViewById(R.id.edt_total_amount);
        edtTotalAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);// 设置输入为数字
        edtTotalAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        final EditText edtPayAmount = view.findViewById(R.id.edt_pay_amount);
        edtPayAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);// 设置输入为数字
        edtPayAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        final EditText edtDepartment = view.findViewById(R.id.edt_department);
        final EditText edtOperator = view.findViewById(R.id.edt_operator);
        final EditText edtComment = view.findViewById(R.id.edt_comment);

        Button btnTime = view.findViewById(R.id.btn_pickTime);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);

        final int mYear, mMonth, mDay;

        // 填充信息
        if (isUpdate) {
            titleText.setText("编辑合同");

            edtContractName.setText(bundle.getString("contract_name"));
            edtContractNo.setText(bundle.getString("contract_no"));
            edtRemittee.setText(bundle.getString("remittee"));
            edtTotalAmount.setText("" + bundle.getFloat("total_amount"));
            edtPayAmount.setText("" + bundle.getFloat("pay_amount"));
            edtDepartment.setText(bundle.getString("department", ""));
            edtOperator.setText(bundle.getString("operator", ""));
            edtComment.setText(bundle.getString("comment", ""));

            btnTime.setText("修改日期");

            mYear = Integer.parseInt(Objects.requireNonNull(bundle.getString("year")));
            mMonth = Integer.parseInt(Objects.requireNonNull(bundle.getString("month")));
            mDay = Integer.parseInt(Objects.requireNonNull(bundle.getString("day")));
            try {
                pickedDate = format.parse(mYear + "/" + mMonth + "/" + mDay);
            } catch (ParseException e) {
                Toast.makeText(MainActivity.this, "自动时间设置异常", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Calendar ca = Calendar.getInstance();
            mYear = ca.get(Calendar.YEAR);
            mMonth = ca.get(Calendar.MONTH);
            mDay = ca.get(Calendar.DAY_OF_MONTH);
        }

        btnTime.setOnClickListener(v -> {
            //收起软键盘
            InputMethodManager manager = ((InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE));
            if (manager != null)
                manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            // 获取日期
            final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        try {
                            pickedDate = format.parse(year + "/" + (month + 1) + "/" + dayOfMonth);
                            Log.d(TAG, "onDateSet: " + pickedDate);
                        } catch (ParseException e) {
                            Toast.makeText(MainActivity.this, "时间设置异常", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.setTitle("请选择日期");
            datePickerDialog.show();
        });

        btnConfirm.setOnClickListener(v -> {
            try {
                String contractName = edtContractName.getText().toString();
                String contractNo = edtContractNo.getText().toString();
                String remittee = edtRemittee.getText().toString();
                Float totalAmount = Float.parseFloat(edtTotalAmount.getText().toString());
                Float payAmount = Float.parseFloat(edtPayAmount.getText().toString().equals("") ? "0" : edtPayAmount.getText().toString());// 已付金额不是必选
                String department = edtDepartment.getText().toString();
                String operator = edtOperator.getText().toString();
                String comment = edtComment.getText().toString();

                // 输入检查
                if (contractName.equals(""))
                    Toast.makeText(MainActivity.this, "请输入合同名称", Toast.LENGTH_LONG).show();
                else if (contractNo.equals(""))
                    Toast.makeText(MainActivity.this, "请输入合同编号", Toast.LENGTH_LONG).show();
                else if (remittee.equals(""))
                    Toast.makeText(MainActivity.this, "请输入收款方", Toast.LENGTH_LONG).show();
                else if (totalAmount <= 0)
                    Toast.makeText(MainActivity.this, "总金额必须大于0", Toast.LENGTH_LONG).show();
                else if (payAmount < 0)
                    Toast.makeText(MainActivity.this, "已付金额不能小于0", Toast.LENGTH_LONG).show();
                else if (pickedDate == null)
                    Toast.makeText(MainActivity.this, "请设置截止时间", Toast.LENGTH_LONG).show();
                else {
                    // 0-新建 1-部分完成 2-全部完成
                    int status = (payAmount == 0) ? 0 : (payAmount == totalAmount ? 2 : 1);

                    TaskInfo taskInfoBean;
                    String reminderStr;
                    if (isUpdate) {
                        int id = bundle.getInt("id");
                        String dateStr = bundle.getString("dateStr");
                        Date date = format.parse(dateStr);
                        taskInfoBean = new TaskInfo(id, contractNo, contractName, totalAmount, payAmount, 0, status, remittee, department, operator, date, pickedDate, comment);
                        taskInfoDao.update(taskInfoBean);
                        reminderStr = "更新成功";
                    } else {
                        taskInfoBean = new TaskInfo(contractNo, contractName, totalAmount, payAmount, 0, status, remittee, department, operator, new Date(), pickedDate, comment);
                        taskInfoDao.insert(taskInfoBean);  // TODO:相同的插入怎么解决
                        reminderStr = "新建成功";
                    }
                    pickedDate = null;
                    Toast.makeText(MainActivity.this, reminderStr, Toast.LENGTH_SHORT).show();

                    // 重新取出数据
                    taskInfoList.clear();
                    taskInfoList.addAll(taskInfoDao.getAll());
                    myAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            } catch (NumberFormatException emptyString) {
                Log.d(TAG, "onClick: " + emptyString);
                Toast.makeText(MainActivity.this, "请输入总金额", Toast.LENGTH_LONG).show();
            } catch (ParseException parseException) {
                Log.d(TAG, "onClick: " + parseException);
                Toast.makeText(MainActivity.this, "日期转化异常", Toast.LENGTH_LONG).show();
            }
        });
    }
}
