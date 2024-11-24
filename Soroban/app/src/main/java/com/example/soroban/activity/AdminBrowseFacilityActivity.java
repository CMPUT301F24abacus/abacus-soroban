package com.example.soroban.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    private User appUser;
    private RecyclerView facilityRecycler;
    private SearchView facilitySearch;
    private AdminBrowseFacilityActivity.BrowseFacilityAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Facility> browseFacilityList;
    private FireBaseController fireBaseController;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_facility);

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

        CollectionReference facilityRf = db.collection("facilities");
        fireBaseController = new FireBaseController(this);
        facilitySearch = findViewById(R.id.facility_search_bar);
        facilitySearch.clearFocus();
        facilityRecycler = findViewById(R.id.facility_admin_recycler);
        facilityRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Customize SearchView text and hint color programmatically
        // Reference: Customize SearchView EditText color programmatically
        int searchEditTextId = facilitySearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = facilitySearch.findViewById(searchEditTextId);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK); // Set text color to black
            searchEditText.setHintTextColor(Color.GRAY); // Set hint color to gray
        }

        // Customize SearchView background
        facilitySearch.setBackgroundResource(R.drawable.search_view_background);



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
                        fireBaseController.fetchUserDoc(user);
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

        // On Searching with Search View
        // Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
        facilitySearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
     * This is a sample adapter to display the facilities.
     */
    private class BrowseFacilityAdapter extends RecyclerView.Adapter<BrowseFacilityAdapter.ViewHolder> {
        private ArrayList<Facility> browseFacilityList;

        public BrowseFacilityAdapter(ArrayList<Facility> browseFacilityList) {
            this.browseFacilityList = browseFacilityList;
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
            View view = getLayoutInflater().inflate(R.layout.facility_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * this method applies filtered list from searchview into adapter
         * @param newList: new array list for search results
         */
        public void filterList(ArrayList<Facility> newList) {
            browseFacilityList = newList;
            notifyDataSetChanged();
        }

        /**
         * This method is called by RecyclerView to display the data at the specified position.
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(BrowseFacilityAdapter.ViewHolder holder, int position) {
            String facilityName = browseFacilityList.get(position).getName();
            String ownerId = browseFacilityList.get(position).getOwner().getDeviceId();
            holder.facilityNameTV.setText(facilityName);
            holder.ownerIdTV.setText("Owner ID: " + ownerId);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminBrowseFacilityActivity.this, AdminViewFacilityActivity.class);
                Facility selectedFacility = browseFacilityList.get(position);
                Bundle newArgs = new Bundle();
                newArgs.putSerializable("selectedFacility", selectedFacility);
                newArgs.putSerializable("appUser", appUser);
                intent.putExtras(newArgs);
                startActivity(intent);
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

    /**
     * This method filters data based on query
     * Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
     * @param enteredText: text searched by query
     */
    private void filter(String enteredText) {
        ArrayList<Facility> filteredList = new ArrayList<>();
        for (Facility filteredFacility : browseFacilityList) {
            if (filteredFacility.getName().toLowerCase().contains(enteredText.toLowerCase())) {
                filteredList.add(filteredFacility);
            }
        }

        adapter.filterList(filteredList);

    }

}
