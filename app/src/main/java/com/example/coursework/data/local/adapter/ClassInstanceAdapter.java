package com.example.coursework.data.local.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.data.local.entities.ClassInstance;

import java.util.ArrayList;
import java.util.List;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.ClassInstanceViewHolder> {

    private List<ClassInstance> classInstances = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(ClassInstance classInstance);
        void onEditCLick(ClassInstance classInstance);

    }
    public ClassInstanceAdapter(OnItemClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public ClassInstanceAdapter.ClassInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_instance, parent, false);
        return new ClassInstanceAdapter.ClassInstanceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassInstanceAdapter.ClassInstanceViewHolder holder, int position) {
        ClassInstance currentInstance = classInstances.get(position);
        holder.bind(currentInstance, listener);
    }

    @Override
    public int getItemCount() {
        return classInstances.size();
    }
    public void setClasses(List<ClassInstance> classes) {
        this.classInstances = classes;
        notifyDataSetChanged();
    }

    static class ClassInstanceViewHolder extends RecyclerView.ViewHolder{

        private final TextView instance_date;
        private  final TextView instance_teacher;
        private final Button delete_button;
        private final Button edit_button;

        public ClassInstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            instance_date = itemView.findViewById(R.id.instance_date);
            instance_teacher = itemView.findViewById(R.id.instance_teacher);
            delete_button = itemView.findViewById(R.id.delete_button);
            edit_button = itemView.findViewById(R.id.edit_button);

        }
        public void bind(final ClassInstance classInstance, final OnItemClickListener listener){
            instance_date.setText(classInstance.date);
            String teacher_text = "Taught by" + classInstance.teacher;
            instance_teacher.setText(teacher_text);
            delete_button.setOnClickListener(v -> {
                if(listener != null){
                    listener.onDeleteClick(classInstance);
                }
                });
            edit_button.setOnClickListener(v -> {
                if(listener != null){
                    listener.onEditCLick(classInstance);
                }
            });
        }

    }

}
