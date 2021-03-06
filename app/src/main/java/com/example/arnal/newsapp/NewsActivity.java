package com.example.arnal.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.content.Loader;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = NewsActivity.class.getName();


    /*ListView global variable*/
    private ListView newsListView;
    //Adapter for list of books
    private NewsAdapter mAdapter;
    /**
     * +     * Constant value for the news loader ID. We can choose any integer.
     * +     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;
    /**
     * URL for News data from the theguardion.com
     */
    private String NEWS_URL = "https://content.guardianapis.com/search?tag=environment/recycling&api-key=885e9fac-b6a5-4f2d-a82c-dff17f60052a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Find a reference to the {@link ListView} in the layout
        newsListView = (ListView) findViewById(R.id.list);
        // Create a new {@link ArrayAdapter} of news
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(mAdapter);
        mEmptyStateTextView = (TextView) findViewById(R.id.text);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            android.app.LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            //check if there's already a loader
            if(getLoaderManager().getLoader(NEWS_LOADER_ID).isStarted()){
                //restart it if there's one
                getLoaderManager().restartLoader(NEWS_LOADER_ID,null,this);
            }
        }else{
            //Otherwise,display error
            //First hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        mAdapter.clear();
        newsListView.setAdapter(mAdapter);
        newsListView.setEmptyView(mEmptyStateTextView);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = mAdapter.getItem(position);

               try {
                   Uri newsUri = Uri.parse(currentNews.getUrl());

                   // Create new intent to view earhquake web site
                   Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                   //Send the intent to launch a new activity
                   startActivity(websiteIntent);
               }catch (Exception e){
                   String data = e.getMessage();
               }
               }
        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, NEWS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous book data
        mAdapter.clear();
        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
            mAdapter.notifyDataSetChanged();
        }
        Log.v("Loader State","on Load Finished");

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
