package com.whiterabbit.postman.oauth;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.whiterabbit.postman.R;
import com.whiterabbit.postman.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: fedepaol
 * Date: 12/23/12
 * Time: 8:53 PM
 */

/**
 * Dialog fragment that hosts the webview responsible for
 * the oauth authentication
 */
class OAuthFragment extends DialogFragment {

    private WebView webViewOauth;
    String mUrl;
    String mRedirectParam;
    OAuthReceivedInterface mReceivedInterface;
    private boolean mAuthFound;
    private boolean mUrlParsed;

    public static OAuthFragment newInstance(String url, String redirectParam, OAuthReceivedInterface receivedInterface) {
        OAuthFragment f = new OAuthFragment();
        f.setReceivedInterface(receivedInterface);
        Bundle args = new Bundle();
        args.putString("URL", url);
        args.putString("PARAM", redirectParam);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthFound = false;
        mUrl = getArguments().getString("URL");
        mRedirectParam = getArguments().getString("PARAM");
        mUrlParsed = false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.i(Constants.LOG_TAG, "Oauth dialog dismissed with no authentication");
        if(!mAuthFound){
            notifyAuthenticationFailed();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.i(Constants.LOG_TAG, "Oauth dialog dismissed with no authentication");
        if(!mAuthFound){
            notifyAuthenticationFailed();
        }
    }


    public void setReceivedInterface(OAuthReceivedInterface receivedInterface) {
        mReceivedInterface = receivedInterface;
    }

    private class MyWebViewClient extends WebViewClient {
        /**
         *
         * @param url
         * @return
         */
        private boolean parseUrl(String url){
            if(mUrlParsed){
                return true;
            }

            //this test depend of your API
            //checks if the login was successful and the access token returned
            if (url.contains(mRedirectParam + "=")) {
                //save your token
                mUrlParsed = true;
                saveAccessToken(url);
                getDialog().dismiss();
                return true;
            }else{
                return false;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(parseUrl(url)){
                return true;
            }else{
                Log.d(Constants.LOG_TAG,
                        String.format("Could not find oauth_verifier in callback url, %s , are you sure you set a callback url in your service?",
                                url));
                return false;
            }
        }

        // need to implement this because on 2.3 shouldOverrideUrlLoading is not called
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);    // Autogenerated
            parseUrl(url);
        }
    }

    private void saveAccessToken(String url) {
        if(mReceivedInterface != null){
            Uri uri=Uri.parse(url);
            String verifier = uri.getQueryParameter(mRedirectParam);
            mReceivedInterface.onAuthReceived(verifier);
            mAuthFound = true;
        }
    }

    private void notifyAuthenticationFailed(){
        if(mReceivedInterface != null){
            mReceivedInterface.onAuthFailed("Could not verify the auth token, wrong callback url");
        }
    }


    @Override
    public void onViewCreated(View arg0, Bundle arg1) {
        super.onViewCreated(arg0, arg1);
        //load the url of the oAuth login page
        webViewOauth.loadUrl(mUrl);
        mUrlParsed = false;
        //set the web client
        webViewOauth.setWebViewClient(new MyWebViewClient());
        //activates JavaScript (just in case)
        WebSettings webSettings = webViewOauth.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Retrieve the webview
        View v = inflater.inflate(R.layout.oauth_screen, container, false);
        webViewOauth = (WebView) v.findViewById(R.id.web_oauth);
        return v;
    }
}
