package jp.researchpanel.rpmemberapp;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.params.ClientPNames;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Generic http client in this project.<br>
 * Created by yiki on 13-9-10.
 */
public class RPMemberAppHttpClient {
    private static final String TAG = RPMemberAppHttpClient.class.getSimpleName();
    private static final int TIME_OUT = 20 * 1000; // ms

    private static AsyncHttpClient sClient;

    static {
        sClient = new AsyncHttpClient();
        sClient.setTimeout(TIME_OUT);
    }

    /**
     * Send request using a relative url.
     *
     * @param context
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void get(Context context, String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        Log.i(TAG, "Loading request: " + getFullUrl(context, url));

        addHeader(url);

        sClient.get(context, getFullUrl(context, url), params, responseHandler);
    }

    /**
     * Cancel all requests in given context.
     *
     * @param context
     */
    public static void cancelRequests(Context context) {
        sClient.cancelRequests(context, true);
    }

    public static void post(Context context, String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        Log.i(TAG, "Posting request: " + getFullUrl(context, url));

        addHeader(url);

        sClient.post(context, getFullUrl(context, url), params, responseHandler);
    }

    private static String getFullUrl(Context context, String url) {
        // Do nothing.. return directly.
        return url;
    }

    private static void addHeader(String url) {
        try {
            sClient.addHeader("Cookie", getCookieFromAppCookieManager(url.toString()));
            sClient.setUserAgent(System.getProperty("http.agent"));
            sClient.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param url the URL for which the cookies are requested
     * @return value the cookies as a string, using the format of the 'Cookie' HTTP request header
     * @throws java.net.MalformedURLException
     */
    public static String getCookieFromAppCookieManager(String url) throws MalformedURLException {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager == null)
            return null;
        String rawCookieHeader = null;
        URL parsedURL = new URL(url);

        // Extract Set-Cookie header value from Android app CookieManager for this URL
        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
        if (rawCookieHeader == null)
            return null;
        return rawCookieHeader;
    }

}
