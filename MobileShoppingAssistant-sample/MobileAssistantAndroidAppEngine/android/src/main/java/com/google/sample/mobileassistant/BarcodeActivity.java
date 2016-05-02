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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.repackaged.com.google.common.base.Objects;
import com.google.sample.mobileassistantbackend.shoppingAssistant.ShoppingAssistant;
import com.google.sample.mobileassistantbackend.shoppingAssistant.model.ProductCollection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
//import android.app.ActionBar;


public class BarcodeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private Button scanBtn;
    private Button item_add_button;
    private TextView formatTxt, contentTxt;
    /**
     * The class automatically generated by the Google App Engine backend
     * project, used to access the API easily from the Android application.
     */
    private ShoppingAssistant shoppingAssistantAPI;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static final Logger LOG = Logger
            .getLogger(MainActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
//        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);

        // Handle to the GAE endpoints in the backend
        shoppingAssistantAPI = CloudEndpointBuilderHelper.getEndpoints();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout_barcode));


        item_add_button = (Button) findViewById(R.id.add_item_but);
        item_add_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(BarcodeActivity.this, item_add_button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.add_item_poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
//                        item_id = new String(item.getItemId());
//                        Toast.makeText(BarcodeActivity.this,"You Clicked : " + item.getNumericShortcut(),Toast.LENGTH_SHORT).show();

                        if (Objects.equal(item.getTitle(), new String("by barcode"))){
//                            Toast.makeText(BarcodeActivity.this,"You Clicked : ").show();
                            IntentIntegrator scanIntegrator = new IntentIntegrator(BarcodeActivity.this);
                            scanIntegrator.initiateScan();
                        }
                        return true;

                    }
                });

                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method


        //        scanBtn.setOnClickListener(this);
    }


    public void goto_scanIntegrator(View v){
//        if(v.getId()==R.id.scan_button){
//            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
//            scanIntegrator.initiateScan();
//        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            List<String> barcode = Arrays.asList(scanFormat, scanContent);
            new getProductByBarcodeAsyncRetriever().execute(barcode);
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class getProductByBarcodeAsyncRetriever
            extends AsyncTask<List<String>, Void, ProductCollection> {

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
        protected ProductCollection doInBackground(final List<String>... params) {
            String barcode_format = params[0].get(0);
            String barcode_content = params[0].get(1);
            Log.i("Barcode format",barcode_format);
            Log.i("Barcode content",barcode_content);
            ProductCollection result;
            try {
                result = shoppingAssistantAPI.products().getProductByBarcode(barcode_format, barcode_content).execute();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                LOG.warning("Exception when checking in =" + message);
                result = null;

            }
            if (result != null && result.getItems() != null)
                Log.i("Barcode", "barcode result: "+result.getItems().get(0).toString());
            else
                Log.i("Barcode", "barcode resut is empty ");
            return result;
        }
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
                mTitle = getString(R.string.title_section1);
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                Intent intent3 = new Intent(this, OCRActivity.class);
                startActivity(intent3);
                break;
            case 6:
                mTitle = getString(R.string.title_section5);
                Intent intent5 = new Intent(this, HistoryActivity.class);
                startActivity(intent5);
                break;
            case 9:
                mTitle = getString(R.string.title_section8);
                Intent intent8 = new Intent(this, SettingsActivity.class);
                startActivity(intent8);
                break;
            case 10:
                mTitle = getString(R.string.title_section9);
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
            ((BarcodeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}