package ferancini.app.simplemapslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Dictionary;

public class MainActivity extends ListActivity {

    private ListView ltView;

    //Minha casa na terra natal
    private final LatLng CASA_NATAL = new LatLng(-21.137566, -42.373347);
    //Casa em Viçosa
    private final LatLng CASA_VICOSA = new LatLng(-20.758949, -42.881250);
    //DPI
    private final LatLng DPI = new LatLng(-20.7649113,-42.869167);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ltView = (ListView) findViewById(android.R.id.list);
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = l.getItemAtPosition(position).toString();
        Intent it = new Intent(this, LocationActivity.class);
        Bundle b = new Bundle();
        Log.i("LOCATION", "Item selecionado : "+item);
        if(item.equals("Minha casa na cidade natal")){
            b.putParcelable("location", CASA_NATAL);
            Log.i("LOCATION", "localização da minha casa");
        }
        else if (item.equals("Minha casa em Viçosa")){
            b.putParcelable("location", CASA_VICOSA);
        }
        else if (item.equals("Meu departamento")){
            b.putParcelable("location",DPI);
        }
        else{
            finish();
        }
        b.putString("location_name",item);
        it.putExtra("bundle", b);
        startActivity(it);
        finish();
    }
}