package com.example.coursework.data.local.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.data.local.entities.YogaCourse;

import java.util.ArrayList;
import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.YogaClassViewHolder> {

    private List<YogaCourse> yogaCourses = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(YogaCourse yogaCourse);
        void onItemClick(YogaCourse yogaCourse);

    }
    public YogaClassAdapter(OnItemClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public YogaClassAdapter.YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_yoga_class, parent, false);
        return new YogaClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassViewHolder holder, int position) {
        YogaCourse currentClass = yogaCourses.get(position);
        holder.bind(currentClass, listener);

    }

    @Override
    public int getItemCount() {
        return yogaCourses.size();
    }

    public void setClasses(List<YogaCourse> classes) {
        this.yogaCourses = classes;
        notifyDataSetChanged();
    }
    static class YogaClassViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemDayTime;
        private final TextView itemCapacity;

        private final TextView duration;

        private final TextView price;

        private final TextView itemClassType;

        private final TextView itemDescription;

        private final TextView itemIntensity;
        private final Button deleteButton;
        private final Button editButton;


        public YogaClassViewHolder(@NonNull View itemView) {
            super(itemView);
            itemClassType = itemView.findViewById(R.id.itemClassType);
            itemDayTime = itemView.findViewById(R.id.itemDayTime);
            itemCapacity = itemView.findViewById(R.id.itemCapacity);
            duration = itemView.findViewById(R.id.duration);
            price = itemView.findViewById(R.id.price);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemIntensity = itemView.findViewById(R.id.itemIntensity);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
        public void bind(final YogaCourse yogaCourse, final OnItemClickListener listener){
            itemClassType.setText((yogaCourse.type));
            String dayTime = yogaCourse.day + " at " + yogaCourse.time;
            itemDayTime.setText(dayTime);
            itemDescription.setText(yogaCourse.description);
            itemIntensity.setText(yogaCourse.intensity);
            itemCapacity.setText(yogaCourse.capacity + " people");
            duration.setText(yogaCourse.duration + "minutes");
            price.setText("£" + yogaCourse.price);
            deleteButton.setOnClickListener(v -> {
                if(listener != null){
                    listener.onDeleteClick(yogaCourse);
                }
            });
            itemView.setOnClickListener(v -> {
                if(listener != null){
                    listener.onItemClick(yogaCourse);
                }
            });
        }
    }



}

