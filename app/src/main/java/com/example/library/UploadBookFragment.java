package com.example.library;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadBookFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_PDF = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 2;

    private EditText titleEditText;
    private Button selectPdfButton;
    private Button uploadButton;
    private Button homeButton;
    private Uri pdfUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_book, container, false);

        titleEditText = view.findViewById(R.id.titleEditText);
        selectPdfButton = view.findViewById(R.id.selectPdfButton);
        uploadButton = view.findViewById(R.id.uploadButton);
        homeButton = view.findViewById(R.id.button);

        selectPdfButton.setOnClickListener(v -> selectPDF());

        uploadButton.setOnClickListener(v -> uploadBook());
        homeButton.setOnClickListener(view1 -> home());

        return view;
    }

    private void selectPDF() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSIONS);
        } else {
            openFilePicker();
        }
    }
    private void home(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .addToBackStack(null)
                .commit();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_CODE_SELECT_PDF);
    }

    private void uploadBook() {
        Toast.makeText(getActivity(), "Книжка успешно загружена", Toast.LENGTH_SHORT).show();
        String title = titleEditText.getText().toString().trim();
        Log.d("UploadBookFragment", "Title entered: " + title);
        if (title.isEmpty() || pdfUri == null) {
            Toast.makeText(getActivity(), "ПЖ, введи название книги и файл", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("UploadBookFragment", "Uploading book with title: " + title);

        // Use the title entered by the user to form the storage path
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("books/" + title + ".pdf");
        UploadTask uploadTask = storageReference.putFile(pdfUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Book book = new Book(title, downloadUrl); // Use the constructor with two parameters
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("books").add(book).addOnSuccessListener(documentReference -> {
                    Log.d("UploadBookFragment", "Book uploaded successfully with ID: " + documentReference.getId());
                    Toast.makeText(getActivity(), "Книжка успешно загружена", Toast.LENGTH_SHORT).show();

                    // Automatically navigate to MainFragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MainFragment())
                            .addToBackStack(null)
                            .commit();
                }).addOnFailureListener(e -> {
                    Log.e("UploadBookFragment", "Error uploading book: " + e.getMessage());
                    Toast.makeText(getActivity(), "Error uploading book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Log.e("UploadBookFragment", "Error getting download URL: " + e.getMessage());
                Toast.makeText(getActivity(), "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e("UploadBookFragment", "Error uploading PDF: " + e.getMessage());
            Toast.makeText(getActivity(), "Error uploading PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_PDF && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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
