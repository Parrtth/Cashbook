package com.example.cashbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHoler> {
    private ArrayList<Transaction> model_transactions;
    private Context context;
    private ItemClickListner clickListner;
    public Adapter(Context context, ArrayList<Transaction> model_transactions){
        this.context=context;
        this.model_transactions=model_transactions;
    }

    public void setClickListner(ItemClickListner clickListner) {
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.list,parent,false);
        MyViewHoler viewHoler=new MyViewHoler(view);
        return viewHoler;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, int position) {
        Transaction model_transaction=model_transactions.get(position);
        holder.txt_remark.setText(model_transaction.getRemark());
        holder.txt_date.setText(model_transaction.getDate());
        if(model_transaction.getIncome()!=0){
            holder.txt_income_expense.setTextColor(Color.parseColor("#50C878"));
            holder.txt_income_expense.setText(String.valueOf(model_transaction.getIncome()));

        } else if (model_transaction.getExpense()!=0) {
            holder.txt_income_expense.setTextColor(Color.parseColor("#E41B17"));
            holder.txt_income_expense.setText(String.valueOf(model_transaction.getExpense()));
        }


        holder.txt_balance.setText(String.valueOf(model_transaction.getBalance()));

    }

    @Override
    public int getItemCount() {
        return model_transactions.size();
    }

    public class MyViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener{
        public  TextView txt_date,txt_remark,txt_income_expense,txt_balance;

        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            this.txt_date=itemView.findViewById(R.id.txt_date);
            this.txt_remark=itemView.findViewById(R.id.txt_date_remark);
            this.txt_income_expense=itemView.findViewById(R.id.txt_cr_dr);
            this.txt_balance=itemView.findViewById(R.id.txt_balance);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListner!=null){
                clickListner.onClick(v,getAdapterPosition());
            }
        }
    }
    /*
            updateList():-
                            using this method we can update the RecycleView data.
     */
    public void updateList(ArrayList<Transaction> transactions){
        model_transactions.clear();
        model_transactions.addAll(transactions);
        notifyDataSetChanged();
    }

}
