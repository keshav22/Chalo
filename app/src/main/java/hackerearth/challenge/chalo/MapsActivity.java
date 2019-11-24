package hackerearth.challenge.chalo;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import hackerearth.challenge.chalo.directionhelpers.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;
    public AutoCompleteTextView dataView;
    public List<String> routeName;
    List<Post> posts;
    MarkerOptions start,end;
    Polyline currentPolyline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        routeName = new ArrayList<String>();
        dataView = (AutoCompleteTextView) findViewById(R.id.data);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://mock.chalo.com:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        if(!response.isSuccessful())
                        {
                            dataView.setText("Code:" + response.code());
                            return;
                        }

                        posts = response.body();
                        String content = "";
                        for(Post post: posts)
                        {
                            content += post.getRouteName();

                            routeName.add(post.getRouteName());

                        }

                String[] rName = new String[routeName.size()];
                rName = routeName.toArray(rName);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (getApplicationContext(), android.R.layout.select_dialog_item, rName);
                dataView.setThreshold(1);
                dataView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                dataView.setText(t.getMessage());
            }
        });


        dataView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                List<Post> result = posts.stream()
                        .filter(it -> dataView.getText().toString().equals(it.getRouteName())).collect(Collectors.toList());

                try {
                    Post p = result.get(0);
                    String RouteN = p.getRouteName();
                    String[] startStopN = RouteN.split(" to ");
                    String startName = startStopN[0];
                    String endName = startStopN[1];

                    List<Stop> stop = p.getStopDataList();

                    //Toast.makeText(getApplicationContext(),p.getRouteId()+" "+p.getRouteName()+" "+p.getStopDataList(),Toast.LENGTH_SHORT).show();
                    start = new MarkerOptions().position(new LatLng(stop.get(0).getLatitute(),stop.get(0).getLongitude())).title(startName);
                    end = new MarkerOptions().position(new LatLng(stop.get(stop.size()-1).getLatitute(),stop.get(stop.size()-1).getLongitude())).title(endName);


                    onMapReady(mMap);

                }
                catch (Exception e)
                {

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.clear();
        if(start!=null) {
            mMap.addMarker(start);
            mMap.addMarker(end);
            String url = getUrl(start.getPosition(),end.getPosition(),"driving");
            new FetchURL(MapsActivity.this).execute(url,"driving");

        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyline!=null)
        {
            currentPolyline.remove();
        }
        //Toast.makeText(this,"Called",Toast.LENGTH_SHORT).show();

        currentPolyline = mMap.addPolyline((PolylineOptions)values[0]);
    }
}
