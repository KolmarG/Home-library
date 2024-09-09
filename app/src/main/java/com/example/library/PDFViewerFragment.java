package com.example.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PDFViewerFragment extends Fragment {

    private static final String ARG_PDF_URL = "pdf_url";
    private static final String PREF_NAME = "pdf_viewer_prefs";
    private String pdfUrl;
    private PDFView pdfView;
    private ProgressBar progressBar;
    private Button backButton;
    private Button prevButton;
    private Button nextButton;
    private SharedPreferences sharedPreferences;

    public static PDFViewerFragment newInstance(String pdfUrl) {
        PDFViewerFragment fragment = new PDFViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_URL, pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pdfUrl = getArguments().getString(ARG_PDF_URL);
            Log.d("PDFViewerFragment", "PDF URL: " + pdfUrl);
        }
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);

        pdfView = view.findViewById(R.id.pdfView);
        progressBar = view.findViewById(R.id.progressBar);
        backButton = view.findViewById(R.id.backButton);
        prevButton = view.findViewById(R.id.prevButton);
        nextButton = view.findViewById(R.id.nextButton);

        backButton.setOnClickListener(v -> {
            MainFragment mainFragment = new MainFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .addToBackStack(null)
                    .commit();
        });

        prevButton.setOnClickListener(v -> {
            if (pdfView.getCurrentPage() > 0) {
                pdfView.jumpTo(pdfView.getCurrentPage() - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (pdfView.getCurrentPage() < pdfView.getPageCount() - 1) {
                pdfView.jumpTo(pdfView.getCurrentPage() + 1);
            }
        });

        downloadAndOpenPdf();

        return view;
    }

    private void downloadAndOpenPdf() {
        progressBar.setVisibility(View.VISIBLE);

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        final File localFile;
        try {
            localFile = File.createTempFile("temp_pdf", ".pdf", getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating temp file", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("PDFViewerFragment", "PDF downloaded successfully");
                openPdf(localFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("PDFViewerFragment", "Error downloading PDF: " + exception.getMessage());
                Toast.makeText(getContext(), "Error downloading PDF", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void openPdf(File file) {
        int lastPage = getLastPageNumber(pdfUrl);
        pdfView.fromFile(file)
                .defaultPage(lastPage)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Log.d("PDFViewerFragment", "PDF loaded with " + nbPages + " pages");
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("PDFViewerFragment", "Error loading PDF: " + t.getMessage());
                        Toast.makeText(getContext(), "Error loading PDF", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .onPageChange(new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        saveCurrentPageNumber(pdfUrl, page);
                    }
                })
                .load();
    }

    private int getLastPageNumber(String pdfUrl) {
        return sharedPreferences.getInt(pdfUrl, 0);
    }

    private void saveCurrentPageNumber(String pdfUrl, int pageNumber) {
        sharedPreferences.edit().putInt(pdfUrl, pageNumber).apply();
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
