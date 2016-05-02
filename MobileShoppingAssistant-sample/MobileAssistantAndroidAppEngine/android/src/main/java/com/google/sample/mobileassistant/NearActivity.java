package com.google.sample.mobileassistant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NearActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout_near));

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=grocery stores");

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);


//        try {
//            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            if (map!=null){
//                map.getUiSettings().setCompassEnabled(true);
//                map.setTrafficEnabled(true);
//                map.setMyLocationEnabled(true);
//
//                // Move the camera instantly to defaultLatLng.
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoomLevel));
//
//                map.addMarker(new MarkerOptions().position(defaultLatLng)
//                        .title("This is the title")
//                        .snippet("This is the snippet within the InfoWindow")
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo)));
//
////                map.setOnInfoWindowClickListener(NearActivity.this);
//            }
//        }catch (NullPointerException e) {
//            e.printStackTrace();
//        }

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
                Intent intent3 = new Intent(this, OCRActivity.class);
                startActivity(intent3);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                Intent intent5 = new Intent(this, HistoryActivity.class);
                startActivity(intent5);
                break;
            case 8:
                mTitle = getString(R.string.title_section8);
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
            ((NearActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
