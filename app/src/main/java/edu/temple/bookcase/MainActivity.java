package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    PagerFragment pf;
    ArrayList<Book> bookList;
    boolean singlePane;
    FragmentManager fragmentManager;
    Button searchButton;
    EditText searchText;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            ArrayList list = (ArrayList) message.obj;
            boolean isUpdate = (boolean)list.get(0);

            try{
                Log.d("MyApplication", "Obtaining booklist");

                // obtain json api and convert to json array
                JSONArray webPage = new JSONArray((String)list.get(1));
                // instantiate array
                MainActivity.this.bookList = new ArrayList<>(webPage.length());

                // get contents of each object in array
                for(int i = 0; i < webPage.length(); i++){
                    // get the individual book contents
                    JSONObject book_web = new JSONObject(webPage.getJSONObject(i).toString());

                    // parse book information
                    int id = book_web.getInt("book_id");
                    String title = book_web.getString("title");
                    String author = book_web.getString("author");
                    int published = book_web.getInt("published");
                    String cover_url = book_web.getString("cover_url");

                    // add our object to the booklist
                    MainActivity.this.bookList.add(new Book(id, title, author, published, cover_url));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            Log.d("MyApplication", message.obj.toString());

            if(isUpdate){
                MainActivity.this.updateFragments();
            }else {
                MainActivity.this.processFragments();
            }
            Log.d("MyApplication", "Completed web update");

            return false;
        }
    });

    protected void processFragments(){
        Log.d("MyApplication", "Creating Fragments");

        if(this.singlePane){
            Log.d("MyApplication", "single_pane");
            this.pf = PagerFragment.newInstance(this.bookList);

            Log.d("MyApplication", "initialized pf");

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, pf, "MyFragment");

            fragmentTransaction.commit();
            Log.d("MyApplication", "completed sp");
        }else{
            Log.d("MyApplication", "Multi_pane");
            this.bookListFragment = BookListFragment.newInstance(this.bookList);

            // initialize preview book image to first book in list
            if(!this.bookList.isEmpty()) {
                this.bookDetailsFragment = BookDetailsFragment.newInstance(this.bookList.get(0));
            }

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, this.bookListFragment, "MyFragment")
                    .replace(R.id.frameLayoutRight, this.bookDetailsFragment);

            fragmentTransaction.commit();
        }
    }

    protected void updateFragments(){
        Log.d("MyApplication", "Updating Fragments");

        // get the last fragment that contains the booklist
        Object unknownFragment = this.fragmentManager.findFragmentByTag("MyFragment");

        // determine what the fragment type is and call the corresponding fetch method
        if(unknownFragment instanceof BookListFragment){
            Log.d("MyApplication", "Updating BooklistFragment");
            ((BookListFragment) unknownFragment).setBookList(this.bookList);

            // initialize preview book image to first book in list
            if(!this.bookList.isEmpty()) {
                this.bookDetailsFragment = BookDetailsFragment.newInstance(this.bookList.get(0));
            }

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutRight, this.bookDetailsFragment);

            fragmentTransaction.commit();
        }else{
            Log.d("MyApplication", "Updating pagerfragment");
            ((PagerFragment) unknownFragment).setBookList(this.bookList);
        }

        /*
        if(this.singlePane){
            Log.d("MyApplication", "single_pane");
            this.pf = PagerFragment.newInstance(this.bookList);

            Log.d("MyApplication", "initialized pf");

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, pf, "MyFragment");

            fragmentTransaction.commit();
            Log.d("MyApplication", "completed sp");
        }else{
            Log.d("MyApplication", "Multi_pane");
            this.bookListFragment = BookListFragment.newInstance(this.bookList);


            // initialize preview book image to first book in list
            if(!this.bookList.isEmpty()) {
                this.bookDetailsFragment = BookDetailsFragment.newInstance(this.bookList.get(0));
            }

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, this.bookListFragment, "MyFragment")
                    .replace(R.id.frameLayoutRight, this.bookDetailsFragment);

            fragmentTransaction.commit();
        }
         */
    }

    protected void obtainWebData(final String urlString, final Boolean isUpdate){
        Thread t = new Thread() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(urlString);
                    BufferedReader reader = null;

                    reader = new BufferedReader(new InputStreamReader(url.openStream()));

                    StringBuilder builder = new StringBuilder();
                    String response;

                    response = reader.readLine();
                    while (response != null) {
                        builder.append(response);
                        response = reader.readLine();
                    }

                    Message message = Message.obtain();

                    // create a message indicating whether to update or recreate
                    ArrayList list = new ArrayList();
                    list.add(isUpdate);
                    list.add(builder.toString());
                    message.obj = list;

                    handler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find pertinent layout objects
        this.singlePane = findViewById(R.id.frameLayoutRight) == null;
        this.fragmentManager = getSupportFragmentManager();

        if(this.fragmentManager.getFragments().isEmpty() == true){
            // On first run of the app, get book list from online
            Log.d("MyApplication", "On Startup");
            String urlString = "https://kamorris.com/lab/audlib/booksearch.php";
            obtainWebData(urlString, false);
        }else{
            // for subsequent runs, we use the fetched fragments stored in our fragment manager's fragment
            Log.d("MyApplication", "After Startup");

            // get the last fragment that contains the booklist
            Object unknownFragment = this.fragmentManager.findFragmentByTag("MyFragment");

            // determine what the fragment type is and call the corresponding fetch method
            if(unknownFragment instanceof BookListFragment){
                this.bookList = ((BookListFragment) unknownFragment).fetch();
            }else{
                this.bookList = ((PagerFragment) unknownFragment).fetch();
            }

            // update our layout
            this.processFragments();
        }

        Log.d("MyApplication", "Completed Initialization");

        // find our search bar and button
        this.searchButton = findViewById(R.id.button);
        this.searchText = findViewById(R.id.bookSearch);

        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = MainActivity.this.searchText.getText().toString();
                Log.d("MyApplication", searchString);

                String urlString;
                if(searchString == null){
                    urlString = "https://kamorris.com/lab/audlib/booksearch.php";
                }else {
                    urlString = "https://kamorris.com/lab/audlib/booksearch.php?search=" + searchString;
                }

                obtainWebData(urlString,true);
            }
        });
    }

    @Override
    public void BookListSelected(int index) {
        Log.d("MyApplication", Integer.toString(index));

        bookDetailsFragment.displayBook(bookList.get(index));
    }
}
