package com.example.library;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.library.databinding.ContactFragmentBinding;


public class ContactFragment extends Fragment {

    private ContactFragmentBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ContactFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set onClickListener for the send button
        binding.sendButton.setOnClickListener(v -> {
            // Get the email input by the user
            String email = binding.emailInput.getText().toString();

            // Check if the email is not empty
            if (!email.isEmpty()) {
                // Create the intent with the email address
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "ng19022005@gmail.com", null));
                // Add the user's email as the subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "From: " + email);

                // Start the email intent
                startActivity(emailIntent);
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
