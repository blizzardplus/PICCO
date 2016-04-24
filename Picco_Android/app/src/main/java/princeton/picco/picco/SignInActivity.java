package princeton.picco.picco;

import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesUtil;
        import com.google.api.client.googleapis.extensions.android.gms.auth
        .GoogleAccountCredential;

        import android.accounts.AccountManager;
        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.util.Log;

/**
 * Activity that allows the user to select the account they want to use to sign
 * in. The class also implements integration with Google Play Services and
 * Google Accounts.
 */
public class SignInActivity extends Activity {

    /**
     * Tag used on log messages.
     */
    static final String TAG = SignInActivity.class.getSimpleName();

    /**
     * Name of the key for the shared preferences to access the current
     * * signed in account.
     */
    private static final String ACCOUNT_NAME_SETTING_NAME = "accountName";

    /**
     *  Constant for startActivityForResult flow.
     */
    private static final int REQUEST_ACCOUNT_PICKER = 1;


    /**
     *  Google Account credentials manager.
     */
    private static GoogleAccountCredential credential;

    /**
     *
     * @return the google account credential manager.
     */
    public static GoogleAccountCredential getCredential() {
        return credential;
    }

    /**
     * Called to sign out the user, so user can later on select a different
     * account.
     *
     * @param activity activity that initiated the sign out.
     */
    static void onSignOut(final Activity activity) {
        SharedPreferences settings = activity
                .getSharedPreferences("MobileAssistant", 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ACCOUNT_NAME_SETTING_NAME, "");

        editor.apply();
        credential.setSelectedAccountName("");

        Intent intent = new Intent(activity, SignInActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Initializes the activity content and then navigates to the MainActivity
     * if the user is already signed in or if the app is configured to not
     * require the sign in.
     * Otherwise it initiates starting the UI for the account selection and
     * a check for Google Play Services being up to date.
     */
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        if (!Constants.SIGN_IN_REQUIRED) {
            // The app won't use authentication, just launch the main activity.
            startMainActivity();
            return;
        }


        if (isSignedIn()) {
            startMainActivity();
        } else {
            startActivityForResult(credential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
        }

    }

    /**
     * Handles the results from activities launched to select an account and to
     * install Google Play Services.
     */
    @Override
    protected final void onActivityResult(final int requestCode,
                                          final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
            default:
                if (data != null && data.getExtras() != null) {
                    String accountName = data.getExtras()
                            .getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        onSignedIn(accountName);
                        return;
                    }
                }
                // Signing in is required so display the dialog again
                startActivityForResult(credential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
                break;
        }
    }

    /**
     * Retrieves the previously used account name from the application
     * preferences and checks if  the credential object can be set to this
     * account.
     * @return a boolean indicating if the user is signed in or not
     */
    private boolean isSignedIn() {
        credential = GoogleAccountCredential.usingAudience(this,
                Constants.AUDIENCE_ANDROID_CLIENT_ID);
        SharedPreferences settings = getSharedPreferences("MobileAssistant", 0);
        String accountName = settings
                .getString(ACCOUNT_NAME_SETTING_NAME, null);
        credential.setSelectedAccountName(accountName);
        return credential.getSelectedAccount() != null;
    }

    /**
     * Called when the user selected an account. The account name is stored in
     * the application preferences and set in the credential object.
     * @param accountName the account that the user selected.
     */
    private void onSignedIn(final String accountName) {
        SharedPreferences settings = getSharedPreferences("MobileAssistant", 0);

        credential.setSelectedAccountName(accountName);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ACCOUNT_NAME_SETTING_NAME, accountName);
        editor.apply();

        startMainActivity();
    }

    /**
     * Registers the device with GCM if necessary, and then navigates to the
     * MainActivity.
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected final void onResume() {
        super.onResume();
    }


}
