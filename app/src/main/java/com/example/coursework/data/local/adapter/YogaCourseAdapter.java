package com.example.coursework.data.local.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.databinding.ListItemYogaCoursesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class YogaCourseAdapter extends RecyclerView.Adapter<YogaCourseAdapter.YogaClassViewHolder> {

    private List<YogaCourse> yogaCourses = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(YogaCourse yogaCourse);
        void onItemClick(YogaCourse yogaCourse, FragmentNavigator.Extras extras);

    }
    public YogaCourseAdapter(OnItemClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public YogaCourseAdapter.YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemYogaCoursesBinding binding = ListItemYogaCoursesBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new YogaClassViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassViewHolder holder, int position) {
        YogaCourse currentClass = yogaCourses.get(position);
        String transitionname = "yoga_class_" + currentClass.uid;
        holder.bind(currentClass, listener, transitionname);

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
        private final ListItemYogaCoursesBinding binding;

        public YogaClassViewHolder(@NonNull ListItemYogaCoursesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final YogaCourse yogaCourse, final OnItemClickListener listener, final String transitionname){
            binding.mainTitle.setText(yogaCourse.type);
            String dayTime = yogaCourse.day + " at " + yogaCourse.time;
            binding.itemDayTime.setText(dayTime);
            binding.descriptionValue.setText(yogaCourse.description);
            binding.intensityClassType.setText(yogaCourse.intensity);
            binding.itemCapacity.setText(String.valueOf(yogaCourse.capacity));
            binding.duration.setText(String.format("%d min", yogaCourse.duration));
            binding.priceValue.setText("£" + yogaCourse.price);
            // *** NEW COLOR LOGIC ***
            Context context = binding.getRoot().getContext();
            int colorDarkRes, colorLightRes;

            if (Objects.equals(yogaCourse.type, "Flow Yoga")) {
                colorDarkRes = R.color.yoga_green_dark;
                colorLightRes = R.color.yoga_green_light;
            } else if (Objects.equals(yogaCourse.type, "Aerial Yoga")) {
                colorDarkRes = R.color.yoga_blue_dark;
                colorLightRes = R.color.yoga_blue_light;
            } else if (Objects.equals(yogaCourse.type, "Family Yoga")) {
                colorDarkRes = R.color.yoga_pink_dark;
                colorLightRes = R.color.yoga_pink_light;
            } else {
                colorDarkRes = R.color.yoga_default_dark;
                colorLightRes = R.color.yoga_default_dark;
            }

            int colorDark = ContextCompat.getColor(context, colorDarkRes);
            int colorLight = ContextCompat.getColor(context, colorLightRes);

            // Apply colors
            binding.myCard.setStrokeColor(colorDark);
            binding.classTypeContainer.setCardBackgroundColor(colorDark);
            binding.dayContainer.setCardBackgroundColor(colorLight);
            binding.detailsContainer.setCardBackgroundColor(colorLight);

            // Also color the text and icons inside the light containers
            for (int i = 0; i < binding.dayContainer.getChildCount(); i++) {
                if (binding.dayContainer.getChildAt(i) instanceof LinearLayout) {
                    LinearLayout innerLayout = (LinearLayout) binding.dayContainer.getChildAt(i);
                    for (int j = 0; j < innerLayout.getChildCount(); j++) {
                        View child = innerLayout.getChildAt(j);
                        if (child instanceof TextView) ((TextView) child).setTextColor(colorDark);
                        if (child instanceof ImageView) ((ImageView) child).setImageTintList(ColorStateList.valueOf(colorDark));
                    }
                }
            }
            for (int i = 0; i < binding.detailsContainer.getChildCount(); i++) {
                if (binding.detailsContainer.getChildAt(i) instanceof LinearLayout) {
                    LinearLayout innerLayout = (LinearLayout) binding.detailsContainer.getChildAt(i);
                    for (int j = 0; j < innerLayout.getChildCount(); j++) {
                        View child = innerLayout.getChildAt(j);
                        if (child instanceof TextView) ((TextView) child).setTextColor(colorDark);
                        if (child instanceof ImageView) ((ImageView) child).setImageTintList(ColorStateList.valueOf(colorDark));
                    }
                }
            }
            binding.deleteButton.setOnClickListener(v -> {
                if(listener != null){
                    listener.onDeleteClick(yogaCourse);
                }
            });

            binding.getRoot().setTransitionName(transitionname);
            
            binding.getRoot().setOnClickListener(v -> {
                if(listener != null){
                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                            .addSharedElement(binding.getRoot(), transitionname).build();
                    listener.onItemClick(yogaCourse, extras);
                }
            });
        }
    }



}

