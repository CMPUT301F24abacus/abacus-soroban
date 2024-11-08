package com.example.soroban.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class AdminBrowseFacilityActivity extends AppCompatActivity {

    private RecyclerView facilityRecycler;
    private AdminBrowseFacilityActivity.BrowseFacilityAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Facility> browseFacilityList;
    private String userId;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_facility);

        CollectionReference facilityRf = db.collection("facilities");
        facilityRecycler = findViewById(R.id.facility_admin_recycler);
        facilityRecycler.setLayoutManager(new LinearLayoutManager(this));

        browseFacilityList = new ArrayList<>();

        facilityRf.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    browseFacilityList.clear();
                    for (QueryDocumentSnapshot document: querySnapshots) {
                        Log.d("Firestore", "Found Facilities!");
                        Map<String, Object> facilityData = document.getData();
                        User user = new User(((DocumentReference) facilityData.get("owner")).getPath().replace("users/",""));
                        user.createFacility();
                        String name = (String) facilityData.get("name");
                        String details = (String) facilityData.get("details");
                        user.getFacility().setName(name);
                        user.getFacility().setDetails(details);
                        browseFacilityList.add(user.getFacility());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        adapter = new BrowseFacilityAdapter(browseFacilityList);
        facilityRecycler.setAdapter(adapter);

    }


    private class BrowseFacilityAdapter extends RecyclerView.Adapter<BrowseFacilityAdapter.ViewHolder> {
        private ArrayList<Facility> browseFacilityList;

        public BrowseFacilityAdapter(ArrayList<Facility> browseFacilityList) {
            this.browseFacilityList = browseFacilityList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.facility_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BrowseFacilityAdapter.ViewHolder holder, int position) {
            String facilityName = browseFacilityList.get(position).getName();
            String ownerId = browseFacilityList.get(position).getOwner().getDeviceId();
            holder.facilityNameTV.setText(facilityName);
            holder.ownerIdTV.setText("Owner ID: " + ownerId);
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
            return browseFacilityList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView facilityNameTV;
            TextView ownerIdTV;

            ViewHolder(View itemView) {
                super(itemView);
                facilityNameTV = itemView.findViewById(R.id.facility_name_text);
                ownerIdTV = itemView.findViewById(R.id.facility_owner_text);
            }
        }

    }

}
