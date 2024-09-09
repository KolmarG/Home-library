package com.example.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.library.databinding.RegistrationFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationFragment extends Fragment {

    private RegistrationFragmentBinding binding;

    public RegistrationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RegistrationFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                String username = binding.usernameEditText.getText().toString();
                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String userId = task.getResult().getUser().getUid();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + userId);
                                    // Create a new User object with the isAdmin field set to false
                                    User newUser = new User(username, email, false);
                                    databaseReference.setValue(newUser);

                                    MainFragment mainFragment = new MainFragment();
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, mainFragment)
                                            .commit();
                                } else {
                                    Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return binding.getRoot();
    }
}
