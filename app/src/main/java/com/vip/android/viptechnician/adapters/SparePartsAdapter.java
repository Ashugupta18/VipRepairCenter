package com.vip.android.viptechnician.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.vip.android.viptechnician.R;
import com.vip.android.viptechnician.TicketDetails;
import com.vip.android.viptechnician.beans.SpareParts;

import java.util.List;

public class SparePartsAdapter extends RecyclerView.Adapter<SparePartsAdapter.MyViewHolder> {

    Context context;
    List<SpareParts> sparePartsList;
    TicketDetails ticketDetails;

    public SparePartsAdapter(TicketDetails ticketDetails,
                             Context context, List<SpareParts> sparePartsList)
    {
        this.context=context;
        this.sparePartsList=sparePartsList;
        this.ticketDetails=ticketDetails;
    }


    @Override
    public SparePartsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spares_single_row, parent, false);

        return new SparePartsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SparePartsAdapter.MyViewHolder holder, int position)
    {

        holder.item.setText(sparePartsList.get(position).getText());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketDetails.getSelectedSpare(sparePartsList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sparePartsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView item;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            item= itemView.findViewById(R.id.item);
        }
    }

}
