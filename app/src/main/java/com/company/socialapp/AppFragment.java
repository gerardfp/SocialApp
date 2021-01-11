package com.company.socialapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public abstract class AppFragment extends Fragment {
    public AppViewModel appViewModel;
    public NavController navController;
    public FirebaseFirestore db;
    public FirebaseUser user;
    public FirebaseStorage storage;
    public FirebaseAuth auth;
    public String uid;

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        navController = Navigation.findNavController(view);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) uid = user.getUid();

//        Log.e("NBS", " ");
//        for(int is : navController.saveState().getIntArray("android-support-nav:controller:backStackIds")){
//            Log.e("NBS", "> " + view.getContext().getResources().getResourceEntryName(is));
//        }
//
//        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
//                new OnBackPressedCallback(true) {
//                    @Override
//                    public void handleOnBackPressed() {
//                        navController.popBackStack();
//                    }
//                });
    }
}
