package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListSelectedListener, BookDetailsFragment.PlayBookListener, ServiceConnection {
    BookListFragment bookListFragment;
    BookDetailsFragment bookDetailsFragment;
    PagerFragment pf;
    ArrayList<Book> bookList;
    boolean singlePane;
    FragmentManager fragmentManager;
    Button searchButton;
    EditText searchText;
    SeekBar seekBar;
    Button pauseButton;
    Button stopButton;
    Book curBook;
    boolean isBound;
    AudiobookService.MediaControlBinder mediaControlBinder;

    public static final String CUR_BOOK_KEY = "book";

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
                    int duration = book_web.getInt("duration");

                    // add our object to the booklist
                    MainActivity.this.bookList.add(new Book(id, title, author, published, cover_url, duration));
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

    // create new fragments with booklist
    protected void processFragments(){
        Log.d("MyApplication", "Creating Fragments");

        if(this.singlePane){
            // if singlepane we use pagerfragment
            Log.d("MyApplication", "single_pane");
            this.pf = PagerFragment.newInstance(this.bookList);

            Log.d("MyApplication", "initialized pf");

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, pf, "MyFragment");

            fragmentTransaction.commit();
            Log.d("MyApplication", "completed sp");
        }else{
            // if doublepane, we create booklistfragment
            Log.d("MyApplication", "Multi_pane");
            this.bookListFragment = BookListFragment.newInstance(this.bookList);

            // initialize preview book image to first book in list
            if(!this.bookList.isEmpty()) {
                this.bookDetailsFragment = BookDetailsFragment.newInstance(this.bookList.get(0));
            }else{
                this.bookDetailsFragment = BookDetailsFragment.newInstance(new Book(-1, null, null, 0, null, 0));
            }

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutLeft, this.bookListFragment, "MyFragment")
                    .replace(R.id.frameLayoutRight, this.bookDetailsFragment);

            fragmentTransaction.commit();
        }
    }

    // update the fragment rather than create a newInstance of the fragment
    protected void updateFragments(){
        Log.d("MyApplication", "Updating Fragments");

        // get the last fragment that contains the booklist
        Object unknownFragment = this.fragmentManager.findFragmentByTag("MyFragment");

        // determine what the fragment type is and update the fragment's booklist
        if(unknownFragment instanceof BookListFragment){
            // update the booklistfragment
            Log.d("MyApplication", "Updating BooklistFragment");

            // set the updated booklist for the fragment rather than reloading it
            ((BookListFragment) unknownFragment).setBookList(this.bookList);

            // initialize preview book image to first book in updated list
            if(!this.bookList.isEmpty()) {
                this.bookDetailsFragment = BookDetailsFragment.newInstance(this.bookList.get(0));
            }else{
                this.bookDetailsFragment = BookDetailsFragment.newInstance(new Book(-1, null, null, 0, null, 0));
            }

            // add the new bookdetailsfragment
            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutRight, this.bookDetailsFragment);

            fragmentTransaction.commit();
        }else{
            Log.d("MyApplication", "Updating pagerfragment");

            // update the booklist for the PagerFragment
            ((PagerFragment) unknownFragment).setBookList(this.bookList);
        }
    }



    // scrape the web data from URL
    protected void obtainWebData(final String urlString, final Boolean isUpdate){
        Thread t = new Thread() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(urlString);
                    BufferedReader reader = null;

                    // read the data
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

                    // send the data to the handler
                    handler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unbind from the service on destroy
        if(isBound) {
            unbindService(MainActivity.this);
            this.isBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load the current book if we were listening to one
        if(savedInstanceState != null){
            curBook = savedInstanceState.getParcelable(CUR_BOOK_KEY);

            if(curBook != null){
                // set the title if the book exists
                Log.d("MyApplication", "Playing " + curBook.getTitle());
                setTitle(getString(R.string.nowPlay) + " " + curBook.getTitle());
            }
        }

        // find pertinent layout objects
        this.singlePane = findViewById(R.id.frameLayoutRight) == null;
        this.fragmentManager = getSupportFragmentManager();

        if(this.fragmentManager.getFragments().isEmpty()){
            // On first run of the app, get book list from online
            Log.d("MyApplication", "On Startup");
            String urlString = "https://kamorris.com/lab/audlib/booksearch.php";

            // create new fragments
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

            // create new fragment with old booklist
            this.processFragments();
        }

        Log.d("MyApplication", "Completed Initialization");

        // find our search bar and button
        this.searchButton = findViewById(R.id.button);
        this.searchText = findViewById(R.id.bookSearch);

        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the searched text
                String searchString = MainActivity.this.searchText.getText().toString();
                Log.d("MyApplication", searchString);

                // create the search URL with search text
                String urlString;
                if(searchString == null){
                    urlString = "https://kamorris.com/lab/audlib/booksearch.php";
                }else {
                    urlString = "https://kamorris.com/lab/audlib/booksearch.php?search=" + searchString;
                }

                // obtain the data from website and update the fragment rather than create new
                obtainWebData(urlString,true);
            }
        });

        this.seekBar = findViewById(R.id.progressBar);

        new Thread(){
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, AudiobookService.class);
                startService(intent);
                bindService(intent, MainActivity.this, BIND_AUTO_CREATE);
                MainActivity.this.isBound = true;
            }
        }.start();

        // find the pause and stop buttons
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        // pause the book
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaControlBinder.pause();
            }
        });

        // stop the book
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaControlBinder.stop();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // if user sets progress, play book from new position
                if(b) {
                    Log.d("MyApplication", "Progress changed to " + String.valueOf(i));

                    // get position as a percent
                    double cur_percent = (double)i / (double)100;

                    // unscale the position based on book length
                    int position = (int)((double)curBook.getDuration() * cur_percent);

                    Log.d("MyApplication", "modified position " + String.valueOf(position));

                    // play the book
                    mediaControlBinder.seekTo(position);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
            }
        });
    }

    @Override
    public void BookListSelected(int index) {
        Log.d("MyApplication", Integer.toString(index));

        bookDetailsFragment.displayBook(bookList.get(index));
    }

    private Handler progressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            // do nothing if nothing is playing (paused for example)
            if(message.obj == null){
                Log.d("MyApplication", "Unable to update progress");
                return false;
            }

            // get the audiobook object
            AudiobookService.BookProgress bookProgress = (AudiobookService.BookProgress)message.obj;
            Log.d("MyApplication", "Setting Progress: " + String.valueOf(bookProgress.getProgress()) + " Out of " + String.valueOf(curBook.getDuration()));

            // find the percentage of the book we have listened to
            double percent = (double) bookProgress.getProgress() / (double)curBook.getDuration();

            Log.d("MyApplication", "Percent: " + String.valueOf((int)(percent*100)));

            // set the progress to the
            // new percentage
            seekBar.setProgress((int)(percent * 100));
            return false;
        }
    });

    @Override
    public void playBook(Book curBook) {
        // track the current book being played
        this.curBook = curBook;

        setTitle(getString(R.string.nowPlay) + " " + curBook.getTitle());


        // set the progress to zero
        this.seekBar.setProgress(0);

        // play the book
        mediaControlBinder.play(curBook.getId());
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // set the binder object
        mediaControlBinder = (AudiobookService.MediaControlBinder) iBinder;
        mediaControlBinder.setProgressHandler(progressHandler);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d("MyApplication", "Unbinding service");
        mediaControlBinder = null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the current book within our activity
        outState.putParcelable(CUR_BOOK_KEY, curBook);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // load the current book if we were listening to one
        if(savedInstanceState != null){
            curBook = savedInstanceState.getParcelable(CUR_BOOK_KEY);
            if(curBook != null){
                Log.d("MyApplication", "Restoring " + curBook.getTitle());
            }
        }
    }
}
