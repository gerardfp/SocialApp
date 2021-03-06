package com.company.socialapp.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.company.socialapp.AppFragment;
import com.company.socialapp.R;
import com.company.socialapp.databinding.FragmentSignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInFragment extends AppFragment {

    private FragmentSignInBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentSignInBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signInProgressBar.setVisibility(View.GONE);

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build());

        firebaseAuthWithGoogle(GoogleSignIn.getLastSignedInAccount(requireContext()));

        binding.signInButton.setOnClickListener(view1 -> {
            signInClient.launch(googleSignInClient.getSignInIntent());
        });
    }

    ActivityResultLauncher<Intent> signInClient = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class));
                    } catch (ApiException e) {}
                }
            });

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        if(account == null) return;

        binding.signInProgressBar.setVisibility(View.VISIBLE);
        binding.signInButton.setVisibility(View.GONE);

        auth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        navController.navigate(R.id.postsHomeFragment);
                    } else {
                        binding.signInProgressBar.setVisibility(View.GONE);
                        binding.signInButton.setVisibility(View.VISIBLE);
                    }
                });
    }
}
