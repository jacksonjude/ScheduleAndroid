package net.moddity.droidnubekit;

/**
 * Created by jaume on 11/6/15.
 */
public class DroidNubeKitConstants {
    /** Current CloudKit endpoint */
    public final static String API_ENDPOINT = "https://api.apple-cloudkit.com";
    /** The current protocol of CloudKit API */
    public final static String PROTOCOL = "1";
    /** The extra key to pass on webview intent */
    public final static String WEBVIEW_REDIRECT_URL_EXTRA = "WEBVIEW_REDIRECT_URL_EXTRA";
    /** The extra key to pass pattern on the webview */
    public final static String WEBVIEW_REDIRECT_PATTERN_EXTRA = "WEBVIEW_REDIRECT_PATTERN_EXTRA";
    /** The prefix appended by CloudKit on redirect Pattern */
    public final static String WEBVIEW_REDIRECT_URL_PREFIX = "cloudkit-";
    /** The login endpoint of auth redirect */
    public final static String WEBVIEW_REDIRECT_LOGIN_ENDPOINT = "login";
    /** Shared preferences name */
    public final static String CLOUDKIT_SHARED_PREFERENCES = "CLOUDKIT_SHARED_PREFERENCES";
    /** Session key */
    public final static String CLOUDKIT_SESSION_KEY = "CK_SESSION_KEY";

    /** Comparator Values */
    public final static String EQUALS = "EQUALS";
    public final static String NOT_EQUALS = "NOT_EQUALS";
    public final static String LESS_THAN = "LESS_THAN";
    public final static String LESS_THAN_OR_EQUALS = "LESS_THAN_OR_EQUALS";
    public final static String GREATER_THAN = "GREATER_THAN";
    public final static String GREATER_THAN_OR_EQUALS = "GREATER_THAN_OR_EQUALS";
    public final static String NEAR = "NEAR";
    public final static String CONTAINS_ALL_TOKENS = "CONTAINS_ALL_TOKENS";
    public final static String IN = "IN";
    public final static String NOT_IN = "NOT_IN";
    public final static String CONTAINS_ANY_TOKENS = "CONTAINS_ANY_TOKENS";
    public final static String LIST_CONTAINS = "LIST_CONTAINS";
    public final static String NOT_LIST_CONTAINS = "NOT_LIST_CONTAINS";
    public final static String NOT_LIST_CONTAINS_ANY = "NOT_LIST_CONTAINS_ANY";
    public final static String BEGINS_WITH = "BEGINS_WITH";
    public final static String NOT_BEGINS_WITH = "NOT_BEGINS_WITH";
    public final static String LIST_MEMBER_BEGINS_WITH = "LIST_MEMBER_BEGINS_WITH";
    public final static String NOT_LIST_MEMBER_BEGINS_WITH = "NOT_LIST_MEMBER_BEGINS_WITH";
    public final static String LIST_CONTAINS_ALL = "LIST_CONTAINS_ALL";
    public final static String NOT_LIST_CONTAINS_ALL = "NOT_LIST_CONTAINS_ALL";

    /** CKValue Types */
    public final static String STRING = "STRING";
    public final static String NUMBER = "NUMBER";
    public final static String BOOLEAN = "BOOLEAN";
    public final static String REFERENCE = "REFERENCE";
    public final static String ASSET = "ASSET";

    /**
     * The different environment types available on CloudKit
     */
    public enum kEnvironmentType {
        kDevelopmentEnvironment,
        kProductionEnvironment;

        public String toString() {
            if(this == kDevelopmentEnvironment)
                return "development";
            return "production";
        }
    }

    public enum kDatabaseType {
        kPublicDatabase,
        kPrivateDatabase;

        public String toString() {
            if(this == kPrivateDatabase)
                return "private";
            return "public";
        }
    }
}
