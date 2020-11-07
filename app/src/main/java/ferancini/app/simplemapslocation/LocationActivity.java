package ferancini.app.simplemapslocation;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LatLng loc;
    private String loc_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Intent it = getIntent();
        Bundle b = it.getBundleExtra("bundle");
        loc = b.getParcelable("location");
        loc_name = b.getString("location_name");

        Log.i("LOCATION", "location retrieved from bundle lat:"+loc.latitude + " long:" + loc.longitude);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                    .position(loc)
                .title(loc_name)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));


        //função do java 8
        //espera 2.5 segundos antes de animar a centralização da câmera
        (new Handler()).postDelayed(this::centralizaCamera, 2500);
    }

    public void centralizaCamera(){
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(loc)
                        .tilt(60)
                        .zoom(18)
                        .build()
        );
        mMap.animateCamera(c);
    }

}