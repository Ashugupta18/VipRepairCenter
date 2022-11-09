package com.vip.android.viptechnician.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vip.android.viptechnician.R;
import com.vip.android.viptechnician.TicketDetails;
import com.vip.android.viptechnician.beans.TicketBean;
import com.vip.android.viptechnician.util.MonserattTextViewRegular;
import com.vip.android.viptechnician.util.MonserattTextview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhiraj on 12/23/2017.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {

    Context context;
    List<TicketBean> ticketBeanList;
    String categoryId;

    public TicketAdapter(Context context, ArrayList<TicketBean> ticketBeanList, String categoryId)
    {
        this.context=context;
        this.ticketBeanList=ticketBeanList;
        this.categoryId= categoryId;
    }


    @Override
    public TicketAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_list_single_row, parent, false);

        return new TicketAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TicketAdapter.MyViewHolder holder, int position) {

        final TicketBean ticketBean= ticketBeanList.get(position);
        holder.textTicketName.setText(ticketBean.getName());
        holder.textTicketAddress.setText(ticketBean.getAddress());
        holder.textTicketPhone.setText(ticketBean.getPhone());
        holder.textTicketNo.setText(ticketBean.getTicketNo());

        holder.textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(ticketBean.getCategory_Type().equals("Sold")){
                    Intent intent = new Intent(context, TicketDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ticketBean", ticketBean);
                    intent.putExtra("categoryid","2");
                    intent.putExtra("verifiedbarcode",ticketBean.getVerified_barcode());
                    context.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, TicketDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ticketBean", ticketBean);
                    intent.putExtra("categoryid","1");
                    intent.putExtra("verifiedbarcode",ticketBean.getVerified_barcode());
                    context.startActivity(intent);
                }
//                Intent intent = new Intent(context, TicketDetails.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("ticketBean", ticketBean);
//                intent.putExtra("categoryid",categoryId);
//                intent.putExtra("verifiedbarcode",ticketBean.getVerified_barcode());
//                //productEdit.putExtra("productbean", registeredProducsBeanList.get(getAdapterPosition()));
//
//               context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        MonserattTextview textTicketName,textTicketPhone,textTicketAddress;
        MonserattTextViewRegular textButton,textTicketNo;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            textTicketName= (MonserattTextview) itemView.findViewById(R.id.text_ticket_name_value);
            textTicketPhone= (MonserattTextview) itemView.findViewById(R.id.text_ticket_phone_value);
            textTicketAddress= (MonserattTextview) itemView.findViewById(R.id.text_ticket_address_value);
            textButton= (MonserattTextViewRegular) itemView.findViewById(R.id.button_view);
            textTicketNo= (MonserattTextViewRegular) itemView.findViewById(R.id.text_ticket_number);

        }

    }

}
