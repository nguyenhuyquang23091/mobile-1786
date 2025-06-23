package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.databinding.DataBindingUtil;

import com.example.coursework.databinding.ActivityConfirmationBinding;

public class ConfirmationActivity extends AppCompatActivity {
    private ActivityConfirmationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirmation);

        getData();
        confirmAction();
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
            binding.valDescription.setText(getString((R.string.no_description)));
        }
    }

    private void confirmAction() {
        binding.editButton.setOnClickListener(v -> {
            finish();
        });

        binding.confirmButton.setOnClickListener(v -> {
            Toast.makeText(this, "Class confirmed! (Save logic to be implemented)", Toast.LENGTH_LONG).show();
            Intent mainActivityIntent = new Intent(ConfirmationActivity.this, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivityIntent);
            finish();
        });
    }
}