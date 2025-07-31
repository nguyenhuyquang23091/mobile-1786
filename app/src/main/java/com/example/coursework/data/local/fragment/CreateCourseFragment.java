package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.coursework.databinding.FragmentCreateCourseBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coursework.R;
import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateCourseFragment extends Fragment {
    private FragmentCreateCourseBinding binding;
    private YogaRepository yogaRepository;
    private YogaCourse editYogaCourse = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateCourseBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yogaRepository = new YogaRepositoryImplementation(requireActivity().getApplication());
        setupAdapters();
        setupClickListeners();
        handlePrefilledData();
        // Check for edit mode from activity intent (existing functionality)
        if(requireActivity().getIntent().hasExtra("uid")){
            String uid = requireActivity().getIntent().getStringExtra("uid");
            if(uid != null && !uid.isEmpty()){
                loadEditClass(uid);
            }
        }
        // Check for edit mode from fragment arguments (new functionality)
        else if(getArguments() != null && getArguments().containsKey("edit_course_uid")){
            String uid = getArguments().getString("edit_course_uid");
            if(uid != null && !uid.isEmpty()){
                loadEditClass(uid);
            }
        }
    }
    private void loadEditClass(String classId){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            editYogaCourse = yogaRepository.findById(classId);
            requireActivity().runOnUiThread(()-> populateForm(editYogaCourse));

        });
    }
    private void populateForm(YogaCourse yogaCourse){
        if (yogaCourse == null ) return;
        binding.dayOfWeekInput.setText(yogaCourse.getDay(), false);
        binding.classTypeInput.setText(yogaCourse.getType(), false);
        binding.timeInput.setText(yogaCourse.getTime());
        binding.capacityInput.setText(String.valueOf(yogaCourse.getCapacity()));
        binding.durationInput.setText(String.valueOf(yogaCourse.getDuration()));
        binding.priceInput.setText(String.valueOf(yogaCourse.getPrice()));
        binding.descriptionInput.setText(yogaCourse.getDescription());
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
                Snackbar.make(requireView(), "Form submitted successfully", Snackbar.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (editYogaCourse != null) {
                        // UPDATE existing class
                        updateClass();
                        Snackbar.make(requireView(), "Class updated successfully!", Snackbar.LENGTH_SHORT).show();
                        // Go back to the list after updating
                        Navigation.findNavController(requireView()).navigateUp();
                    } else {
                        // INSERT new class
                        passDataToConfirmation(); // Your existing flow for new classes
                    }
                }, 2500); // 2500 ms = 2.5 seconds
            }
        });
    }


    private void handlePrefilledData() {
        Bundle args = getArguments();
        if (args != null) {
            // Pre-fill form with data from arguments
            if (args.containsKey("prefilled_type")) {
                binding.classTypeInput.setText(args.getString("prefilled_type"), false);
            }
            if (args.containsKey("prefilled_day")) {
                binding.dayOfWeekInput.setText(args.getString("prefilled_day"), false);
            }
            if (args.containsKey("prefilled_time")) {
                binding.timeInput.setText(args.getString("prefilled_time"));
            }
            if (args.containsKey("prefilled_capacity")) {
                binding.capacityInput.setText(String.valueOf(args.getInt("prefilled_capacity")));
            }
            if (args.containsKey("prefilled_duration")) {
                binding.durationInput.setText(String.valueOf(args.getInt("prefilled_duration")));
            }
            if (args.containsKey("prefilled_price")) {
                binding.priceInput.setText(args.getString("prefilled_price"));
            }
            if (args.containsKey("prefilled_description")) {
                binding.descriptionInput.setText(args.getString("prefilled_description"));
            }
            if (args.containsKey("prefilled_intensity")) {
                String intensity = args.getString("prefilled_intensity");
                // Find and check the corresponding chip based on intensity value
                if (intensity != null) {
                    for (int i = 0; i < binding.intensityChipGroup.getChildCount(); i++) {
                        Chip chip = (Chip) binding.intensityChipGroup.getChildAt(i);
                        if (chip.getText().toString().equals(intensity)) {
                            binding.intensityChipGroup.check(chip.getId());
                            break;
                        }
                    }
                }
            }
        }
    }

    private void updateClass() {
        editYogaCourse.setDay(binding.dayOfWeekInput.getText().toString());
        editYogaCourse.setType(binding.classTypeInput.getText().toString());
        editYogaCourse.setTime(Objects.requireNonNull(binding.timeInput.getText()).toString());
        editYogaCourse.setCapacity(Integer.parseInt(Objects.requireNonNull(binding.capacityInput.getText()).toString()));
        editYogaCourse.setDuration(Integer.parseInt(Objects.requireNonNull(binding.durationInput.getText()).toString()));
        editYogaCourse.setPrice(Double.parseDouble(Objects.requireNonNull(binding.priceInput.getText()).toString()));
        editYogaCourse.setDescription(Objects.requireNonNull(binding.descriptionInput.getText()).toString());
        yogaRepository.updateYogaCourse(editYogaCourse);
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


        if (TextUtils.isEmpty(binding.dayOfWeekInput.getText())) {
            // FIX: Get the parent TextInputLayout to set the error
            ((TextInputLayout) binding.dayOfWeekInput.getParent().getParent()).setError("Please select a valid day");
            valid = false;
        }

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
        ((TextInputLayout) binding.dayOfWeekInput.getParent().getParent()).setError(null);
        binding.timeLayout.setError(null);
        binding.capacityLayout.setError(null);
        binding.durationLayout.setError(null);
        binding.priceLayout.setError(null);
        ((TextInputLayout) binding.classTypeInput.getParent().getParent()).setError(null);
    }

    private void passDataToConfirmation() {

        String day = binding.dayOfWeekInput.getText().toString();
        String time = Objects.requireNonNull(binding.timeInput.getText()).toString();
        int capacity = Integer.parseInt(Objects.requireNonNull(binding.capacityInput.getText()).toString());
        int duration = Integer.parseInt(Objects.requireNonNull(binding.durationInput.getText()).toString());
        double price =  Integer.parseInt(Objects.requireNonNull(binding.priceInput.getText()).toString());
        String type = binding.classTypeInput.getText().toString();
        String description = Objects.requireNonNull(binding.descriptionInput.getText()).toString();
        int selectedChipId = binding.intensityChipGroup.getCheckedChipId();
        Chip selectedChip = binding.getRoot().findViewById(selectedChipId);
        CreateCourseFragmentDirections.ActionCreateClassFragmentToConfirmFragment action =
                CreateCourseFragmentDirections.actionCreateClassFragmentToConfirmFragment
                        (type, day, time, selectedChip.getText().toString(), capacity, duration, String.valueOf(price), description);
        Navigation.findNavController(requireView()).navigate(action);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
