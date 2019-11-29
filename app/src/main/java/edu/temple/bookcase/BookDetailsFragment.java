package edu.temple.bookcase;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    Button button;
    PlayBookListener parent;


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
        if(getArguments() != null) {
            this.book = getArguments().getParcelable(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);

        // get all items of our fragment
        titleTextView = v.findViewById(R.id.bookTitle);
        authorTextView = v.findViewById(R.id.bookAuthor);
        publishedTextView = v.findViewById(R.id.bookPublish);
        bookImageView = v.findViewById(R.id.bookImage);
        button = v.findViewById(R.id.playButton);

        if(this.book.getId() != -1) {
            // display the book if the book exists
            displayBook(this.book);
        }else{
            // if an empty book, display nothing

            // display the book title
            titleTextView.setText("");

            // display the author
            authorTextView.setText("");

            // display the publish year
            publishedTextView.setText("");

            // force the imageview to be invisible
            bookImageView.setVisibility(View.INVISIBLE);

            // make button invisible
            this.button.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    // used to display the book image bitmap
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Bitmap bm = (Bitmap)message.obj;
            BookDetailsFragment.this.bookImageView.setImageBitmap(bm);
            Log.d("MyApplication", "Handling");

            return false;
        }
    });

    // ensure parent is of your interface type
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // check if parent is correct interface
        if(!(context instanceof PlayBookListener)){
            throw new RuntimeException("ERROR: Parent does not implement BookListSelectedListener interface");
        }

        parent = (PlayBookListener) context;
    }

    public void displayBook(Book curbook){
        // update what book is being displayed
        this.book = curbook;

        // display the book title
        titleTextView.setText(book.getTitle());
        titleTextView.setTextSize(32);
        this.button.setVisibility(View.VISIBLE);

        // display the author
        authorTextView.setText(book.getAuthor());

        // display the publish year
        publishedTextView.setText(Integer.toString(book.getPublished()));

        // make playbutton visible
        bookImageView.setVisibility(View.VISIBLE);

        // read in the image through the URL and display the image
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

        // updates play click listener to check if play button was clicked
        BookDetailsFragment.this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // notifies parent of click
                Log.d("MyApplication", "Clicked " + BookDetailsFragment.this.book.getTitle());
                parent.playBook(BookDetailsFragment.this.book);
            }
        });
    }

    public interface PlayBookListener{
        void playBook(Book curBook);
    }
}
