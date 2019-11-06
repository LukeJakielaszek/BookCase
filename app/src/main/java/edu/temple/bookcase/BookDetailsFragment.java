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
    Book book;
    TextView titleTextView;
    TextView authorTextView;
    TextView publishedTextView;

    public static final String BOOK_KEY = "book";

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(Book book){
        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();

        // set our default book title
        Bundle bundle = new Bundle();
        bundle.putParcelable(BOOK_KEY, book);
        bookDetailsFragment.setArguments(bundle);

        return bookDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get our book list array
        if(getArguments() != null){
            this.book = getArguments().getParcelable(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);

        titleTextView = v.findViewById(R.id.bookTitle);
        authorTextView = v.findViewById(R.id.bookAuthor);
        publishedTextView = v.findViewById(R.id.bookPublish);

        displayBook(this.book);

        return v;
    }

    public void displayBook(Book book){
        titleTextView.setText(book.getTitle());
        titleTextView.setTextSize(32);

        authorTextView.setText(book.getAuthor());

        publishedTextView.setText(Integer.toString(book.getPublished()));
    }
}
