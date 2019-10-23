package edu.temple.bookcase;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookDetailsFragment extends Fragment {
    String bookTitle;
    TextView textView;

    public static final String BOOK_TITLE_KEY = "book_title";

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(String title){
        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        // set our default book title
        Bundle bundle = new Bundle();
        bundle.putString(BOOK_TITLE_KEY, title);
        bookDetailsFragment.setArguments(bundle);

        return bookDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get our book list array
        if(getArguments() != null){
            this.bookTitle = getArguments().getString(BOOK_TITLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);

        textView = v.findViewById(R.id.textView);
        textView.setText(this.bookTitle);
        textView.setTextSize(32);


        return v;
    }

    public void displayBook(String bookTitle){
        textView.setText(bookTitle);
        textView.setTextSize(32);
    }
}
