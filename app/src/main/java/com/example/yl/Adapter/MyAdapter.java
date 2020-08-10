package com.example.yl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yl.Beans.TaskInfo;
import com.example.yl.R;
import com.xx.roundprogressbar.RoundProgressBar;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<TaskInfo> taskInfoList;

    public MyAdapter(Context context, List<TaskInfo> beanList) {
        this.context = context;
        this.taskInfoList = beanList;
    }

    @Override
    public int getCount() {
        return taskInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置progressBar
        float payAmount = taskInfoList.get(position).getPayAmount();
        float totalAmount = taskInfoList.get(position).getTotalAmount();
        float progress = payAmount / totalAmount * 100;
        viewHolder.progressBar.setCurrentProgress(progress > 100 ? 100 : progress);
        // 设置合同名称
        String title = taskInfoList.get(position).getContractName();
        viewHolder.title.setText(truncateString(title, 12));
        // 设置总金额
        viewHolder.amount.setText(totalAmount / 10000 + "万元");
        // 设置日期
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        viewHolder.startDate.setText(format.format(taskInfoList.get(position).getDate()));
        viewHolder.endDate.setText(format.format(taskInfoList.get(position).getDeadline()));

        return convertView;
    }

    private String truncateString(String content, int length) {
        int contentLength = content.getBytes().length / 3;

        if (contentLength <= length) {
            return content;
        } else {
            String result = content.substring(0, length - 1) + "..";
            return result;
        }
    }

    class ViewHolder {
        TextView title;
        TextView amount;
        TextView startDate;
        TextView endDate;
        RoundProgressBar progressBar;

        public ViewHolder(View view) {
            this.title = view.findViewById(R.id.title);
            this.amount = view.findViewById(R.id.amount);
            this.startDate = view.findViewById(R.id.startDate);
            this.endDate = view.findViewById(R.id.endDate);
            this.progressBar = view.findViewById(R.id.progressBar);
        }
    }
}
