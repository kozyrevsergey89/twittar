package lohika.com.twittar;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

public class TwittarApp extends MultiDexApplication {
  public static TwittarApp get(Context context) {
    return (TwittarApp) context.getApplicationContext();
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @Override public void onCreate() {
    super.onCreate();

    TwitterConfig config = new TwitterConfig.Builder(this)
        .logger(new DefaultLogger(Log.DEBUG))
        .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString
            (R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
        .debug(true)
        .build();
    Twitter.initialize(config);
  }

  public static TwitterAuthToken getTwitterAuthToken() {
    TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

    return session != null ? session.getAuthToken() : null;
    //TwitterAuthToken authToken = session.getAuthToken();
    //String token = authToken.token;
    //String secret = authToken.secret;
  }
}
