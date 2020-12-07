package com.chrastly.myfinance;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>{

    private ArrayList<CategoryCardView> categoryCardViewArrayList;
    private OnCategoryListener onCategoryListener;
    private int selectedPosition = -1;

    public CategoryRecyclerViewAdapter(ArrayList<CategoryCardView> categoryCardViewArrayList, OnCategoryListener onCategoryListener){
        this.categoryCardViewArrayList = categoryCardViewArrayList;
        this.onCategoryListener = onCategoryListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_category, parent, false);
        ViewHolder holder = new ViewHolder(view, onCategoryListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.categoryImageView.setImageResource(categoryCardViewArrayList.get(position).getCategoryIcon());
        holder.categoryTextField.setText(categoryCardViewArrayList.get(position).getCategoryText());

        if(selectedPosition == position){
            holder.parentLayout.setCardBackgroundColor(Color.parseColor("#ECEFF1"));
            holder.categoryTextField.setTextColor(Color.parseColor("#64B5F6"));
            ImageViewCompat.setImageTintList(holder.categoryImageView, ColorStateList.valueOf(Color.parseColor("#64B5F6")));
        }else{
            holder.parentLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.categoryTextField.setTextColor(Color.parseColor("#000000"));
            ImageViewCompat.setImageTintList(holder.categoryImageView, ColorStateList.valueOf(Color.parseColor("#000000")));
        }

    }


    @Override
    public int getItemCount() {
        return categoryCardViewArrayList.size();
    }

    public void setCategoryCardViewArrayList(ArrayList<CategoryCardView> categoryCardViewArrayList) {
        this.categoryCardViewArrayList = categoryCardViewArrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView categoryTextField;
        private ImageView categoryImageView;
        private CardView parentLayout;
        OnCategoryListener onCategoryListener;

        public ViewHolder(@NonNull View itemView, OnCategoryListener onCategoryListener) {
            super(itemView);
            categoryImageView = itemView.findViewById(R.id.categoryIcon);
            categoryTextField = itemView.findViewById(R.id.categoryText);
            parentLayout = itemView.findViewById(R.id.cardViewLayout);

            this.onCategoryListener = onCategoryListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onCategoryListener.OnCategoryClick(getAdapterPosition());
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();

        }
    }

    public interface OnCategoryListener{
        void OnCategoryClick(int position);
    }

}
