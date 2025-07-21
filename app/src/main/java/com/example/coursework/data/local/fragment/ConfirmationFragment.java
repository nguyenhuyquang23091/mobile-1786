package com.example.coursework.data.local.fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.coursework.R;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.data.local.util.SyncFirebaseListener;
import com.example.coursework.databinding.FragmentConfirmationBinding;
import com.google.android.material.snackbar.Snackbar;


import java.util.Objects;

public class ConfirmationFragment extends Fragment {

    private FragmentConfirmationBinding binding;
    private YogaClassRepository yogaClassRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConfirmationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        yogaClassRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
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

        binding.moreActionsButton.setOnClickListener(this::showMoreActionsMenu);
    }

    private void showMoreActionsMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        // This inflates the menu we created earlier.
        popup.getMenuInflater().inflate(R.menu.confirmation_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                requireActivity().finish();
                return true;
            }
            return false;
        });

        popup.show();
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

        yogaCourse.type = type;
        yogaCourse.day = day;
        yogaCourse.time = time;
        yogaCourse.intensity = intensity;
        yogaCourse.capacity = Integer.valueOf(capacity);
        yogaCourse.duration = Integer.valueOf(duration);
        yogaCourse.price = Double.valueOf(price);
        yogaCourse.description = description;

        // Use the repository to insert the class.
        yogaClassRepository.insert(yogaCourse, new SyncFirebaseListener() {
            @Override
            public void syncFailure(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    binding.getRoot().postDelayed(() -> {
                        binding.loadingIndicator.setVisibility(View.GONE);
                        binding.confirmButton.setEnabled(true);
                        binding.moreActionsButton.setEnabled(true);
                        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                        navigateToMain();
                    }, 2000);
                });
            }
            @Override
            public void syncSuccess() {
                requireActivity().runOnUiThread(() -> {
                    binding.getRoot().postDelayed(() -> {
                        binding.loadingIndicator.setVisibility(View.GONE);
                        binding.confirmButton.setEnabled(true);
                        binding.moreActionsButton.setEnabled(true);
                        Snackbar.make(binding.getRoot(), "Class saved and synced successfully!", Snackbar.LENGTH_LONG).show();
                        navigateToMain();
                    }, 2000);
                });
            }
        });
    }

    private void navigateToMain(){
        Navigation.findNavController(requireView()).navigate(R.id.action_confirmationFragment_to_createClassFragment);

    }

}
