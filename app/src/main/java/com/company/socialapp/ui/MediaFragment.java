package com.company.socialapp.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.company.socialapp.AppFragment;
import com.company.socialapp.databinding.FragmentMediaBinding;


public class MediaFragment extends AppFragment {

    private FragmentMediaBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentMediaBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        appViewModel.postSeleccionado.observe(getViewLifecycleOwner(), post -> {
            if ("video".equals(post.mediaType) || "audio".equals(post.mediaType)) {
                MediaController mc = new MediaController(requireContext());
                mc.setAnchorView(binding.videoView);
                binding.videoView.setMediaController(mc);
                binding.videoView.setVideoPath(post.mediaUrl);
                binding.videoView.start();
            } else if ("image".equals(post.mediaType)) {
                Glide.with(requireView()).load(post.mediaUrl).into(binding.imageView);
            }
        });
    }
}
