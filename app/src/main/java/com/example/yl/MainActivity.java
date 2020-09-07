package com.example.yl;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.yl.Adapter.MyAdapter;
import com.example.yl.Beans.TaskInfo;
import com.example.yl.Dao.BaseDao;
import com.example.yl.Dao.TaskInfoImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<TaskInfo> taskInfoList = new ArrayList<>();
    private MyAdapter myAdapter;
    private GridView gridView;

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

        if (!checkPermission())
            Toast.makeText(MainActivity.this, "请允许使用日历权限", Toast.LENGTH_LONG).show();
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
        Iterator<TaskInfo> iterator;

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
            case R.id.unfinished:
                taskInfoList.clear();
                taskInfoList.addAll(taskInfoDao.getAll());

                iterator = taskInfoList.iterator();
                while (iterator.hasNext()) {
                    TaskInfo bean = iterator.next();
                    if (bean.getStatus() == 2)
                        iterator.remove();
                }

                myAdapter.notifyDataSetChanged();
                return true;
            case R.id.finished:
                taskInfoList.clear();
                taskInfoList.addAll(taskInfoDao.getAll());

                iterator = taskInfoList.iterator();
                while (iterator.hasNext()) {
                    TaskInfo bean = iterator.next();
                    if (bean.getStatus() != 2)
                        iterator.remove();
                }

                myAdapter.notifyDataSetChanged();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1);
            return false;
        }
        return true;
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridView = findViewById(R.id.grid_view);

        // 长按编辑或删除
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            TaskInfo bean = taskInfoList.get(position);

            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_menu, null, false);
            Dialog dialog = makeDialog(view);

            Button editButton = view.findViewById(R.id.btn_edit);
            Button deleteButton = view.findViewById(R.id.btn_delete);

            editButton.setOnClickListener(v -> {
                dialog.dismiss();
                Bundle bundle = getBundle(bean);

                v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_edit, null, false);
                Dialog editDialog = makeDialog(v);
                // 点击视图外不会触发返回
                editDialog.setCanceledOnTouchOutside(false);
                // TODO: 返回的时候处理逻辑 -- 如果有修改的话提示是否保存，没有的话就直接返回
//                editDialog.setOnCancelListener(dialog1 -> {
//                    Log.d(TAG, "onCancel: ");
//                    Button btn = v.findViewById(R.id.btn_confirm);
//                    // 默认的日期是当日
//                    pickedDate = new Date();
//                    btn.callOnClick();
//                });
                createOrUpdate(editDialog, true, bundle);
            });
            deleteButton.setOnClickListener(v -> {
                dialog.dismiss();
                v = LayoutInflater.from(MainActivity.this).inflate(R.layout.delete, null, false);
                Dialog deleteDialog = makeDialog(v);

                Button btnComfirm = v.findViewById(R.id.btn_confirm);
                Button btnCancel = v.findViewById(R.id.btn_cancel);

                btnComfirm.setOnClickListener(v1 -> {
                    taskInfoDao.delete(bean);
                    taskInfoList.remove(bean);
                    myAdapter.notifyDataSetChanged();
                    deleteDialog.dismiss();
                });
                btnCancel.setOnClickListener(v1 -> {
                    deleteDialog.dismiss();
                });
            });

            // 设为true后，setOnItemClickListener就不会响应
            return true;
        });

        // 点击显示合同信息
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            TaskInfo bean = taskInfoList.get(position);

            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_view, null, false);
            makeDialog(view);

            TextView contractNameView = view.findViewById(R.id.text_contract_name);
            TextView contractNoView = view.findViewById(R.id.text_contract_no);
            TextView remitteeView = view.findViewById(R.id.text_remittee);
            TextView startDateView = view.findViewById(R.id.text_date);
            TextView deadlineView = view.findViewById(R.id.text_deadline);
            TextView totalAmountView = view.findViewById(R.id.text__total_amount);
            totalAmountView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            TextView payAmountView = view.findViewById(R.id.text_pay_amount);
            payAmountView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            TextView payTimesView = view.findViewById(R.id.text_pay_times);
            payTimesView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            TextView departmentView = view.findViewById(R.id.text_department);
            TextView operatorView = view.findViewById(R.id.text_operator);
            TextView commentView = view.findViewById(R.id.text_comment);

            contractNameView.setText(bean.getContractName());
            contractNoView.setText(bean.getContractNo());
            remitteeView.setText(bean.getRemittee());
            startDateView.setText(format.format(bean.getDate()));
            deadlineView.setText(format.format(bean.getDeadline()));
            DecimalFormat floatFormat = new DecimalFormat();// 设置显示的格式
            floatFormat.setMaximumFractionDigits(2);
            totalAmountView.setText("" + floatFormat.format(bean.getTotalAmount()));
            payAmountView.setText("" + floatFormat.format(bean.getPayAmount()));
            payTimesView.setText("" + bean.getPayTimes());
            departmentView.setText(bean.getDepartment());
            operatorView.setText(bean.getOperator());
            commentView.setText(bean.getComment());
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_edit, null, false);
            Dialog dialog = makeDialog(view);
            // 点击视图外不会触发返回
            dialog.setCanceledOnTouchOutside(false);
            // TODO: 魔幻现实主义代码, 深究一下应该是根据组件ID去定位不是根据内存地址
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

    private Bundle getBundle(TaskInfo bean) {
        Bundle bundle = new Bundle();

        Date fromdate = bean.getDate();
        Date deadline = bean.getDeadline();
        String fromdateStr = format.format(fromdate);
        String deadlineStr = format.format(deadline);
        String year = deadlineStr.substring(0, 4);
        String month = deadlineStr.substring(5, 7);
        String day = deadlineStr.substring(8, 10);

        // 放置数据
        bundle.putInt("id", bean.getId());
        bundle.putString("contract_name", bean.getContractName());
        bundle.putString("contract_no", bean.getContractNo());
        bundle.putString("remittee", bean.getRemittee());
        bundle.putFloat("total_amount", bean.getTotalAmount());
        bundle.putFloat("pay_amount", bean.getPayAmount());
        bundle.putInt("pay_times", bean.getPayTimes());
        bundle.putString("department", bean.getDepartment());
        bundle.putString("operator", bean.getOperator());
        bundle.putString("comment", bean.getComment());

        bundle.putString("year", year);
        bundle.putString("month", month);
        bundle.putString("day", day);
        bundle.putString("fromDateStr", fromdateStr);

        return bundle;
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
            edtContractName.setEnabled(false);
            edtContractNo.setText(bundle.getString("contract_no"));
            edtContractNo.setEnabled(false);
            edtRemittee.setText(bundle.getString("remittee"));
            edtRemittee.setEnabled(false);
            DecimalFormat floatFormat = new DecimalFormat();// 设置显示的格式
            floatFormat.setMaximumFractionDigits(2);
            edtTotalAmount.setText("" + floatFormat.format(bundle.getFloat("total_amount")));
            edtTotalAmount.setEnabled(false);
            edtPayAmount.setText("" + floatFormat.format(bundle.getFloat("pay_amount")));
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
                        try { //TODO:时间不能早于当前
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
                Float totalAmount = Float.parseFloat(edtTotalAmount.getText().toString().replace(",", ""));// 显示出来的数字会有逗号
                Log.d(TAG, "createOrUpdate: totalAmount  " + totalAmount);
                Float payAmount = Float.parseFloat(edtPayAmount.getText().toString().equals("") ? "0" : edtPayAmount.getText().toString().replace(",", ""));// 已付金额不是必选
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
                    int status = (payAmount == 0) ? 0 : (payAmount.equals(totalAmount) ? 2 : 1);
                    TaskInfo taskInfoBean;
                    String reminderStr;

                    if (isUpdate) {
                        int id = bundle.getInt("id");
                        String dateStr = bundle.getString("fromDateStr");
                        Date fromDate = format.parse(dateStr);
                        taskInfoBean = new TaskInfo(id, contractNo, contractName, totalAmount, payAmount, 0, status, remittee, department, operator, fromDate, pickedDate, comment);
                        // 更新数据库
                        taskInfoDao.update(taskInfoBean);
                        // 编辑日历事件
                        editCalendarEvent(taskInfoBean);

                        reminderStr = "更新成功";
                    } else {
                        taskInfoBean = new TaskInfo(contractNo, contractName, totalAmount, payAmount, 0, status, remittee, department, operator, new Date(), pickedDate, comment);
                        // 插入数据库
                        taskInfoDao.insert(taskInfoBean);  // TODO:相同的合同编号插入
                        // 编辑日历事件
                        editCalendarEvent(taskInfoBean);

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

    private void editCalendarEvent(TaskInfo taskInfoBean) {
        String title = "合同" + taskInfoBean.getContractNo() + "截止日";
        String description = taskInfoBean.toString();
        long deadline = taskInfoBean.getDeadline().getTime();
        int status = taskInfoBean.getStatus();

        if (status == 0 || status == 1) {
            // 添加/修改
            CalendarReminder.addCalendarEvent(MainActivity.this, title, description, deadline, 7);
        } else if (status == 2) {
            // 删除
            CalendarReminder.deleteCalendarEvent(MainActivity.this, title);
        }
    }

}
