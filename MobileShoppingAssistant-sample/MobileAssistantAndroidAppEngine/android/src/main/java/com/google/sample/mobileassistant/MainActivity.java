/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.mobileassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.sample.mobileassistantbackend.shoppingAssistant.ShoppingAssistant;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.CheckIn;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.PlaceInfo;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.PlaceInfoCollection;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.ProductCollection;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

//import com.google.sample.mobileassistantbackend
//        .shoppingAssistant.model.Product;
//import com.google.sample.mobileassistantbackend
//        .shoppingAssistant.model.HistoryItem;

//import android.widget.SearchView;



/**
 * Mobile Shopping Assistant Main Activity. Launched after the application
 * starts. Allows retrieving  the list of places (e.g., stores) and selecting a
 * place / checking into a place and navigating to another activities. Also
 * responsible for integration with Google Play Services and Google Accounts
 * for OAuth2 authentication.
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, SearchView.OnQueryTextListener {
    /**
     * Time limit for the application to wait on a response from Play Services.
     */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Tag used on log messages.
     */
    static final String TAG = MainActivity.class.getSimpleName();

    /**
     * * Log output.
     */
    private static final Logger LOG = Logger
            .getLogger(MainActivity.class.getName());

    /**
     * Name of the key for the shared preferences to access the current device
     * registration id for GCM.
     */
    private static final String PROPERTY_REG_ID = "registrationId";

    /**
     * Name of the key for the shared preferences to access the current
     * application version, to see if GCM registration id needs to be updated.
     */
    private static final String PROPERTY_APP_VERSION = "appVersion";

    /**
     * The class automatically generated by the Google App Engine backend
     * project, used to access the API easily from the Android application.
     */
    private ShoppingAssistant shoppingAssistantAPI;


    private SwipeRefreshLayout swipeLayout;

    /**
     * ListView containing the places.
     */
    private ListView placesList;

    /**
     * ListView containing the label for the places.
     */
    private TextView placesListLabel;

    /**
     * The list of places.
     */
    private List<PlaceInfo> places = null;

    /**
     * Helper class to get the user location.
     */
    private GeoLocationHelper geoLocationHelper = new GeoLocationHelper();

    /**
     * Event handler invoked when user clicks on an item in the list of nearby
     * places. It asynchronously checks the user into the selected place and
     * navigates to the activity that will present details about this place.
     */
    private OnItemClickListener placesListClickListener
            = new OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> arg0, final View arg1,
                                final int arg2, final long arg3) {
            PlaceInfo selectedPlace = places.get((int) arg3);

            new CheckInTask().execute(selectedPlace);

            PlaceDetailsActivity.setCurrentPlace(selectedPlace);
            Intent i = new Intent(MainActivity.this,
                    PlaceDetailsActivity.class);
            startActivity(i);
        }
    };

    /**
     * Google Cloud Messaging API.
     */
    private GoogleCloudMessaging gcm;

    /**
     * The application context.
     */
    private Context context;

    /**
     * The registration ID.
     */
    private String regId;

    /**
     * Returns the application version.
     *
     * @param context the Application context.
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Initializes the activity content, binds relevant widgets, sets up
     * geo-location retrieval, registers with Google Cloud Messaging (GCM)
     * and starts asynchronously retrieving the list of nearby places.
     */
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);


        //TessBaseAPI baseApi = new TessBaseAPI();
// DATA_PATH = Path to the storage
// lang = for which the language data exists, usually "eng"
        ////baseApi.init(DATA_PATH, lang);
// Eg. baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata", "eng");
        ////baseApi.setImage(bitmap);
        //String recognizedText = baseApi.getUTF8Text();
        //baseApi.end();

        // Handle to the GAE endpoints in the backend
        shoppingAssistantAPI = CloudEndpointBuilderHelper.getEndpoints();

        context = getApplicationContext();

        new CheckInTask().execute();

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

//        View root_view =  inflater.inflate(R.layout.activity_main, container, false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (mDrawerLayout != null) {
            // Set a custom shadow that overlays the main content when the drawer opens
//            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            // Enable ActionBar app icon to behave as action to toggle nav drawer
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }


        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        onRetrievePlaces(getWindow().getDecorView().getRootView());
                    }
                }, 1500);

            }

        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        placesList = (ListView) findViewById(R.id.PlacesList);
        placesListLabel = (TextView) findViewById(R.id.PlacesListLabel);
        placesList.setOnItemClickListener(placesListClickListener);

        geoLocationHelper.startRetrievingLocation(this);

        // Check device for Play Services APK. If check succeeds, proceed
        // with GCM registration.
        /*
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                Log.i(TAG, "Not registered with GCM.");

                // Register GCM id in the background
                new GcmAsyncRegister().execute();

            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        */
        // start retrieving the list of nearby places
        new ListOfPlacesAsyncRetriever()
                .execute(geoLocationHelper.getCurrentLocation());
    }

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
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                Intent intent2 = new Intent(this, BarcodeActivity.class);
                startActivity(intent2);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
