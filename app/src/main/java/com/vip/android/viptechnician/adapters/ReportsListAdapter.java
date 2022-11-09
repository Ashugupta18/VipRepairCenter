package com.vip.android.viptechnician.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vip.android.viptechnician.R;
import com.vip.android.viptechnician.beans.ReportsBean;
import com.vip.android.viptechnician.util.AutofitTextView;

import java.util.ArrayList;

public class ReportsListAdapter extends BaseAdapter
{
    Context context;
    ArrayList<ReportsBean> reportsBeanArrayList;
    LayoutInflater inflater;

    public ReportsListAdapter(Context context, ArrayList<ReportsBean> reportsBeanArrayList)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.reportsBeanArrayList = reportsBeanArrayList;

    }

    @Override
    public int getCount() {
        return reportsBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return reportsBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v=convertView;
        final ViewHolder holder;

        if(convertView==null)
        {
            v=inflater.inflate(R.layout.reports_single_row,parent, false);

            holder=new ViewHolder();

            holder.ticket_no=(AutofitTextView) v.findViewById(R.id.ticket_no);
            holder.cp_name=(AutofitTextView) v.findViewById(R.id.cp_name);
            holder.issue=(AutofitTextView) v.findViewById(R.id.issue);
            holder.payment=(AutofitTextView) v.findViewById(R.id.payment);
            holder.amount=(AutofitTextView) v.findViewById(R.id.amount);
            holder.ticket_status=(AutofitTextView) v.findViewById(R.id.ticket_status);

            v.setTag(holder);

        }
        else
            holder=(ViewHolder) v.getTag();

        holder.ticket_no.setText(reportsBeanArrayList.get(position).getTicket_no());
        holder.cp_name.setText(reportsBeanArrayList.get(position).getCp_name());
        holder.issue.setText(reportsBeanArrayList.get(position).getIssue());
        holder.payment.setText(reportsBeanArrayList.get(position).getPayment());
        holder.amount.setText(reportsBeanArrayList.get(position).getAmount());
        holder.ticket_status.setText(reportsBeanArrayList.get(position).getTicket_status());

        return v;
    }


    static class ViewHolder
    {
        AutofitTextView ticket_no, cp_name, issue, payment, amount, ticket_status;

    }
}