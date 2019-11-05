package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListSelectedListener{
    BookListFragment bookListFragment;
    BookDetailsFragment bookDetailsFragment;
    ArrayList<String> bookList;
    boolean singlePane;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try{
                Log.d("MyApplication", message.obj.toString());
                JSONArray webPage = new JSONArray(message.obj.toString());

                JSONObject book_web = new JSONObject(webPage.getJSONObject(2).toString());
                Log.d("MyApplication", book_web.getString("author"));

            }catch (JSONException e){
                e.printStackTrace();
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(){
            @Override
            public void run() {
                URL url = null;

                try{
                    url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
                    BufferedReader reader = null;

                    reader = new BufferedReader(new InputStreamReader(url.openStream()));

                    StringBuilder builder = new StringBuilder();
                    String response;

                    response = reader.readLine();
                    while(response != null){
                        builder.append(response);
                        response = reader.readLine();
                    }

                    Log.d("MyApplication", builder.toString());
                    Message message = Message.obtain();
                    message.obj = builder.toString();

                    handler.sendMessage(message);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();

        Log.d("MyApplication", "Grabbing book list");

        singlePane = findViewById(R.id.frameLayoutRight) == null;

        Log.d("MyApplication", "Grabbing book list");
        String[] books = getResources().getStringArray(R.array.book_list);
        bookList = new ArrayList<>(Arrays.asList(books));


        Log.d("MyApplication", "Initialized BookList");

        if(singlePane){
            Log.d("MyApplication", "single_pane");
            PagerFragment pf = PagerFragment.newInstance(bookList);

            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, pf);

            fragmentTransaction.commit();
        }else{
            Log.d("MyApplication", "Multi_pane");
            bookListFragment = BookListFragment.newInstance(bookList);
            bookDetailsFragment = BookDetailsFragment.newInstance(bookList.get(0));

            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, bookListFragment)
                    .replace(R.id.frameLayoutRight, bookDetailsFragment);

            fragmentTransaction.commit();
        }

    }

    @Override
    public void BookListSelected(int index) {
        Log.d("MyApplication", Integer.toString(index));
        bookDetailsFragment.displayBook(bookList.get(index));
    }
}
