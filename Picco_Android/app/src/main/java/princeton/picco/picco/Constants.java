package princeton.picco.picco;

/**
 * Created by yfu on 4/23/16.
 */

/**
 * API Keys, Client Ids and Audience Ids for accessing APIs and configuring
 * Cloud Endpoints. When you deploy your solution, you need to use your own API
 * Keys and IDs. Please update config.gradle to define them.
 */
public final class Constants {

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    static final String SENDER_ID = BuildConfig.SENDER_ID;

    /**
     * Web client ID from Google Cloud console.
     */
    static final String WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID;

    /**
     * The web client ID from Google Cloud Console.
     */
    static final String AUDIENCE_ANDROID_CLIENT_ID =
            "server:client_id:" + WEB_CLIENT_ID;

    /**
     * The URL to the API. Default when running locally on your computer:
     * "http://10.0.2.2:8080/_ah/api/"
     */
    public static final String ROOT_URL = BuildConfig.ROOT_URL;

    /**
     * Defines whether authentication is required or not.
     */
    public static final boolean SIGN_IN_REQUIRED = BuildConfig.SIGN_IN_REQUIRED;


    /**
     * Default constructor, never called.
     */
    private Constants() { }
}
