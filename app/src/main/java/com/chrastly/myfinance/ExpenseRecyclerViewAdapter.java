package com.chrastly.myfinance;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseRecyclerViewAdapter extends RecyclerView.Adapter<ExpenseRecyclerViewAdapter.ViewHolder>{

    private ArrayList<Expense> expenseArrayList = new ArrayList<>();
    private OnExpenseListener onExpenseListener;
    private Boolean expenseDetailShow;

    public ExpenseRecyclerViewAdapter(ArrayList<Expense> expenseArrayList, OnExpenseListener onExpenseListener){
        this.expenseArrayList = expenseArrayList;
        this.onExpenseListener = onExpenseListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_view, parent, false);
        ViewHolder holder = new ViewHolder(view, onExpenseListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.expenseTitleTextField.setText(expenseArrayList.get(position).getExpenseTitle());
        holder.expenseAmountTextField.setText(expenseArrayList.get(position).getExpenseAmount());

        if (expenseArrayList.get(position).getExpenseCategory().equals("FOOD/DINING")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_meal);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("GROCERIES")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_grocery);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("ENTERTAINMENT")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_entertainment);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("SHOPPING")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_shopping);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("SPORT/GYM")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_sport);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("TRANSPORT")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_transport);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("RENTAL")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_rental);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("BILLS")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_utility);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("GIFTS")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_gift);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("TRAVEL")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_travel);

        } else if (expenseArrayList.get(position).getExpenseCategory().equals("TAXES")) {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_tax);

        }else {
            holder.expenseCategoryImageView.setImageResource(R.drawable.ic_nocategory);
        }


        if(position % 2 ==1){
            holder.parentLayout.setBackgroundColor(Color.parseColor("#ECEFF1"));
        }else{
            holder.parentLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

    }


    @Override
    public int getItemCount() {
        return expenseArrayList.size();
    }

    public void setExpenseArrayList(ArrayList<Expense> expenseArrayList) {
        this.expenseArrayList = expenseArrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView expenseTitleTextField, expenseAmountTextField, expenseDetailTextField;
        private RelativeLayout parentLayout;
        private ImageView expenseCategoryImageView;
        OnExpenseListener onExpenseListener;

        public ViewHolder(@NonNull View itemView, OnExpenseListener onExpenseListener) {
            super(itemView);
            expenseTitleTextField = itemView.findViewById(R.id.expenseTitleRecyclerView);
            expenseAmountTextField = itemView.findViewById(R.id.expenseAmountRecyclerView);
            expenseCategoryImageView = itemView.findViewById(R.id.expenseCategoryImageView);

            parentLayout = itemView.findViewById(R.id.expenseRecyclerViewLayout);

            this.onExpenseListener = onExpenseListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onExpenseListener.OnExpenseClick(getAdapterPosition());

        }
    }

    public interface OnExpenseListener{
        void OnExpenseClick(int position);
    }


}
