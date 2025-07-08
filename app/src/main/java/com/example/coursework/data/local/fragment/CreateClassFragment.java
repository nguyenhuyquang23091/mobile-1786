package com.example.coursework.data.local.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.databinding.FragmentCreateClassBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateClassFragment extends Fragment {
    private FragmentCreateClassBinding binding;
    private YogaClassRepository yogaClassRepository;
    private YogaClass editYogaClass = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateClassBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yogaClassRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        setupAdapters();
        setupClickListeners();
        if(requireActivity().getIntent().hasExtra("uid")){
            int uid = requireActivity().getIntent().getIntExtra("uid", -1);
            if(uid != -1){
                loadEditClass(uid);
            }
        }

    }
    private void loadEditClass(int classId){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            editYogaClass = yogaClassRepository.findById(classId);
            requireActivity().runOnUiThread(()-> populateForm(editYogaClass));

        });
    }

    private void populateForm(YogaClass yogaClass){
        if (yogaClass == null ) return;
        binding.dayOfWeekInput.setText(yogaClass.day, false);
        binding.classTypeInput.setText(yogaClass.type, false);
        binding.timeInput.setText(yogaClass.time);
        binding.capacityInput.setText(String.valueOf(yogaClass.capacity));
        binding.durationInput.setText(String.valueOf(yogaClass.duration));
        binding.priceInput.setText(String.valueOf(yogaClass.price));
        binding.descriptionInput.setText(yogaClass.description);
    }

    private void setupAdapters() {
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(
                //cant use this as context
                requireContext(),
                R.array.days_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        binding.dayOfWeekInput.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.type_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        binding.classTypeInput.setAdapter(typeAdapter);
    }

    private void setupClickListeners() {
        // Listener to show a TimePickerDialog when the time field is clicked
        binding.timeInput.setOnClickListener(v -> showTimePickerDialog());
        binding.timeLayout.setEndIconOnClickListener(v -> showTimePickerDialog());

        // Listener for the main save button - This contains your original logic flow
        binding.saveCourseButton.setOnClickListener(v -> {
            if (validateForm()) {
                // Your original success toast
                Toast.makeText(getContext(), "Form submitted", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (editYogaClass != null) {
                        // UPDATE existing class
                        updateClass();
                        Toast.makeText(getContext(), "Class Updated!", Toast.LENGTH_SHORT).show();
                        // Go back to the list after updating
                        startActivity(new Intent(requireActivity(), ClassListFragment.class));
                        requireActivity().finish();
                    } else {
                        // INSERT new class
                        passDataToConfirmation(); // Your existing flow for new classes
                    }
                }, 2500); // 2500 ms = 2.5 seconds
            }
        });
    }
    private void updateClass() {
        editYogaClass.day = binding.dayOfWeekInput.getText().toString();
        editYogaClass.type = binding.classTypeInput.getText().toString();
        editYogaClass.time = Objects.requireNonNull(binding.timeInput.getText()).toString();
        editYogaClass.capacity = Integer.parseInt(Objects.requireNonNull(binding.capacityInput.getText()).toString());
        editYogaClass.duration = Integer.parseInt(Objects.requireNonNull(binding.durationInput.getText()).toString());
        editYogaClass.price = Double.parseDouble(Objects.requireNonNull(binding.priceInput.getText()).toString());
        editYogaClass.description = Objects.requireNonNull(binding.descriptionInput.getText()).toString();

        yogaClassRepository.update(editYogaClass);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select Time")
                .build();
        picker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
        // add on listener
        picker.addOnPositiveButtonClickListener(dialog -> {
            int selectedHour = picker.getHour();
            int selectedMinute = picker.getMinute();
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            binding.timeInput.setText(formattedTime);
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        // Clear previous errors before validating again
        clearAllErrors();

        // --- Your Validation Logic (Upgraded & Fixed) ---
        // Day input
        if (TextUtils.isEmpty(binding.dayOfWeekInput.getText())) {
            // FIX: Get the parent TextInputLayout to set the error
            ((TextInputLayout) binding.dayOfWeekInput.getParent().getParent()).setError("Please select a valid day");
            valid = false;
        }

        // Time input
        if (TextUtils.isEmpty(binding.timeInput.getText())) {
            binding.timeLayout.setError("Please select a time");
            valid = false;
        }

        // Capacity input
        if (TextUtils.isEmpty(binding.capacityInput.getText())) {
            binding.capacityLayout.setError("Please enter capacity");
            valid = false;
        }

        // Duration input
        if (TextUtils.isEmpty(binding.durationInput.getText())) {
            binding.durationLayout.setError("Please enter duration");
            valid = false;
        }

        // Price input
        if (TextUtils.isEmpty(binding.priceInput.getText())) {
            binding.priceLayout.setError("Please enter a price");
            valid = false;
        }

        // Type input
        if (TextUtils.isEmpty(binding.classTypeInput.getText())) {
            // FIX: Get the parent TextInputLayout to set the error
            ((TextInputLayout) binding.classTypeInput.getParent().getParent()).setError("Please select a valid type");
            valid = false;
        }

        return valid;
    }
    private void clearAllErrors() {
        // FIX: Get the parent TextInputLayout to clear the error
        ((TextInputLayout) binding.dayOfWeekInput.getParent().getParent()).setError(null);
        binding.timeLayout.setError(null);
        binding.capacityLayout.setError(null);
        binding.durationLayout.setError(null);
        binding.priceLayout.setError(null);
        // FIX: Get the parent TextInputLayout to clear the error
        ((TextInputLayout) binding.classTypeInput.getParent().getParent()).setError(null);
    }

    private void passDataToConfirmation() {
        Intent intent = new Intent(requireActivity(), ConfirmationActivity.class);
        //store intent information in object

        intent.putExtra("day", binding.dayOfWeekInput.getText().toString());
        intent.putExtra("time", Objects.requireNonNull(binding.timeInput.getText()).toString());
        intent.putExtra("capacity", Objects.requireNonNull(binding.capacityInput.getText()).toString());
        intent.putExtra("duration", Objects.requireNonNull(binding.durationInput.getText()).toString());
        intent.putExtra("price", Objects.requireNonNull(binding.priceInput.getText()).toString());
        intent.putExtra("type", binding.classTypeInput.getText().toString());
        intent.putExtra("description", Objects.requireNonNull(binding.descriptionInput.getText()).toString());

        // Find the selected chip's text from the ChipGroup
        int selectedChipId = binding.intensityChipGroup.getCheckedChipId();
        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = binding.getRoot().findViewById(selectedChipId);
            intent.putExtra("intensity", selectedChip.getText().toString());
        } else {
            intent.putExtra("intensity", "Not specified");
        }
        startActivity(intent);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
