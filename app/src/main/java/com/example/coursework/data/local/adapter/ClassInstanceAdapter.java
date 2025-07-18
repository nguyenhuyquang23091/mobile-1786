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
import com.example.coursework.databinding.ListItemInstanceBinding;

import java.util.ArrayList;
import java.util.List;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.ClassInstanceViewHolder> {

    private List<ClassInstance> classInstances = new ArrayList<>();
    private OnItemClickListener listener;

    private boolean showButtons;

    public interface OnItemClickListener {
        void onDeleteClick(ClassInstance classInstance);

        void onEditCLick(ClassInstance classInstance);

        void onItemClick(ClassInstance classInstance);

    }

    public ClassInstanceAdapter(OnItemClickListener listener, boolean showButtons) {
        this.listener = listener;
        this.showButtons = showButtons;
    }

    @NonNull
    @Override
    public ClassInstanceAdapter.ClassInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemInstanceBinding binding = ListItemInstanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ClassInstanceAdapter.ClassInstanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassInstanceAdapter.ClassInstanceViewHolder holder, int position) {
        ClassInstance currentInstance = classInstances.get(position);
        holder.bind(currentInstance, listener, showButtons);
    }

    @Override
    public int getItemCount() {
        return classInstances.size();
    }

    public void setClasses(List<ClassInstance> classes) {
        this.classInstances = classes;
        notifyDataSetChanged();
    }

    static class ClassInstanceViewHolder extends RecyclerView.ViewHolder {
        private final ListItemInstanceBinding binding;

        public ClassInstanceViewHolder(@NonNull ListItemInstanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(ClassInstance classInstance, OnItemClickListener listener, boolean showButtons) {
            binding.instanceDate.setText(classInstance.date);
            String teacher_text = "Taught by" + classInstance.teacher;
            binding.instanceTeacher.setText(teacher_text);
            if (showButtons) {
                binding.actionsLayout.setVisibility(View.VISIBLE);
                binding.viewButton.setVisibility(View.GONE);
                binding.deleteButton.setOnClickListener(v -> {
                    if(listener != null ){
                        listener.onDeleteClick(classInstance);
                    }
                });
                binding.editButton.setOnClickListener(v -> {
                    if(listener != null ){
                        listener.onEditCLick(classInstance);
                    }
                });
            } else {
                binding.actionsLayout.setVisibility(View.GONE);
                binding.viewButton.setVisibility(View.VISIBLE);
                binding.getRoot().setOnClickListener(v -> {
                    if(listener != null ){
                        listener.onItemClick(classInstance);
                    }
                });
            }

        }
    }
}
