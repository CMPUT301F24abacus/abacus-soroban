package com.example.soroban.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class AdminBrowseProfileActivity extends AppCompatActivity {

    private RecyclerView profileRecycler;
    private AdminBrowseProfileActivity.BrowseProfilesAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> browseProfilesList;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_users);

        CollectionReference userRf = db.collection("users");
        profileRecycler = findViewById(R.id.user_admin_recycler);
        profileRecycler.setLayoutManager(new LinearLayoutManager(this));


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
                        browseProfilesList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        adapter = new BrowseProfilesAdapter(browseProfilesList);
        profileRecycler.setAdapter(adapter);

    }


    private class BrowseProfilesAdapter extends RecyclerView.Adapter<BrowseProfilesAdapter.ViewHolder> {
        private ArrayList<User> browseProfilesList;

        public BrowseProfilesAdapter(ArrayList<User> browseProfilesList) {
            this.browseProfilesList = browseProfilesList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.user_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BrowseProfilesAdapter.ViewHolder holder, int position) {
            String deviceId = browseProfilesList.get(position).getDeviceId();
            holder.userNameTV.setText(deviceId);
            holder.itemView.setOnClickListener(v -> {
                //Intent intent = new Intent(AdminBrowseEventActivity.this, EventRegistrationActivity.class);
                //intent.putExtra("isRegistered", true);
                //startActivity(intent);
            });
        }

        /**
         * This method returns the total number of items in the data set held by the adapter.
         * @return
         */
        @Override
        public int getItemCount() {
            return browseProfilesList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView userNameTV;

            ViewHolder(View itemView) {
                super(itemView);
                // this will be updated to use profile_item eventually...
                userNameTV = itemView.findViewById(R.id.tv_user_name);
            }
        }

    }

}