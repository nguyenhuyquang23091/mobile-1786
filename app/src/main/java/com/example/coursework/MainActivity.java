package com.example.coursework;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coursework.data.local.AppDatabase;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.Objects;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    // Using ViewBinding to avoid findViewById and make code safer
    private ActivityMainBinding binding;
    private YogaClass editYogaClass = null;
    private boolean isVisible;
    private YogaClassRepository yogaClassRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        yogaClassRepository = new YogaRepositoryImplementation(getApplication());
        // Keep padding for system bars (EdgeToEdge) - Your original code
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(getIntent().hasExtra("uid")){
            int uid = getIntent().getIntExtra("uid", -1);
            if(uid != -1){
                loadEditClass(uid);
            }
        }

        // Setup dropdown menus and click listeners
        setupAdapters();
        setupClickListeners();
        setupFabMenu();
    }

    private void loadEditClass(int classId){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            editYogaClass = yogaClassRepository.findById(classId);
           runOnUiThread(()-> populateForm(editYogaClass));

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
                this,
                R.array.days_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        binding.dayOfWeekInput.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this,
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
                Toast.makeText(MainActivity.this, "Form submitted", Toast.LENGTH_SHORT).show();

                binding.lvAnimation.setAnimationFromUrl("https://lottie.host/1b344cc0-b4b0-4af7-8310-a8fbb260448a/4n8jLj6c1R.lottie");
                binding.lvAnimation.setVisibility(View.VISIBLE);
                binding.lvAnimation.playAnimation();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    binding.lvAnimation.cancelAnimation();
                    binding.lvAnimation.setVisibility(View.GONE);
                    if (editYogaClass != null) {
                        // UPDATE existing class
                        updateClass();
                        Toast.makeText(MainActivity.this, "Class Updated!", Toast.LENGTH_SHORT).show();
                        // Go back to the list after updating
                        startActivity(new Intent(MainActivity.this, ClassListActivity.class));
                        finish();
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
        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
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
        Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
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
            com.google.android.material.chip.Chip selectedChip = findViewById(selectedChipId);
            intent.putExtra("intensity", selectedChip.getText().toString());
        } else {
            intent.putExtra("intensity", "Not specified");
        }
        startActivity(intent);
    }
    private void setupFabMenu(){
        isVisible = false;
        binding.mainFab.setRotation(0);

        binding.mainFab.setOnClickListener(view -> {
            if(!isVisible){
                binding.viewAllClassesFab.show();
                binding.resetDatabaseFab.show();
                binding.viewAllClassesText.setVisibility(View.VISIBLE);
                binding.resetDatabaseText.setVisibility(View.VISIBLE);

                binding.mainFab.animate().rotation(135f).setInterpolator(new OvershootInterpolator()).setDuration(300).start();
            } else {
                binding.viewAllClassesFab.hide();
                binding.resetDatabaseFab.hide();
                binding.viewAllClassesText.setVisibility(View.GONE);
                binding.resetDatabaseText.setVisibility(View.GONE);

                binding.mainFab.animate().rotation(0).setInterpolator(new OvershootInterpolator()).setDuration(300).start();

            }
            isVisible = !isVisible;
        });
        binding.viewAllClassesFab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
            startActivity(intent);
        });

    }
}
