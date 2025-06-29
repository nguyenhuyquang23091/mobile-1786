package com.example.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.data.local.entities.YogaClass;

import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.YogaClassViewHolder> {

    private List<YogaClass> yogaClasses;
    private OnDeleteClickListener onDeleteClickListener;

    public interface  OnDeleteClickListener {
        void onDeleteClick(YogaClass yogaClass);

    }
    public YogaClassAdapter(OnDeleteClickListener onDeleteClickListener){
        this.onDeleteClickListener = onDeleteClickListener;
    }


    @NonNull
    @Override
    public YogaClassAdapter.YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_yoga_class, parent, false);
        return new YogaClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassAdapter.YogaClassViewHolder holder, int position) {
        YogaClass currentClass = yogaClasses.get(position);
        holder.itemClassType.setText(currentClass.type);
        String dayTime = currentClass.day + " at " + currentClass.time;
        holder.itemDayTime.setText(dayTime);

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(currentClass);
            }
        });

    }

    @Override
    public int getItemCount() {
        return yogaClasses.size();
    }

    public void setClasses(List<YogaClass> classes) {
        this.yogaClasses = classes;
        notifyDataSetChanged();
    }

    static class YogaClassViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemClassType;
        private final TextView itemDayTime;
        private final ImageButton deleteButton;

        public YogaClassViewHolder(@NonNull View itemView) {
            super(itemView);
            itemClassType = itemView.findViewById(R.id.itemClassType);
            itemDayTime = itemView.findViewById(R.id.itemDayTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }



}

