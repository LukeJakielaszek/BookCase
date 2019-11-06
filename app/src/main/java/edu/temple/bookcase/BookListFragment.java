package edu.temple.bookcase;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookListFragment extends Fragment {
    private ArrayList<Book> bookList;
    private ListView listView;

    BookListSelectedListener parent;

    public static final String BOOK_KEY = "book";

    public BookListFragment() {
        // Required empty public constructor
    }

    public static BookListFragment newInstance(ArrayList<Book> bookList){
        BookListFragment bookListFragment = new BookListFragment();

        // set our book list array
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BOOK_KEY, bookList);
        bookListFragment.setArguments(bundle);

        return bookListFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(!(context instanceof BookListSelectedListener)){
            throw new RuntimeException("ERROR: Parent does not implement BookListSelectedListener interface");
        }

        parent = (BookListSelectedListener)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get our book list array
        if(getArguments() != null){
            this.bookList = getArguments().getParcelableArrayList(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);

        listView = v.findViewById(R.id.ListView);

        BookListAdapter bookListAdapter = new BookListAdapter(getContext(), this.bookList);

        listView.setAdapter(bookListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                parent.BookListSelected(i);
            }
        });

        return v;
    }

    public interface BookListSelectedListener{
        void BookListSelected(int index);
    }
}
