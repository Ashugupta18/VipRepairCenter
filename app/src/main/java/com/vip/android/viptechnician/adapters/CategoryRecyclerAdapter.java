package com.vip.android.viptechnician.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vip.android.viptechnician.CPListActivity;
import com.vip.android.viptechnician.R;
import com.vip.android.viptechnician.beans.CategoryBean;
import com.vip.android.viptechnician.util.MonserattTextViewRegular;

import java.util.List;

/**
 * Created by Android on 12/21/2017.
 */

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.MyViewHolder>
{
    LayoutInflater inflater;
    Context context;
    List<CategoryBean> categoryBeanList;
   // RegisteredProductsActivity registeredProductsActivity;


    public CategoryRecyclerAdapter(Context context,
                                   List<CategoryBean> categoryBeanList)
    {
        this.context=context;
       // this.registeredProductsActivity=registeredProductsActivity;
        this.categoryBeanList=categoryBeanList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View v = inflater.inflate(R.layout.category_single_row, parent, false);

        MyViewHolder holder =new MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {

        holder.category_name.setText(categoryBeanList.get(position).getCategory_name());

        holder.category_name.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent productEdit = new Intent(context, CPListActivity.class);
                productEdit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                productEdit.putExtra("categoryid",categoryBeanList.get(position).getCategory_id());
                //productEdit.putExtra("productbean", registeredProducsBeanList.get(getAdapterPosition()));
                context.startActivity(productEdit);
            }

        });

    }

    @Override
    public int getItemCount() {
        return categoryBeanList.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder
    {

        MonserattTextViewRegular category_name;

        //RelativeLayout prod_list_single_row_root;


        public MyViewHolder(View itemView)
        {
            super(itemView);


            category_name=(MonserattTextViewRegular) itemView.findViewById(R.id.category_name);

           // prod_list_single_row_root= (RelativeLayout) itemView.findViewById(R.id.prod_list_single_row_root);





        }


    }
}