//                OCRActivity
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                Intent intent5 = new Intent(this, HistoryActivity.class);
                startActivity(intent5);
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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Event handler for Button widget that allows the user to refresh the list
     * of nearby places.
     *
     * @param view the ListView to put the places.
     */
    public final void onRetrievePlaces(final View view) {
        if (geoLocationHelper.getCurrentLocation() == null) {
            // Optional - you could improve this by asking the user to turn on
            // location, but most real users will have some form of location
            // turned on.  Except on the Emulator.
            Log.i(TAG, "Location service not available.");
        }
        new ListOfPlacesAsyncRetriever()
                .execute(geoLocationHelper.getCurrentLocation());
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.menu_signOut) {
//            SignInActivity.onSignOut(this);
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Stops retrieving geo-location updates when the activity is no longer
     * visible.
     */
    @Override
    protected final void onStop() {
        super.onStop();
        geoLocationHelper.stopRetrievingLocation();
    }

    /**
     * Resumes retrieving geo-location updates when the activity is restarted.
     */
    @Override
    protected final void onRestart() {
        super.onRestart();
        geoLocationHelper.startRetrievingLocation(this);
    }

    /**
     * Checks if Google Play Services are installed and if not it initializes
     * opening the dialog to allow user to install Google Play Services.
     *
     * @return a boolean indicating if the Google Play Services are available.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @param applicationContext the Application context.
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(final Context applicationContext) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs
                .getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param applicationContext application's context.
     * @param registrationId     registration ID
     */
    private void storeRegistrationId(final Context applicationContext,
                                     final String registrationId) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        int appVersion = getAppVersion(applicationContext);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * @param applicationContext the Application context.
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(final Context
                                                        applicationContext) {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this
     * demo since the device sends upstream messages to a server that echoes
     * back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        try {
            shoppingAssistantAPI.registrations()
                    .registerDevice(regId).execute();
            // Persist the registration ID - no need to register again.
            storeRegistrationId(context, regId);
        } catch (IOException e) {
            LOG.warning("Exception when sending registration ID to the "
                    + "backend = " + e.getMessage());
            // If there is an error, we will try again to register the
            // device with GCM the next time the MainActivity starts.
        }
    }

    /**
     * AsyncTask for retrieving the list of nearby places (e.g., stores) and
     * updating the corresponding ListView and label.
     */
    private class ListOfPlacesAsyncRetriever
            extends AsyncTask<Location, Void, PlaceInfoCollection> {

        /**
         * Updates UI to indicate that the list of nearby places is being
         * retrieved.
         */
        @Override
        protected void onPreExecute() {
            placesListLabel.setText(R.string.retrievingPlaces);
            MainActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        /**
         * Updates UI to indicate that retrieval of the list of nearby places
         * completed successfully or failed.
         */
        @Override
        protected void onPostExecute(final PlaceInfoCollection result) {
            MainActivity.this.setProgressBarIndeterminateVisibility(false);

            if (result == null || result.getItems() == null
                    || result.getItems().size() < 1) {
                if (result == null) {
                    placesListLabel.setText(R.string.failedToRetrievePlaces);
                } else {
                    placesListLabel.setText(R.string.noPlacesNearby);
                }

                placesList.setAdapter(null);
                return;
            }

            placesListLabel.setText(R.string.nearbyPlaces);

            ListAdapter placesListAdapter = createPlaceListAdapter(
                    result.getItems());
            placesList.setAdapter(placesListAdapter);

            places = result.getItems();
        }

        /**
         * Creates ListAdapter populated with the list of nearby places.
         *
         * @param placesRetrieved the list of places to put in the adapter.
         * @return an adapter populated with the list of nearby places.
         */
        private ListAdapter createPlaceListAdapter(final List<PlaceInfo>
                                                           placesRetrieved) {
            final double kilometersInAMile = 1.60934;
            List<Map<String, Object>> data = new ArrayList<>();
            for (PlaceInfo place : placesRetrieved) {
                Map<String, Object> map = new HashMap<>();
                map.put("placeIcon", R.drawable.ic_shopping_cart_black_48dp);
                map.put("placeName", place.getName());
                map.put("placeAddress", place.getAddress());
                String distance = String.format(
                        getString(R.string.distance),
                        place.getDistanceInKilometers(),
                        place.getDistanceInKilometers() / kilometersInAMile);
                map.put("placeDistance", distance);
                data.add(map);
            }

            return new SimpleAdapter(MainActivity.this, data,
                    R.layout.place_item,
                    new String[]{"placeIcon", "placeName", "placeAddress",
                            "placeDistance"},
                    new int[]{R.id.place_Icon, R.id.place_name,
                            R.id.place_address,
                            R.id.place_distance});
        }


        /**
         * Retrieves the list of nearby places through appropriate
         * CloudEndpoint.
         *
         * @param params the current geolocation for which to retrieve the list
         *               of nearby places.
         * @return the collection of retrieved nearby places.
         */
        @Override
        protected PlaceInfoCollection doInBackground(final Location... params) {
            Location checkInLocation = params[0];

            float longitude;
            float latitude;

            if (checkInLocation == null) {
                // This is used to easily simulate a location in the emulator
                // when developing. For real devices deployment,
                // this temporary code should be removed and the function
                // should just return null.
                longitude = BuildConfig.DUMMY_LONGITUDE;
                latitude = BuildConfig.DUMMY_LATITUDE;
                // return null;
            } else {
                latitude = (float) checkInLocation.getLatitude();
                longitude = (float) checkInLocation.getLongitude();
            }

            PlaceInfoCollection result;

            // Retrieve the list of up to 10 places within 50 kms
            try {

                final long distanceInKm = 50;
                final int count = 10;

                result = shoppingAssistantAPI.places().getPlaces(
                        Float.toString(longitude), Float.toString(latitude),
                        distanceInKm, count)
                        .execute();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                LOG.severe("Exception=" + message);
                result = null;
            }
            return result;
        }
    }

    /**
     * AsyncTask for calling Mobile Assistant API for checking into a place
     * (e.g., a store).
     */
    private class CheckInTask extends AsyncTask<PlaceInfo, Void, Void> {

        /**
         * Calls appropriate CloudEndpoint to indicate that user checked into a
         * place.
         *
         * @param params the place where the user is checking in.
         */
        @Override
        protected Void doInBackground(final PlaceInfo... params) {

            CheckIn checkin = new CheckIn();
            checkin.setPlaceId("StoreOfy");

            try {
                shoppingAssistantAPI.checkins().insertCheckIn(checkin)
                        .execute();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                LOG.warning("Exception when checking in =" + message);
            }
            return null;
        }
    }

    /**
     * AsyncTask for registering the device with GCM in the background.
     */
    private class GcmAsyncRegister
            extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(final Void... params) {
            String msg;
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regId = gcm.register(Constants.SENDER_ID);
                msg = String.format(
                        getString(R.string.gcmRegistrationSuccess), regId);

                sendRegistrationIdToBackend();
            } catch (IOException ex) {
                LOG.warning("Exception when registering device with GCM ="
                        + ex.getMessage());
                // If there is an error, we will try again to register the
                // device with GCM the next time the MainActivity starts.
                msg = getString(R.string.gcmRegistrationError);
            }
            return msg;
        }

        @Override
        protected void onPostExecute(final String msg) {
            Toast toast = Toast
                    .makeText(getApplicationContext(),
                            msg,
                            Toast.LENGTH_LONG);
            toast.show();
            Log.i(TAG, msg);
        }
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
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    // Bottom button functions

    public void goto_MainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goto_HistoryActivity(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void goto_BarcodeActivity(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void goto_CameraActivity(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    // Search functions

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(this);
//
//        return true;
//    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        new searchProductsAsyncRetriever().execute(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }

    private class searchProductsAsyncRetriever
            extends AsyncTask<String, Void, ProductCollection> {

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
        protected void onPostExecute(final ProductCollection result) {
        /*
        MainActivity.this.setProgressBarIndeterminateVisibility(false);

        if (result == null || result.getItems() == null
                || result.getItems().size() < 1) {
            if (result == null) {
                placesListLabel.setText(R.string.failedToRetrievePlaces);
            } else {
                placesListLabel.setText(R.string.noPlacesNearby);
            }

            placesList.setAdapter(null);
            return;
        }

        placesListLabel.setText(R.string.nearbyPlaces);

        ListAdapter placesListAdapter = createPlaceListAdapter(
                result.getItems());
        placesList.setAdapter(placesListAdapter);

        places = result.getItems();
        */
        }


        @Override
        protected ProductCollection doInBackground(final String... params) {
            String query = params[0];
            ProductCollection result;
            if (places == null) {
                return null;
            }
            List<Long> storeid_list = new ArrayList<Long>();
            for (PlaceInfo place_cur : places) {
                storeid_list.add(place_cur.getPlaceId());
            }
            try {
                result = shoppingAssistantAPI.products().searchProducts(query, storeid_list).execute();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                LOG.warning("Exception when checking in =" + message);
                result = null;

            }
            return result;
        }
    }
}
