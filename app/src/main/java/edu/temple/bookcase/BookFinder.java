package edu.temple.bookcase;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class BookFinder extends AsyncTask {
    String message;
    ArrayList<Book> bookList;

    private void getPage(){
        URL url = null;
        try {
            url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
            BufferedReader reader = null;

            reader = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder builder = new StringBuilder();
            String response;

            response = reader.readLine();
            while (response != null) {
                builder.append(response);
                response = reader.readLine();
            }

            this.message = builder.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parsePage(){
        try{
            Log.d("MyApplication", "Obtaining booklist");
            // obtain json api and convert to json array
            JSONArray webPage = new JSONArray(this.message);

            // instantiate array
            this.bookList = new ArrayList<>(webPage.length());

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
                bookList.add(new Book(id, title, author, published, cover_url));

            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.d("MyApplication", "Async Task Started");
        Log.d("MyApplication", "Getting Page");
        this.getPage();

        Log.d("MyApplication", "Page obtained obtained");

        Log.d("MyApplication", "Parsing Page");
        this.parsePage();
        Log.d("MyApplication", "Page Parsed");

        Log.d("MyApplication", "booklist obtained");

        return this.bookList;
    }
}
