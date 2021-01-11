package com.company.socialapp.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.socialapp.AppFragment;
import com.company.socialapp.R;
import com.company.socialapp.databinding.FragmentPostsBinding;
import com.company.socialapp.databinding.ViewholderPostBinding;
import com.company.socialapp.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

public class PostsHomeFragment extends AppFragment {

    private FragmentPostsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPostsBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fab.setOnClickListener(v -> navController.navigate(R.id.newPostFragment));

        binding.postsRecyclerView.setAdapter(
                new PostsAdapter(
                        new FirestoreRecyclerOptions.Builder<Post>()
                            .setQuery(setQuery(), Post.class)
                            .setLifecycleOwner(this)
                            .build()
                )
        );
    }

    class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsViewHolder>{
        PostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) { super(options); }

        @NonNull
        @Override
        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PostsViewHolder(ViewholderPostBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull final Post post) {
            final String postKey = getSnapshots().getSnapshot(position).getId();

            holder.binding.autor.setText(post.author);
            Glide.with(PostsHomeFragment.this).load(post.authorPhotoUrl).circleCrop().into(holder.binding.autorFoto);

            holder.binding.contenido.setText(post.content);

            holder.binding.favorito.setChecked(post.likes.containsKey(user.getUid()));
            holder.binding.favorito.setText(String.valueOf(post.likes.size()));

            holder.binding.favorito.setOnClickListener(view -> {
                db.collection("posts")
                        .document(postKey)
                        .update("likes."+uid, post.likes.containsKey(uid) ? FieldValue.delete() : true);
            });

            if (post.mediaUrl != null) {
                holder.binding.imagen.setVisibility(View.VISIBLE);

                if ("audio".equals(post.mediaType)) {
                    Glide.with(requireView()).load(R.drawable.audio).centerCrop().into(holder.binding.imagen);
                } else {
                    Glide.with(requireView()).load(post.mediaUrl).centerCrop().into(holder.binding.imagen);
                }

                holder.binding.imagen.setOnClickListener(view -> {
                    appViewModel.postSeleccionado.setValue(post);
                    navController.navigate(R.id.mediaFragment);
                });
            } else {
                holder.binding.imagen.setVisibility(View.GONE);
            }
        }
    }

    static class PostsViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderPostBinding binding;

        public PostsViewHolder(ViewholderPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    Query setQuery(){
        return db.collection("posts").limit(50);
    }
}
