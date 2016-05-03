package com.google.sample.mobileassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListAdapter;

import com.google.sample.mobileassistantbackend.shoppingAssistant.ShoppingAssistant;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.HistoryItemCollection;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.HistoryItem;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.PlaceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HistoryActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private List<HistoryItem> history = null;

    private ListView HistoryList;

    /**
     * ListView containing the label for the places.
     */
    private TextView HistoryListLabel;

    /**
     * The class automatically generated by the Google App Engine backend
     * project, used to access the API easily from the Android application.
     */
    private ShoppingAssistant shoppingAssistantAPI;

    private static final Logger LOG = Logger
            .getLogger(MainActivity.class.getName());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Handle to the GAE endpoints in the backend
        shoppingAssistantAPI = CloudEndpointBuilderHelper.getEndpoints();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout_history));

        HistoryList = (ListView) findViewById(R.id.HistoryList);
        HistoryListLabel = (TextView) findViewById(R.id.HistoryListLabel);
        HistoryList.setOnItemClickListener(HistoryListClickListener);

        new getHistoryAsyncRetriever().execute();
    }

    private AdapterView.OnItemClickListener HistoryListClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> arg0, final View arg1,
                                final int arg2, final long arg3) {
//            HistoryItem selectedHistory = history.get((int) arg3);
//
//            new CheckInTask().execute(selectedHistory);
//
//            HistoryDetailsActivity.setCurrentPlace(selectedHistory);
//            Intent i = new Intent(HistoryActivity.this,
//                    HistoryDetailsActivity.class);
//            startActivity(i);
        }
    };

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section2);
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                Intent intent2 = new Intent(this, BarcodeActivity.class);
                startActivity(intent2);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                Intent intent3 = new Intent(this, CameraActivity.class);
                startActivity(intent3);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
            case 8:
                mTitle = getString(R.string.title_section8);
                Intent intent7 = new Intent(this, NearActivity.class);
                startActivity(intent7);
                break;
            case 9:
                mTitle = getString(R.string.title_section9);
                Intent intent8 = new Intent(this, SettingsActivity.class);
                startActivity(intent8);
                break;
            case 10:
                mTitle = getString(R.string.title_section10);
                SignInActivity.onSignOut(this);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(0xff8c1a));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HistoryActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private class getHistoryAsyncRetriever
            extends AsyncTask<Void, Void, HistoryItemCollection> {

        /**
         * Updates UI to indicate that the list of nearby places is being
         * retrieved.
         */
    /*
    @Override
    protected void onPreExecute() {
        placesListLabel.setText(R.string.retrievingPlaces);
        MainActivity.this.setProgressBarIndeterminateVisibility(true);
    }
    */

        /**
         * Updates UI to indicate that retrieval of the list of nearby places
         * completed successfully or failed.
         */
        @Override
        protected void onPostExecute(final  HistoryItemCollection result) {

//        MainActivity.this.setProgressBarIndeterminateVisibility(false);

        if (result == null || result.getItems() == null
                || result.getItems().size() < 1) {
            if (result == null) {
                HistoryListLabel.setText(R.string.failedToRetrievePlaces);
            } else {
                HistoryListLabel.setText(R.string.noHistory);
            }

            HistoryList.setAdapter(null);
            return;
        }

//        placesListLabel.setText(R.string.nearbyPlaces);
//
        ListAdapter HistoryListAdapter = createHistoryListAdapter(
                result.getItems());
        HistoryList.setAdapter(HistoryListAdapter);
//
//        places = result.getItems();

//            HistoryListLabel.setText(R.string.nearbyPlaces);
            history = result.getItems();
        }


        private ListAdapter createHistoryListAdapter(final List<HistoryItem>
                                                           placesRetrieved) {
            final double kilometersInAMile = 1.60934;
            List<Map<String, Object>> data = new ArrayList<>();
            for (HistoryItem hist : placesRetrieved) {
                Map<String, Object> map = new HashMap<>();
//                map.put("placeIcon", R.drawable.ic_shopping_cart_black_48dp);
                map.put("placeName", hist.getProductName());
                map.put("placeAddress", (hist.getPurchasePrice())/100.0);
                String distance = String.format(
                        hist.getPlaceName(),
                        hist.getPurchaseDate());
                map.put("placeDistance", distance);
                data.add(map);
            }

            return new SimpleAdapter(HistoryActivity.this, data,
                    R.layout.place_item,
                    new String[]{"placeIcon", "placeName", "placeAddress",
                            "placeDistance"},
                    new int[]{R.id.place_Icon, R.id.place_name,
                            R.id.place_address,
                            R.id.place_distance});
        }

        @Override
        protected HistoryItemCollection doInBackground(Void... params) {

            HistoryItemCollection result;
            try {
                result = shoppingAssistantAPI.historyItems().getUserHistory().execute();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                LOG.warning("Exception when checking in =" + message);
                result = null;

            }
            if (result != null && result.getItems() != null)
                Log.i("History", "history result: "+result.getItems().get(0).toString());
            else
                Log.i("History", "history result is empty ");
            return result;
        }
    }
}
