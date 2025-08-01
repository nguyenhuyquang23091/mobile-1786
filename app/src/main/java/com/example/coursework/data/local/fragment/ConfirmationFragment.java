package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coursework.R;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaRepository;
import com.example.coursework.data.local.util.SyncYogaCourseListener;
import com.example.coursework.databinding.FragmentConfirmationBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ConfirmationFragment extends Fragment {

    private FragmentConfirmationBinding binding;
    private YogaRepository yogaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConfirmationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        yogaRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        getData();
        setupClickListeners();
    }

    private void getData() {
        String day = ConfirmationFragmentArgs.fromBundle(getArguments()).getDay();
        String time = ConfirmationFragmentArgs.fromBundle(getArguments()).getTime();
        String intensity=  ConfirmationFragmentArgs.fromBundle(getArguments()).getIntensity();
        int capacity = Integer.parseInt(String.valueOf(ConfirmationFragmentArgs.fromBundle(getArguments()).getCapacity()));
        int duration = Integer.parseInt(String.valueOf(ConfirmationFragmentArgs.fromBundle(getArguments()).getDuration()));
        double price = Double.parseDouble(ConfirmationFragmentArgs.fromBundle(getArguments()).getPrice());;
        String type = ConfirmationFragmentArgs.fromBundle(getArguments()).getType();
        String description = ConfirmationFragmentArgs.fromBundle(getArguments()).getDescription();
        if (getArguments() != null ){
            binding.valDay.setText(day);
            binding.valType.setText(type);
            binding.valTime.setText(time);
            binding.valIntensity.setText(intensity);
            binding.valCapacity.setText(capacity + " people");
            binding.valDuration.setText(duration + " min");
            binding.valPrice.setText("£" + String.format("%.2f", price));
            if (description != null && !description.isEmpty()) {
                binding.valDescription.setText(description);
            } else {
                binding.valDescription.setText(R.string.no_description_provided);
            }

        }

    }

    private void setupClickListeners() {
        binding.confirmButton.setOnClickListener(v -> {
            saveAndFinish();

        });

        binding.moreActionsButton.setOnClickListener(v -> navigateToCreateCourse(true));
    }

    private void saveAndFinish() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.confirmButton.setEnabled(false);
        binding.moreActionsButton.setEnabled(false);

        YogaCourse yogaCourse = new YogaCourse();
        String day = ConfirmationFragmentArgs.fromBundle(getArguments()).getDay();
        String time = ConfirmationFragmentArgs.fromBundle(getArguments()).getTime();
        String intensity=  ConfirmationFragmentArgs.fromBundle(getArguments()).getIntensity();
        String capacity = String.valueOf(ConfirmationFragmentArgs.fromBundle(getArguments()).getCapacity());
        String duration = String.valueOf(ConfirmationFragmentArgs.fromBundle(getArguments()).getDuration());
        String price = ConfirmationFragmentArgs.fromBundle(getArguments()).getPrice();;
        String type = ConfirmationFragmentArgs.fromBundle(getArguments()).getType();
        String description = ConfirmationFragmentArgs.fromBundle(getArguments()).getDescription();

        yogaCourse.setType(type);
        yogaCourse.setDay(day);
        yogaCourse.setTime(time);
        yogaCourse.setIntensity(intensity);
        yogaCourse.setCapacity(Integer.valueOf(capacity));
        yogaCourse.setDuration(Integer.valueOf(duration));
        yogaCourse.setPrice(Double.valueOf(price));
        yogaCourse.setDescription(description);

        // Use the repository to insertYogaCourse the class.
        yogaRepository.insertYogaCourse(yogaCourse, new SyncYogaCourseListener() {
            @Override
            public void syncFailure(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    binding.getRoot().postDelayed(() -> {
                        binding.loadingIndicator.setVisibility(View.GONE);
                        binding.confirmButton.setEnabled(true);
                        binding.moreActionsButton.setEnabled(true);
                        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                        navigateToCreateCourse(false);
                    }, 2000);
                });
            }
            @Override
            public void syncFirebaseWithLocal() {
                requireActivity().runOnUiThread(() -> {
                    binding.getRoot().postDelayed(() -> {
                        binding.loadingIndicator.setVisibility(View.GONE);
                        binding.confirmButton.setEnabled(true);
                        binding.moreActionsButton.setEnabled(true);
                        Snackbar.make(binding.getRoot(), "Class saved successfully!", Snackbar.LENGTH_LONG).show();
                        navigateToCreateCourse(false);
                    }, 2000);
                });
            }

            @Override
            public void syncFirebaseWithLocal(List<YogaCourse> courses) {

            }
        });
    }

    private void navigateToCreateCourse(boolean passData) {
        if (passData) {
            // Extract data from arguments for editing
            String day = ConfirmationFragmentArgs.fromBundle(getArguments()).getDay();
            String time = ConfirmationFragmentArgs.fromBundle(getArguments()).getTime();
            String intensity = ConfirmationFragmentArgs.fromBundle(getArguments()).getIntensity();
            int capacity = ConfirmationFragmentArgs.fromBundle(getArguments()).getCapacity();
            int duration = ConfirmationFragmentArgs.fromBundle(getArguments()).getDuration();
            String price = ConfirmationFragmentArgs.fromBundle(getArguments()).getPrice();
            String type = ConfirmationFragmentArgs.fromBundle(getArguments()).getType();
            String description = ConfirmationFragmentArgs.fromBundle(getArguments()).getDescription();

            // Pass the data as arguments
            Bundle bundle = new Bundle();
            bundle.putString("prefilled_day", day);
            bundle.putString("prefilled_time", time);
            bundle.putString("prefilled_intensity", intensity);
            bundle.putInt("prefilled_capacity", capacity);
            bundle.putInt("prefilled_duration", duration);
            bundle.putString("prefilled_price", price);
            bundle.putString("prefilled_type", type);
            bundle.putString("prefilled_description", description);
            
            Navigation.findNavController(requireView()).navigate(R.id.action_confirmationFragment_to_createCourseFragment, bundle);
        } else {
            // Navigate without data (after save/sync operations)
            Navigation.findNavController(requireView()).navigate(R.id.action_confirmationFragment_to_createCourseFragment);
        }
    }

}
