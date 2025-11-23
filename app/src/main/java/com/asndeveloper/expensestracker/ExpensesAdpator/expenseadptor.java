package com.asndeveloper.expensestracker.ExpensesAdpator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asndeveloper.expensestracker.Database.ExpensesEntity;
import com.asndeveloper.expensestracker.R;

import java.util.List;

public class expenseadptor extends RecyclerView.Adapter<expenseadptor.ViewHolder>{
   Context context;
    List<ExpensesEntity> list;

    public expenseadptor(Context context,List<ExpensesEntity> list) {
        this.context=context;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.recent_tr_recycler, parent, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull expenseadptor.ViewHolder holder, int position) {
        // entty ko bula rahe hai bhai
        ExpensesEntity entity=list.get(position);
        // show kar ge

        holder.titleshow.setText(entity.getTitle());
        holder.amountshow.setText("₹ " + entity.getAmount());
        holder.dateshow.setText(entity.getTime());
        holder.caty.setText(entity.getCategory());
    }
// Update kare Ge
    public void updateList(List<ExpensesEntity> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }
    // GET ITEM (For swipe delete)
    public ExpensesEntity getItem(int position) {
        return list.get(position);
    }


    // REMOVE ITEM (Delete from RecyclerView)

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleshow,amountshow,dateshow,caty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleshow=itemView.findViewById(R.id.txttitle);
            amountshow=itemView.findViewById(R.id.txtamount);
            dateshow=itemView.findViewById(R.id.txttime);
            caty=itemView.findViewById(R.id.txtcat);
        }
    }
}
