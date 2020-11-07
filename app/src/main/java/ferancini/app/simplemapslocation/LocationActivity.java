package ferancini.app.simplemapslocation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager lm;
    private Criteria criteria;
    private String provider;

    private LatLng loc;
    private String loc_name;
    private Marker locMarker;
    private Location currentLoc;
    private Marker currentLocMarker;

    //Minha casa na terra natal
    private LatLng CASA_NATAL = new LatLng(-21.137566, -42.373347);
    //Casa em Viçosa
    private LatLng CASA_VICOSA = new LatLng(-20.758949, -42.881250);
    //DPI
    private LatLng DPI = new LatLng(-20.7649113, -42.869167);

    //permissão para acessar localização
    private final int LOCATION_PERMISSION = 1;

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

        Log.i("LOCATION", "location retrieved from bundle lat:" + loc.latitude + " long:" + loc.longitude);

        configuraCriteriaLocation();
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //habilitar localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }
        else
            iniciaGPS(this);

        configMapa();
    }

    public void centralizaCamera(){
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(loc)
                        .tilt(60)
                        .zoom(19)
                        .build()
        );
        mMap.animateCamera(c);
    }

    public void configMapa(){
        //só podemos alterar o mapa se ele já tiver carregado
        if(mMap != null){
            if(loc_name.equals("Minha casa na cidade natal"))
            {
                loc = CASA_NATAL;
                mMap.setBuildingsEnabled(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
            else if (loc_name.equals("Minha casa em Viçosa")) {
                loc = CASA_VICOSA;
                mMap.setBuildingsEnabled(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
            else{
                loc = DPI;
                mMap.setBuildingsEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }

            if(locMarker != null)
                locMarker.remove();

            locMarker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(loc_name)
            );

            //move camera e centraliza no local
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

            Toast.makeText(this,"Opção selecionada : "+loc_name, Toast.LENGTH_LONG).show();

            centralizaCamera();
        }
    }

    public void onClickLocBtn(View v){
        String tag = v.getTag().toString();

        if(tag.equals("CASA_NATAL"))
            loc_name = "Minha casa na cidade natal";
        else if (tag.equals("CASA_VICOSA"))
            loc_name = "Minha casa em Viçosa";
        else
            loc_name = "Meu departamento";

        configMapa();
    }

    public void configuraCriteriaLocation(){
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        PackageManager packageManager = getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if(hasGPS){
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            Log.i("LOCATION", "localização por GPS");
        }
        else{
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            Log.i("LOCATION", "localização por WI-FI ou dados");
        }
    }

    @SuppressLint("MissingPermission")
    public void iniciaGPS(Context ctx){
        provider = lm.getBestProvider(criteria, true);

        if(provider ==null){
            Log.e("PROVIDER" , "Não foi possível encontrar um provedor");
        }
        else{
            Log.i("PROVIDER", "Provedor utilizado : "+provider);
            lm.requestLocationUpdates(provider, 5000, 0, (LocationListener) ctx);
        }

    }

    public void requestLocationPermission(){
        //verifica se precisa explicar a permissão
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        ){

            //pede permissão
            ActivityCompat.requestPermissions(this
                    ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_PERMISSION
            );

            Toast.makeText(this
                    ,"Permita o acesso a localização do dispositivo para\n" +
                            "medir a distância até o local selecionado."
                    ,Toast.LENGTH_LONG).show();

        }
        else{
            //pede permissão
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERMISSION : {
                if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    iniciaGPS(this);
                }
            }
        }
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public double getDistanciaPkmn(Marker m1, Marker m2){
        //cria location do treinador para ver distância
        Location l1 = new Location(provider);
        l1.setLatitude(m1.getPosition().latitude);
        l1.setLongitude(m1.getPosition().longitude);

        //cria location do pokemon para ver distância
        Location l2 = new Location(provider);
        l2.setLatitude(m2.getPosition().latitude);
        l2.setLongitude(m2.getPosition().longitude);

        return l1.distanceTo(l2);
    }

    public void onClickCurrentLoc(View v){

        //checa se tem permissão para acessar localização
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                !=
                PackageManager.PERMISSION_GRANTED
            &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                !=
                PackageManager.PERMISSION_GRANTED
        ){
            requestLocationPermission();
        }
        else{
            System.out.println("WHERE IS THE MARKER?!");
            if(currentLocMarker != null)
                currentLocMarker.remove();

            if(currentLoc != null){
                LatLng currentLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());

                currentLocMarker = mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("Minha localização atual")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );

                double distancia = getDistanciaPkmn(currentLocMarker,locMarker);
                DecimalFormat df = new DecimalFormat("0.##");
                Toast.makeText(this
                        ,"Distancia até  "+loc_name +" é de "+df.format(distancia) +" metros"
                        , Toast.LENGTH_LONG).show();

                CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(currentLatLng)
                                .tilt(60)
                                .zoom(19)
                                .build()
                );
                mMap.animateCamera(c);
            }

        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLoc = new Location(provider);
        currentLoc.setLatitude(location.getLatitude());
        currentLoc.setLongitude(location.getLongitude());
        Log.i("PROVIDER", "Localização atual latitude " + location.getLatitude() + " longitude" + location.getLongitude());
    }

    @Override
    protected void onDestroy() {
        lm.removeUpdates(this);
        super.onDestroy();
    }
}