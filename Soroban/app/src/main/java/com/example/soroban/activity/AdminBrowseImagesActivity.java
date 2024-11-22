package com.example.soroban.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminBrowseImagesActivity extends AppCompatActivity {
    private User appUser;
    private RecyclerView imageRecycler;
    private SearchView imageSearch;
    private Button deleteImageButton;
    private AdminBrowseImagesActivity.BrowseImagesAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> browseImageList;
    private ArrayList<String> userIdList;
    private FireBaseController firebaseController;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_images);

        // Get arguments passed from previous activity.
        // Reference: https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
        Bundle args = getIntent().getExtras();

        // Initialize appUser for this activity.
        if (args != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            } else {
                appUser = (User) args.getSerializable("appUser");
            }

            if (appUser == null) {
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }

        } else {
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        firebaseController = new FireBaseController(this);
        deleteImageButton = findViewById(R.id.delete_image_button);
        imageSearch = findViewById(R.id.image_search_bar);
        imageSearch.clearFocus();
        imageRecycler = findViewById(R.id.image_admin_recycler);
        imageRecycler.setLayoutManager(new LinearLayoutManager(this));

        browseImageList = new ArrayList<>();
        userIdList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null) {
                            browseImageList.clear();
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                userIdList.add(userId);
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(userId)
                                        .child("profileImageUrl")
                                        .get()
                                        .addOnSuccessListener(snapshotImage -> {
                                            String imageUrl = snapshotImage.getValue(String.class);
                                            Log.d("AdminBrowseProfileActivity", "Fetched image URL: " + imageUrl);  // Logging URL for debugging
                                            if (imageUrl != null) {
                                                browseImageList.add(imageUrl);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AdminBrowseProfileActivity", "Failed to load image URL", e);
                                        });
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AdminBrowseProfileActivity", "Failed to retrieve users");
                    }
                });


        adapter = new BrowseImagesAdapter(browseImageList, userIdList);
        imageRecycler.setAdapter(adapter);

        // On Searching with Search View
        // Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
        imageSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

    }

    /**
     * This is a sample adapter to display the images.
     */
    private class BrowseImagesAdapter extends RecyclerView.Adapter<BrowseImagesAdapter.ViewHolder> {
        private ArrayList<String> browseImageList;
        private ArrayList<String> userIdList;

        public BrowseImagesAdapter(ArrayList<String> browseImageList, ArrayList<String> userIdList) {
            this.browseImageList = browseImageList;
            this.userIdList = userIdList;
        }

        /**
         * this method applies filtered list from searchview into adapter
         * @param newList: new array list for search results
         */
        public void filterList(ArrayList<String> newList, ArrayList<String> newIdList) {
            browseImageList = newList;
            userIdList = newIdList;
            notifyDataSetChanged();
        }

        /**
         * This method is called when a new ViewHolder is created.
         * @param parent: The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType: The view type of the new View.
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.image_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * This method is called by RecyclerView to display the data at the specified position.
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(BrowseImagesAdapter.ViewHolder holder, int position) {
            firebaseController = new FireBaseController(AdminBrowseImagesActivity.this);
            User certainUser = new User(userIdList.get(position));
            firebaseController.fetchUserDoc(certainUser);
            holder.imageNameTV.setText("Profile Picture Of: " + certainUser.getDeviceId());
            Glide.with(AdminBrowseImagesActivity.this)
                    .load(browseImageList.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Disable caching to ensure updated image
                    .skipMemoryCache(true)
                    .into(holder.imageIV);

            holder.itemView.setOnClickListener(v -> {
                deleteImageButton.setVisibility(View.VISIBLE);

                deleteImageButton.setOnClickListener(v2 -> {
                    new AlertDialog.Builder(AdminBrowseImagesActivity.this)
                            .setTitle("Delete Image")
                            .setMessage("Would you like to delete \"" + userIdList.get(position) + "\"'s image?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                FirebaseDatabase.getInstance().getReference("users").child(certainUser.getDeviceId())
                                        .removeValue();
                            })
                            .setNegativeButton("No", null)
                            .show();
                });

            });
        }

        /**
         * This method returns the total number of items in the data set held by the adapter.
         * @return
         */
        @Override
        public int getItemCount() {
            return browseImageList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageIV;
            TextView imageNameTV;
            TextView imageDetailsTV;

            ViewHolder(View itemView) {
                super(itemView);
                imageIV = itemView.findViewById(R.id.image_iv);
                imageNameTV = itemView.findViewById(R.id.image_name_text);
                imageDetailsTV = itemView.findViewById(R.id.image_origin_text);
            }
        }

    }


    /**
     * This method filters data based on query
     * Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
     * @param enteredText: text searched by query
     */
    private void filter(String enteredText) {
        ArrayList<String> filteredImageList = new ArrayList<>();
        ArrayList<String> filteredUserList = new ArrayList<>();
        for (String filtered : userIdList) {
            int index = userIdList.indexOf(filtered);
            if (filtered.toLowerCase().contains(enteredText.toLowerCase())) {
                filteredImageList.add(browseImageList.get(index));
                filteredUserList.add(filtered);
            }
        }

        adapter.filterList(filteredImageList, filteredUserList);

    }

}
