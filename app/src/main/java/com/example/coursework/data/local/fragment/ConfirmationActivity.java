package com.example.coursework.data.local.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.coursework.R;
import com.example.coursework.data.local.MainActivity;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.implementation.YogaRepositoryImplementation;
import com.example.coursework.data.local.repository.YogaClassRepository;
import com.example.coursework.databinding.ActivityConfirmationBinding;

import java.util.Objects;

public class ConfirmationActivity extends AppCompatActivity {

    private ActivityConfirmationBinding binding;
    private YogaClassRepository yogaClassRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirmation);

        // Initialize the repository
        yogaClassRepository = new YogaRepositoryImplementation(getApplication());

        // Set up the UI
        getData();
        setupClickListeners(); // Changed from confirmAction() to a more descriptive name
    }

    /**
     * Retrieves data from the Intent and populates the TextViews.
     */
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
            binding.valDescription.setText("No description provided.");
        }
    }

    /**
     * Sets up the OnClickListeners for the new split button.
     */
    private void setupClickListeners() {
        // The main "Confirm" button's logic is now in a helper method.
        binding.confirmButton.setOnClickListener(v -> {
            saveAndFinish();
        });

        // The new dropdown button shows a menu with the "Edit" option.
        binding.moreActionsButton.setOnClickListener(this::showMoreActionsMenu);
    }

    /**
     * Creates and shows a PopupMenu for the "Edit" action.
     * @param anchor The view to which the popup menu should be anchored.
     */
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

    /**
     * A helper method to create the YogaClass object, save it, and finish the activity.
     */
    private void saveAndFinish() {
        YogaClass yogaClass = new YogaClass();
        Intent intent = getIntent();

        // **BUG FIX:** We now correctly parse the string data into numbers.
        yogaClass.type = intent.getStringExtra("type");
        yogaClass.day = intent.getStringExtra("day");
        yogaClass.time = intent.getStringExtra("time");
        yogaClass.intensity = intent.getStringExtra("intensity");
        yogaClass.capacity = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("capacity")));
        yogaClass.duration = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("duration")));
        yogaClass.price = Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("price")));
        yogaClass.description = intent.getStringExtra("description");

        // Use the repository to insert the class.
        yogaClassRepository.insert(yogaClass);

        Toast.makeText(this, "Class saved successfully!", Toast.LENGTH_LONG).show();

        // Navigate back to the main activity.
        Intent mainActivityIntent = new Intent(ConfirmationActivity.this, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivityIntent);
        finish();
    }
}