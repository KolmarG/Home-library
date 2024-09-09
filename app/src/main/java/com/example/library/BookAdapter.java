package com.example.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private SharedPreferences sharedPreferences;

    public BookAdapter(Context context, List<Book> bookList) {
        this.bookList = bookList;
        this.sharedPreferences = context.getSharedPreferences("pdf_viewer_prefs", Context.MODE_PRIVATE);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.title);
        holder.itemView.setOnClickListener(v -> {
            // Open the PDF viewer fragment
            Log.d("BookAdapter", "Opening PDF: " + book.url);
            PDFViewerFragment pdfViewerFragment = PDFViewerFragment.newInstance(book.url);
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, pdfViewerFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Display the last page number for the book
        int lastPage = sharedPreferences.getInt(book.url, 0);
        holder.pageTextView.setText("Последняя страница: " + lastPage);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setFilteredList(List<Book> filteredList) {
        bookList = filteredList;
        notifyDataSetChanged();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView pageTextView;

        public BookViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            pageTextView = itemView.findViewById(R.id.pageTextView);
        }
    }
}
