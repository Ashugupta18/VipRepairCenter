package com.vip.android.viptechnician.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vip.android.viptechnician.R;
import com.vip.android.viptechnician.TicketListActivity;
import com.vip.android.viptechnician.beans.CPBean;
import com.vip.android.viptechnician.util.MonserattTextview;

import java.util.List;

/**
 * Created by Android on 12/21/2017.
 */

public class CPRecyclerAdapter extends RecyclerView.Adapter<CPRecyclerAdapter.MyViewHolder>
{
    Context context;
    List<CPBean> cpBeanList;
  //  String categoryId;

    public CPRecyclerAdapter(Context context, List<CPBean> cpBeanList/*, String categoryId*/)
    {
        this.context=context;
        this.cpBeanList=cpBeanList;
       // this.categoryId= categoryId;
    }

    @Override
    public CPRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cp_list_single_row, parent, false);

        return new CPRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CPRecyclerAdapter.MyViewHolder holder, int position) {

        final CPBean cpBean= cpBeanList.get(position);
        holder.textCpId.setText(cpBean.getCp_id());
        holder.textCpName.setText(cpBean.getCp_name());
        holder.textCpPhone.setText(cpBean.getCp_phone());
        holder.textCpAddress.setText(cpBean.getCp_addr());
        holder.textTicketCount.setText(cpBean.getTicketCountOpen());
        holder.linearMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TicketListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("cpid",cpBean.getCp_id());
                //intent.putExtra("categoryid", categoryId);
                //productEdit.putExtra("productbean", registeredProducsBeanList.get(getAdapterPosition()));
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return cpBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        MonserattTextview textCpId,textCpName,textCpPhone,textCpAddress,textTicketCount;
        LinearLayout linearMain;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            textCpId= (MonserattTextview) itemView.findViewById(R.id.text_id_value);
            textCpName= (MonserattTextview) itemView.findViewById(R.id.text_name_value);
            textCpPhone= (MonserattTextview) itemView.findViewById(R.id.text_phone_value);
            textCpAddress= (MonserattTextview) itemView.findViewById(R.id.text_address_value);
            textTicketCount= (MonserattTextview) itemView.findViewById(R.id.text_count_ticket);
            linearMain= (LinearLayout) itemView.findViewById(R.id.layout_main_cp);

        }


    }

}
