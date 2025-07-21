package com.example.coursework.data.local.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.databinding.ListItemYogaClassBinding;

import java.util.ArrayList;
import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.ClassInstanceViewHolder> {

    private List<YogaClass> yogaClasses = new ArrayList<>();
    private OnItemClickListener listener;

    private boolean showButtons;

    public interface OnItemClickListener {
        void onDeleteClick(YogaClass yogaClass);

        void onEditCLick(YogaClass yogaClass);

        void onItemClick(YogaClass yogaClass);

    }

    public YogaClassAdapter(OnItemClickListener listener, boolean showButtons) {
        this.listener = listener;
        this.showButtons = showButtons;
    }

    @NonNull
    @Override
    public YogaClassAdapter.ClassInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemYogaClassBinding binding = ListItemYogaClassBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new YogaClassAdapter.ClassInstanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassAdapter.ClassInstanceViewHolder holder, int position) {
        YogaClass currentInstance = yogaClasses.get(position);
        holder.bind(currentInstance, listener, showButtons);
    }

    @Override
    public int getItemCount() {
        return yogaClasses.size();
    }

    public void setClasses(List<YogaClass> classes) {
        this.yogaClasses = classes;
        notifyDataSetChanged();
    }

    static class ClassInstanceViewHolder extends RecyclerView.ViewHolder {
        private final ListItemYogaClassBinding binding;

        public ClassInstanceViewHolder(@NonNull ListItemYogaClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(YogaClass yogaClass, OnItemClickListener listener, boolean showButtons) {
            binding.courseType.setText(yogaClass.courseType);
            binding.instanceDate.setText(yogaClass.date);
            binding.instanceTeacher.setText( yogaClass.teacher);
            binding.classTitle.setText(yogaClass.title);
            binding.classSubtitle.setText(yogaClass.description);
            if (showButtons) {
                binding.actionsLayout.setVisibility(View.VISIBLE);
                binding.viewButton.setVisibility(View.GONE);
                binding.deleteButton.setOnClickListener(v -> {
                    if(listener != null ){
                        listener.onDeleteClick(yogaClass);
                    }
                });
                binding.editButton.setOnClickListener(v -> {
                    if(listener != null ){
                        listener.onEditCLick(yogaClass);
                    }
                });
            } else {
                binding.actionsLayout.setVisibility(View.GONE);
                binding.viewButton.setVisibility(View.VISIBLE);
                binding.getRoot().setOnClickListener(v -> {
                    if(listener != null ){
                        listener.onItemClick(yogaClass);
                    }
                });
            }

        }
    }
}
