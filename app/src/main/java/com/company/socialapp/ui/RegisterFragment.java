package com.company.socialapp.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.company.socialapp.AppFragment;
import com.company.socialapp.R;
import com.company.socialapp.databinding.FragmentNewPostBinding;
import com.company.socialapp.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterFragment extends AppFragment  {

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentRegisterBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.verifyEmailButton.setOnClickListener(v -> {
            sendEmailVerification();
        });
        binding.createAccountButton.setOnClickListener(v -> {
            createAccount(binding.emailEditText.getText().toString(), binding.passwordEditText.getText().toString());
        });

        updateUI(auth.getCurrentUser());
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null){
            navController.navigate(R.id.postsHomeFragment);
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        updateUI(auth.getCurrentUser());
                    } else {
                        Log.w("ABCD", "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        updateUI(auth.getCurrentUser());
                    } else {
                        Log.w("ABCD", "signInWithEmail:failure", task.getException());
                    }
                });
    }

    private void sendEmailVerification() {
        binding.verifyEmailButton.setEnabled(false);

        final FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(requireActivity(), task -> {
                    binding.verifyEmailButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ABCD", "sendEmailVerification", task.getException());
                        Toast.makeText(requireActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = binding.emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Required.");
            valid = false;
        } else {
            binding.emailEditText.setError(null);
        }

        String password = binding.passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Required.");
            valid = false;
        } else {
            binding.passwordEditText.setError(null);
        }

        return valid;
    }
}
