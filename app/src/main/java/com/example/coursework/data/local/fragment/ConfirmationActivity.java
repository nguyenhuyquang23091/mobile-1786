package com.example.coursework.data.local.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.coursework.R;
import com.example.coursework.data.local.MainActivity;
import com.example.coursework.data.local.entities.YogaCourse;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.data.local.util.SyncFirebaseListener;
import com.example.coursework.databinding.ActivityConfirmationBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ConfirmationActivity extends AppCompatActivity {

    private ActivityConfirmationBinding binding;
    private YogaClassRepository yogaClassRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirmation);

        yogaClassRepository = new YogaRepositoryImplementation(getApplication());

        getData();
        setupClickListeners();
    }

    private void getData() {
        Intent intent = getIntent();
        binding.valType.setText(intent.getStringExtra("type"));
        binding.valDay.setText(intent.getStringExtra("day"));
        binding.valTime.setText(intent.getStringExtra("time"));
        binding.valIntensity.setText(intent.getStringExtra("intensity"));

        String capacity = intent.getStringExtra("capacity") + " people";
        binding.valCapacity.setText(capacity);

        String duration = intent.getStringExtra("duration") + " minutes";
        binding.valDuration.setText(duration);

        String price = "£" + intent.getStringExtra("price");
        binding.valPrice.setText(price);

        String description = intent.getStringExtra("description");
        if (description != null && !description.isEmpty()) {
            binding.valDescription.setText(description);
        } else {
            binding.valDescription.setText(R.string.no_description_provided);
        }
    }


    private void setupClickListeners() {
        binding.confirmButton.setOnClickListener(v -> {
            saveAndFinish();
        });

        binding.moreActionsButton.setOnClickListener(this::showMoreActionsMenu);
    }

    private void showMoreActionsMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        // This inflates the menu we created earlier.
        popup.getMenuInflater().inflate(R.menu.confirmation_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                // finish() closes this activity and returns to MainActivity to edit.
                finish();
                return true;
            }
            return false;
        });

        popup.show();
    }
    private void saveAndFinish() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.buttonsLayout.setVisibility(View.GONE);

        YogaCourse yogaCourse = new YogaCourse();
        Intent intent = getIntent();

        yogaCourse.type = intent.getStringExtra("type");
        yogaCourse.day = intent.getStringExtra("day");
        yogaCourse.time = intent.getStringExtra("time");
        yogaCourse.intensity = intent.getStringExtra("intensity");
        yogaCourse.capacity = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("capacity")));
        yogaCourse.duration = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("duration")));
        yogaCourse.price = Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("price")));
        yogaCourse.description = intent.getStringExtra("description");

        // Use the repository to insert the class.
        yogaClassRepository.insert(yogaCourse, new SyncFirebaseListener() {
            @Override
            public void syncFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
                    navigateToMain();
                });
            }
            @Override
            public void syncSuccess() {
                runOnUiThread(() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Class saved and synced successfully!", Snackbar.LENGTH_LONG).show();
                    navigateToMain();
                });
            }
        });


        Snackbar.make(findViewById(android.R.id.content), "Class saved successfully!", Snackbar.LENGTH_LONG).show();

    }

    private void navigateToMain(){
        Intent mainActivityIntent = new Intent(ConfirmationActivity.this, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivityIntent);
        finish();
    }
}