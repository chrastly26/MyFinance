package com.chrastly.myfinance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        OnExpenseListener onExpenseListener;

        public ViewHolder(@NonNull View itemView, OnExpenseListener onExpenseListener) {
            super(itemView);
            expenseTitleTextField = itemView.findViewById(R.id.expenseTitleRecyclerView);
            expenseAmountTextField = itemView.findViewById(R.id.expenseAmountRecyclerView);
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
