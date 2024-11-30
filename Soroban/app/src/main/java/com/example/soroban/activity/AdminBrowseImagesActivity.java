package com.example.soroban.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.soroban.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Allows administrators to browse and manage user profile image stored in Firebase.
 * Implements functionality for searching, filtering, displaying, and deleting user profile images.
 * @author Kevin Li
 * @see FireBaseController
 * @see User
 */
public class AdminBrowseImagesActivity extends AppCompatActivity {
    private User appUser;
    private RecyclerView imageRecycler;
    private SearchView imageSearch;
    private Button deleteImageButton;
    private AdminBrowseImagesActivity.BrowseImagesAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> browseImageList;
    private ArrayList<String> idList;
    private ArrayList<String> keyList;
    private FireBaseController firebaseController;

    /**
     * Called when the activity is first created.
     * Sets up the UI, initializes Firebase, and configures the RecyclerView.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if required arguments are missing or incorrect.
     */
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

        // Customize SearchView text and hint color programmatically
        // Reference: Customize SearchView EditText color programmatically
        int searchEditTextId = imageSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = imageSearch.findViewById(searchEditTextId);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK); // Set text color to black
            searchEditText.setHintTextColor(Color.GRAY); // Set hint color to gray
        }

        // Customize SearchView background
        imageSearch.setBackgroundResource(R.drawable.search_view_background);



        browseImageList = new ArrayList<>();
        idList = new ArrayList<>();
        keyList = new ArrayList<>();


        firebaseRead(browseImageList, idList, keyList);

        adapter = new BrowseImagesAdapter(browseImageList, idList, keyList);
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
     * This is a sample adapter to display user profile images.
     */
    private class BrowseImagesAdapter extends RecyclerView.Adapter<BrowseImagesAdapter.ViewHolder> {
        private ArrayList<String> browseImageList;
        private ArrayList<String> idList;
        private ArrayList<String> keyList;

        /**
         * Constructor for the adapter.
         * @param browseImageList the list of image URLs to display.
         * @param idList the list of important references associated with the images.
         * @param keyList the list of keys indicating whether image is from a "profile" or "event"
         */
        public BrowseImagesAdapter(ArrayList<String> browseImageList, ArrayList<String> idList, ArrayList<String> keyList) {
            this.browseImageList = browseImageList;
            this.idList = idList;
            this.keyList = keyList;
        }

        /**
         * this method updates the adapter with a new filtered
         * list of images and user IDs from searchview into adapter
         * @param newList: new array list for search results
         */
        public void filterList(ArrayList<String> newList, ArrayList<String> newIdList, ArrayList<String> newKeyList) {
            browseImageList = newList;
            idList = newIdList;
            keyList = newKeyList;
            notifyDataSetChanged();
        }

        /**
         * This method is called when a new ViewHolder is created.
         * @param parent: The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType: The view type of the new View.
         * @return a new ViewHolder
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
            String newKey = keyList.get(position);

            holder.imageNameTV.setText(newKey);
            String id = idList.get(position);
            String[] splitId = id.split(", ");
            String imageDetail = (newKey == "From Event: ") ? splitId[1] + "'s " + splitId[0] : null;
            holder.imageDetailsTV.setText((newKey == "From Profile: ") ? id : imageDetail);
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
                            .setMessage("Would you like to delete \"" + ((newKey == "From Profile: ") ? id : splitId[1]) + "\"'s image?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                if (newKey == "From Profile: ") {
                                    FirebaseDatabase.getInstance().getReference("users").child(id)
                                            .removeValue();
                                } else if (newKey == "From Event: ") {
                                    db.collection("events").document(id).update("posterUrl", FieldValue.delete());
                                    firebaseRead(browseImageList, idList, keyList);
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                });

            });



        }

        /**
         * This method returns the total number of items in the data set held by the adapter.
         * @return the item count
         */
        @Override
        public int getItemCount() {
            return browseImageList.size();
        }

        /**
         * ViewHolder for RecyclerView items representing profile images.
         */
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
        ArrayList<String> filteredIdList = new ArrayList<>();
        ArrayList<String> filteredKeyList = new ArrayList<>();
        for (String filtered : idList) {
            int index = idList.indexOf(filtered);
            if (filtered.toLowerCase().contains(enteredText.toLowerCase())) {
                filteredImageList.add(browseImageList.get(index));
                filteredIdList.add(filtered);
                filteredKeyList.add(keyList.get(index));
            }
        }

        adapter.filterList(filteredImageList, filteredIdList, filteredKeyList);

    }

    /**
     * This method reads the url data from Firebase.
     */
    private void firebaseRead(ArrayList<String> browseImageList, ArrayList<String> idList, ArrayList<String> keyList) {

        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        browseImageList.clear();
                        idList.clear();
                        keyList.clear();
                        if (snapshot != null) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                idList.add(userId);
                                keyList.add("From Profile: ");
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

                            db.collection("events").orderBy("posterUrl").whereNotEqualTo("posterUrl", null)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Log.e("Firestore", error.toString());
                                                return;
                                            }
                                            if (querySnapshots != null) {
                                                for (QueryDocumentSnapshot document: querySnapshots) {
                                                    Map<String, Object> urlData = document.getData();
                                                    if (!idList.contains(document.getId())) {
                                                        idList.add(document.getId());
                                                        keyList.add("From Event: ");
                                                        browseImageList.add((String) urlData.get("posterUrl"));
                                                    }
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AdminBrowseProfileActivity", "Failed to retrieve users");
                    }
                });
    }

}
