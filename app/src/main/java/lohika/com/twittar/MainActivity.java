package lohika.com.twittar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

  public static final int REQUEST_CODE = 100;
  public static final String TAG = MainActivity.class.getSimpleName();
  public static final String SEARCH_TAG = "#gamedev";

  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  private Button loadTweetsBtn;
  private FusedLocationProviderClient fusedLocationClient;

  public static void startClearTaskNewTask(Context context) {
    Intent intent = new Intent(context, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    // Example of a call to a native method
    TextView tv = (TextView) findViewById(R.id.sample_text);
    tv.setText(stringFromJNI());

    loadTweetsBtn = (Button) findViewById(R.id.load_tweets_btn);
    loadTweetsBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        loadTweets();
      }
    });
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //do nothing
  }

  private void loadTweets() {
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      ActivityCompat.requestPermissions(this, new String[] {
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest
              .permission.ACCESS_COARSE_LOCATION
      }, REQUEST_CODE);
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }

    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
    final SearchService searchService = twitterApiClient.getSearchService();

    fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
      @Override
      public void onSuccess(Location location) {
        // Got last known location. In some rare situations this can be null.
        if (location != null) {
          Call<Search> call = searchService.tweets(SEARCH_TAG, new Geocode(location.getLatitude(), location
              .getLongitude(),
              25, Geocode
              .Distance.KILOMETERS), null, null, null, null, null, null, null, null);
          call.enqueue(new retrofit2.Callback<Search>() {
            @Override public void onResponse(Call<Search> call, Response<Search> response) {
              updateTweets(response.body().tweets);
              Toast.makeText(MainActivity.this, "Tweets update Success", Toast.LENGTH_SHORT).show();
            }

            @Override public void onFailure(Call<Search> call, Throwable t) {
              Toast.makeText(MainActivity.this, "Tweets update Failure", Toast.LENGTH_SHORT).show();
            }
          });
        }
      }
    });

    //StatusesService statusesService = twitterApiClient.getStatusesService();
    //Call<Tweet> call = statusesService.show(524971209851543553L, null, null, null);
    //call.enqueue(new Callback<Tweet>() {
    //  @Override
    //  public void success(Result<Tweet> result) {
    //    //Do something with result
    //  }
    //
    //  public void failure(TwitterException exception) {
    //    //Do something on failure
    //  }
    //});
  }

  private void updateTweets(List<Tweet> tweets) {
    for (Tweet tweet : tweets) {
      Log.d("123", "tweet : " + tweet.text);
    }
    // TODO fill ar scene with tweets
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * A native method that is implemented by the 'native-lib' native library,
   * which is packaged with this application.
   */
  public native String stringFromJNI();
}
