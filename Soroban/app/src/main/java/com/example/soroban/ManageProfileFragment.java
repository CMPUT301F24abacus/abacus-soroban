package com.example.soroban;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

public class ManageProfileFragment extends DialogFragment{
    private UserController userController;

    public static ManageProfileFragment newInstance(@Nullable User user){
        Bundle args = new Bundle();
        args.putSerializable("appUser", user);

        ManageProfileFragment newFragment = new ManageProfileFragment();
        newFragment.setArguments(args);
        return newFragment;
    }


    @SuppressLint({"DialogFragmentCallbacksDetector", "SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = getLayoutInflater().inflate(R.layout.user_profile_edit, null);

        // Get references to all views that will display a profile's information.
        TextView firstNameEdit = view.findViewById(R.id.user_firstNameEdit);
        TextView lastNameEdit = view.findViewById(R.id.user_lastNameEdit);
        TextView emailEdit = view.findViewById(R.id.user_emailAddressEdit);
        TextView phoneNumberEdit = view.findViewById(R.id.user_PhoneNumberEdit);

        Bundle args = getArguments();
        if(args != null){

            Dialog newDialog = super.onCreateDialog(savedInstanceState);
            newDialog.setContentView(view);

            // Reference: https://stackoverflow.com/questions/7189948/full-screen-dialogfragment-in-android
            newDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            Serializable user = args.getSerializable("appUser");


            // Check if the app user was passed as an argument
            if(user != null){
                User appUser= (User)user;

                // Set up controller to enable changes to user
                userController = new UserController(appUser);

                // Update views to have correct info

                firstNameEdit.setText(appUser.getFirstName());
                lastNameEdit.setText(appUser.getLastName());
                emailEdit.setText(appUser.getEmail());
                phoneNumberEdit.setText(String.valueOf(appUser.getPhoneNumber()));

                // Set up listeners for buttons
                Button discardButton = view.findViewById(R.id.discardChangesButton);
                Button confirmButton = view.findViewById(R.id.confirmChangesButton);

                // Exit Profile editing
                discardButton.setOnClickListener(v -> {
                    newDialog.dismiss();
                });

                // Confirm Profile edits
                confirmButton.setOnClickListener(v -> {
                    userController.updateUser(firstNameEdit.getText(), lastNameEdit.getText(), emailEdit.getText(), phoneNumberEdit.getText());
                    newDialog.dismiss();
                });


            }else{
                throw new RuntimeException("Must pass User object to populate fields.");
            }
            return newDialog;

        }else{
            throw new RuntimeException("Instantiate ManageProfileFragment by using newInstance() method.");
        }
    }

}
