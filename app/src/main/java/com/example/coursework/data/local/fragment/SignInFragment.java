package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coursework.R;
import com.example.coursework.data.local.implementation.AuthRepositoryImplementation;
import com.example.coursework.data.local.repository.AuthRepository;
import com.example.coursework.data.local.util.AuthListener;
import com.example.coursework.databinding.FragmentSignInBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
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
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepositoryImplementation(requireActivity().getApplication());
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.loginButton.setOnClickListener(v -> handleSignIn());
        
        binding.signupText.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }

    private void handleSignIn() {
        String email = Objects.requireNonNull(binding.emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.passwordInput.getText()).toString().trim();

        if (validateInput(email, password)) {
            setLoadingState(true);
            authRepository.signIn(email, password, new AuthListener() {
                @Override
                public void onFailure(String errorMessage) {
                    if (getActivity() != null && binding != null) {
                        getActivity().runOnUiThread(() -> setLoadingState(false));
                        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(FirebaseUser user) {
                    if (getActivity() != null && binding != null) {
                        getActivity().runOnUiThread(() -> setLoadingState(false));
                        Snackbar.make(binding.getRoot(), "Sign in successful", Snackbar.LENGTH_LONG).show();
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_loginFragment_to_createClassFragment);
                    }
                }
            });
        }
    }

    private boolean validateInput(String email, String password) {
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

        return isValid;
    }

    private void setLoadingState(boolean isLoading) {
        if (binding == null) return;
        
        binding.loginButton.setEnabled(!isLoading);
        binding.emailInput.setEnabled(!isLoading);
        binding.passwordInput.setEnabled(!isLoading);
        
        if (isLoading) {
            binding.loginButton.setText("Signing In...");
        } else {
            binding.loginButton.setText("Sign In");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
