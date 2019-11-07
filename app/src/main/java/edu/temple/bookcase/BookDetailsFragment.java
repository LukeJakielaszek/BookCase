package edu.temple.bookcase;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookDetailsFragment extends Fragment {
    Book book;
    TextView titleTextView;
    TextView authorTextView;
    TextView publishedTextView;
    ImageView bookImageView;
    String url;

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
        bookImageView = v.findViewById(R.id.bookImage);

        displayBook(this.book);

        return v;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Bitmap bm = (Bitmap)message.obj;
            BookDetailsFragment.this.bookImageView.setImageBitmap(bm);

            return false;
        }
    });

    public void displayBook(Book book){
        titleTextView.setText(book.getTitle());
        titleTextView.setTextSize(32);

        authorTextView.setText(book.getAuthor());

        publishedTextView.setText(Integer.toString(book.getPublished()));
        url = book.getCoverURL();

        new Thread() {
            @Override
            public void run() {
                try
                {
                    InputStream imageURL = (InputStream) new URL(BookDetailsFragment.this.url).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(imageURL);

                    Message message = Message.obtain();
                    message.obj = bitmap;
                    handler.sendMessage(message);
                }catch(
                        IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
