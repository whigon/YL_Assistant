package com.example.yl;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.yl.Adapter.MyAdapter;
import com.example.yl.Beans.TaskInfo;
import com.example.yl.Dao.BaseDao;
import com.example.yl.Dao.TaskInfoImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<TaskInfo> taskInfoList = new ArrayList<>();
    private MyAdapter myAdapter;
    private GridView gridView;
    private Toolbar toolbar;

    private BaseDao taskInfoDao = new TaskInfoImpl(MainActivity.this);
//    private BaseDao paymentRecordDao = new PaymentRecordImpl(MainActivity.this);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "点击setting", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridView = findViewById(R.id.grid_view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_add, null, false);
                Dialog dialog = makeDialog(view);
            }
        });
    }

    private Dialog makeDialog(View view) {
        Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
        dialog.getWindow().setContentView(view);
        dialog.show();
        return dialog;
    }
}
