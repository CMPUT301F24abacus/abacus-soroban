package com.example.soroban.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Allows administrators to browse user profiles.
 * Implements functionality for searching, filtering, and displaying user profiles in a RecyclerView.
 * @author Kevin Li
 * @see AdminViewProfileActivity
 * @see User
 * @see FireBaseController
 */
public class AdminBrowseProfileActivity extends AppCompatActivity {
    private User appUser;
    private RecyclerView profileRecycler;
    private SearchView profileSearch;
    private Switch searchSwitch;
    private AdminBrowseProfileActivity.BrowseProfilesAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> browseProfilesList;

    /**
     * Initializes the activity. Sets up the user interface, Firebase connection,
     * and RecyclerView for browsing user profiles.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if required arguments are missing or incorrect.
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_users);

        // Get arguments passed from previous activity.
        // Reference: https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
        Bundle args = getIntent().getExtras();

        // Initialize appUser for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
            }

            if(appUser == null ){
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        CollectionReference userRf = db.collection("users");
        searchSwitch = findViewById(R.id.searchSwitch);
        searchSwitch.setChecked(false);
        searchSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (searchSwitch.isChecked()) {
                searchSwitch.setText("Name Search");
            } else {
                searchSwitch.setText("ID Search");
            }
        });

        profileSearch = findViewById(R.id.profile_search_bar);
        profileSearch.clearFocus();
        profileRecycler = findViewById(R.id.user_admin_recycler);
        profileRecycler.setLayoutManager(new LinearLayoutManager(this));


        // Customize SearchView text and hint color programmatically
        // Reference: Customize SearchView EditText color programmatically
        int searchEditTextId = profileSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = profileSearch.findViewById(searchEditTextId);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK); // Set text color to black
            searchEditText.setHintTextColor(Color.GRAY); // Set hint color to gray
        }

        // Customize SearchView background
        profileSearch.setBackgroundResource(R.drawable.search_view_background);


        browseProfilesList = new ArrayList<>();

        userRf.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    browseProfilesList.clear();
                    for (QueryDocumentSnapshot document: querySnapshots) {
                        Log.d("Firestore", "Browsing users.");
                        Map<String, Object> userData = document.getData();
                        User user = new User((String) userData.get("deviceId"));
                        user.setEmail((String) userData.get("email"));
                        user.setFirstName((String) userData.get("firstName"));
                        user.setLastName((String) userData.get("lastName"));
                        if (userData.get("phoneNumber") != null) { user.setPhoneNumber((long) userData.get("phoneNumber")); }
                        user.createFacility();
                        browseProfilesList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        adapter = new BrowseProfilesAdapter(browseProfilesList);
        profileRecycler.setAdapter(adapter);

        // On Searching with Search View
        // Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
        profileSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(AdminBrowseProfileActivity.this, AdminDashboardActivity.class);
                Bundle newArgs = new Bundle();
                newArgs.putSerializable("appUser",appUser);
                intent.putExtras(newArgs);
                startActivity(intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

    }

    /**
     * This is a sample adapter to display the user profiles.
     */
    private class BrowseProfilesAdapter extends RecyclerView.Adapter<BrowseProfilesAdapter.ViewHolder> {
        private ArrayList<User> browseProfilesList;

        /**
         * Constructor for the adapter.
         *
         * @param browseProfilesList the list of user profiles to display.
         */
        public BrowseProfilesAdapter(ArrayList<User> browseProfilesList) {
            this.browseProfilesList = browseProfilesList;
        }

        /**
         * this method applies filtered list from searchview into adapter
         * @param newList: new array list for search results
         */
        public void filterList(ArrayList<User> newList) {
            browseProfilesList = newList;
            notifyDataSetChanged();
        }

        /**
         * This method is called when a new ViewHolder is created.
         * @param parent: The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType: The view type of the new View.
         * @return a new VieHolder
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.profile_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * This method is called by RecyclerView to display the data at the specified position.
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(BrowseProfilesAdapter.ViewHolder holder, int position) {
            String deviceId = browseProfilesList.get(position).getDeviceId();
            String userName = browseProfilesList.get(position).getFirstName(); // replace with userName eventually (if we're using it)
            holder.userIdTV.setText(deviceId);
            if (userName != null) {
                holder.userNameTV.setText(userName);
            } else {
                holder.userNameTV.setText("No Name Available");
            }
            // Fetch and display the profile image
            FirebaseDatabase.getInstance().getReference("users")
                    .child(deviceId)
                    .child("profileImageUrl")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        String imageUrl = snapshot.getValue(String.class);
                        Log.d("AdminBrowseProfileActivity", "Fetched image URL: " + imageUrl);  // Logging URL for debugging
                        if (imageUrl != null) {
                            Log.e("AdminBrowseProfileActivity", "before glide");
                            Glide.with(AdminBrowseProfileActivity.this)
                                    .load(imageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Disable caching to ensure updated image
                                    .skipMemoryCache(true)
                                    .into(holder.userPicIV);
                            Log.e("AdminBrowseProfileActivity", "After glide");
                        } else {
                            holder.userPicIV.setImageResource(R.drawable.ic_profile); // Set default if no URL
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdminBrowseProfileActivity", "Failed to load image URL", e);
                        holder.userPicIV.setImageResource(R.drawable.ic_profile); // Default image if fetch fails
                    });

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminBrowseProfileActivity.this, AdminViewProfileActivity.class);
                User selectedUser = browseProfilesList.get(position);
                Bundle newArgs = new Bundle();
                newArgs.putSerializable("selectedUser", selectedUser);
                newArgs.putSerializable("appUser", appUser);
                intent.putExtras(newArgs);
                startActivity(intent);
            });
        }

        /**
         * This method returns the total number of items in the data set held by the adapter.
         * @return the item count
         */
        @Override
        public int getItemCount() {
            return browseProfilesList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView userIdTV;
            TextView userNameTV;
            ImageView userPicIV;

            /**
             * Constructor for the ViewHolder.
             *
             * @param itemView the item view.
             */
            ViewHolder(View itemView) {
                super(itemView);
                userIdTV = itemView.findViewById(R.id.tv_profile_id);
                userNameTV = itemView.findViewById(R.id.tv_profile_name);
                userPicIV = itemView.findViewById(R.id.iv_profile_picture);
            }
        }

    }

    /**
     * This method filters data based on query
     * Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
     * @param enteredText: text searched by query
     */
    private void filter(String enteredText) {
        ArrayList<User> filteredList = new ArrayList<>();
        for (User filteredUser : browseProfilesList) {
            if (searchSwitch.isChecked()) {
                if (filteredUser.getFirstName() != null) {
                    if (filteredUser.getFirstName().toLowerCase().contains(enteredText.toLowerCase())) {
                        filteredList.add(filteredUser);
                    }
                }
            } else {
                if (filteredUser.getDeviceId().toLowerCase().contains(enteredText.toLowerCase())) {
                    filteredList.add(filteredUser);
                }
            }
        }

        adapter.filterList(filteredList);

    }


}