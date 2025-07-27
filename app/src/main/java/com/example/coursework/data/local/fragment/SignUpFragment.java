package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coursework.R;
import com.example.coursework.data.local.implementation.AuthRepositoryImplementation;
import com.example.coursework.data.local.repository.AuthRepository;
import com.example.coursework.data.local.util.AuthListener;
import com.example.coursework.databinding.FragmentSignUpBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private AuthRepository authRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitTransition(new MaterialElevationScale(false));
        setEnterTransition(new MaterialElevationScale(true));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepositoryImplementation(requireActivity().getApplication());
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.createAccountButton.setOnClickListener(v -> handleSignUp());
        
        binding.signInLink.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
        });
    }

    private void handleSignUp() {
        String email = Objects.requireNonNull(binding.emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.passwordInput.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(binding.confirmPasswordInput.getText()).toString().trim();

        if (validateInput(email, password, confirmPassword)) {
            setLoadingState(true);
            authRepository.signUp(email, password, new AuthListener() {
                @Override
                public void onFailure(String errorMessage) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                setLoadingState(false));
                        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(FirebaseUser user) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                setLoadingState(false));
                        Snackbar.make(binding.getRoot(), "Sign up successfully", Snackbar.LENGTH_LONG).show();
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_registerFragment_to_createCourseFragment);
                    }

                }
            });
        }
    }
    private boolean validateInput(String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            binding.emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Please enter a valid email address");
            isValid = false;
        } else {
            binding.emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            binding.passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            binding.passwordLayout.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordLayout.setError("Confirm password is required");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            binding.confirmPasswordLayout.setError(null);
        }

        return isValid;
    }

    private void setLoadingState(boolean isLoading) {
        binding.createAccountButton.setEnabled(!isLoading);
        binding.emailInput.setEnabled(!isLoading);
        binding.passwordInput.setEnabled(!isLoading);
        binding.confirmPasswordInput.setEnabled(!isLoading);
        
        if (isLoading) {
            binding.createAccountButton.setText("Creating Account...");
        } else {
            binding.createAccountButton.setText("Create Account");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
