package com.company.socialapp.ui;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.company.socialapp.AppFragment;
import com.company.socialapp.BuildConfig;
import com.company.socialapp.databinding.FragmentNewPostBinding;
import com.company.socialapp.model.Post;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class NewPostFragment extends AppFragment {

    public Uri mediaUri;
    public String mediaTipo;

    private FragmentNewPostBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentNewPostBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.publicar.setOnClickListener(v -> submitPost());

        binding.camaraFotos.setOnClickListener(v -> tomarFoto());
        binding.camaraVideo.setOnClickListener(v -> tomarVideo());
        binding.grabarAudio.setOnClickListener(v -> grabarAudio());
         
        binding.imagenGaleria.setOnClickListener(v -> seleccionarImagen());
        binding.videoGaleria.setOnClickListener(v -> seleccionarVideo());
        binding.audioGaleria.setOnClickListener(v -> seleccionarAudio());

        appViewModel.mediaSeleccionado.observe(getViewLifecycleOwner(), media -> {
            this.mediaUri = media.uri;
            this.mediaTipo = media.tipo;

            Glide.with(this).load(media.uri).into(binding.previsualizacion);
        });
    }


    private final ActivityResultLauncher<String> galeria = registerForActivityResult(new GetContent(), uri -> {
        appViewModel.setMediaSeleccionado(uri, mediaTipo);
    });

    private final ActivityResultLauncher<Uri> camaraFotos = registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
        appViewModel.setMediaSeleccionado(mediaUri, "image");
    });

    private final ActivityResultLauncher<Uri> camaraVideos = registerForActivityResult(new ActivityResultContracts.TakeVideo(), isSuccess -> {
        appViewModel.setMediaSeleccionado(mediaUri, "video");
    });

    private final ActivityResultLauncher<Intent> grabadoraAudio = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            appViewModel.setMediaSeleccionado(result.getData().getData(), "audio");
        }
    });

    private void seleccionarImagen() {
        mediaTipo = "image";
        galeria.launch("image/*");
    }

    private void seleccionarVideo() {
        mediaTipo = "video";
        galeria.launch("video/*");
    }

    private void seleccionarAudio() {
        mediaTipo = "audio";
        galeria.launch("audio/*");
    }


    private void tomarFoto() {
        try {
            mediaUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", File.createTempFile("img", ".jpg", requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
            camaraFotos.launch(mediaUri);
        } catch (IOException e) {}
    }


    private void tomarVideo() {
        try {
            mediaUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", File.createTempFile("vid", ".mp4", requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)));
            camaraVideos.launch(mediaUri);
        } catch (IOException e) {}
    }

    private void grabarAudio() {
        grabadoraAudio.launch(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION));
    }

    private void submitPost(){
        String postText = binding.contenido.getText().toString();

        if (postText.isEmpty()) {
            binding.contenido.setError("Required");
            return;
        }

        binding.publicar.setEnabled(false);

        if (mediaTipo == null) {
            writeNewPost(postText, null);
        } else {
            uploadAndWriteNewPost(postText);
        }
    }

    private void writeNewPost(String postText, String mediaUrl) {
        db.collection("posts")
                .add(new Post(uid, user.getDisplayName(), user.getPhotoUrl().toString(), postText, mediaUrl, mediaTipo))
                .addOnSuccessListener(documentReference -> {
                    navController.popBackStack();
                    appViewModel.setMediaSeleccionado(null, null);
                });
    }

    private void uploadAndWriteNewPost(final String postText) {
        storage.getReference(mediaTipo + "/" + UUID.randomUUID())
                .putFile(mediaUri)
                .continueWithTask(task -> task.getResult().getStorage().getDownloadUrl())
                .addOnSuccessListener(url -> writeNewPost(postText, url.toString()));
    }
}
