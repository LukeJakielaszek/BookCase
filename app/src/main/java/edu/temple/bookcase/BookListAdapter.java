package edu.temple.bookcase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookListAdapter extends BaseAdapter {
    ArrayList<Book> bookList;
    Context context;

    public BookListAdapter(Context context, ArrayList<Book> bookList){
        this.bookList = bookList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int i) {
        return bookList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if(view instanceof TextView){
            textView = (TextView)view;
        }else{
            textView = new TextView(context);
        }

        textView.setText(((Book)this.getItem(i)).getTitle());

        return textView;
    }
}
