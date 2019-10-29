package edu.temple.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListSelectedListener{
    BookListFragment bookListFragment;
    BookDetailsFragment bookDetailsFragment;
    ArrayList<String> bookList;
    boolean singlePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MyApplication", "Grabbing book list");

        singlePane = findViewById(R.id.frameLayout2) == null;

        Log.d("MyApplication", "Grabbing book list");
        String[] books = getResources().getStringArray(R.array.book_list);
        bookList = new ArrayList<>(Arrays.asList(books));


        Log.d("MyApplication", "Initialized BookList");


        if(singlePane){
            PagerFragment pf = PagerFragment.newInstance(bookList);

            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout1, pf);

            fragmentTransaction.commit();
        }else{
            bookListFragment = BookListFragment.newInstance(bookList);
            bookDetailsFragment = BookDetailsFragment.newInstance(bookList.get(0));

            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout1, bookListFragment)
                    .replace(R.id.frameLayout2, bookDetailsFragment);

            fragmentTransaction.commit();
        }

    }

    @Override
    public void BookListSelected(int index) {
        Log.d("MyApplication", Integer.toString(index));
        bookDetailsFragment.displayBook(bookList.get(index));
    }
}
