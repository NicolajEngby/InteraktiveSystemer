package dk.au.cs.is2017.banegaardfence;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private FloatingActionButton fab;
    private ListView listView;
    private TextView address;
    private EditText newAlert;
    private TextView currentAlert;
    private Button save;
    private Button delete;
    private int currentGeofence;
    private ArrayList<String> geofences;
    private ArrayList<String> alerts;
    public static final String TAG = "IT2-g08";
    public static final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        listView = findViewById(R.id.listView);
        address = findViewById(R.id.address);
        newAlert = findViewById(R.id.newAlert);
        currentAlert = findViewById(R.id.currentAlert);
        save = findViewById(R.id.saveNewAlert);
        delete = findViewById(R.id.deleteGeoFence);
        populateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                address.setText(item);
                currentGeofence = i;
                if(alerts.isEmpty()) return;
                currentAlert.setText(alerts.get(i));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geofences.remove(currentGeofence);
                updateListView();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAlert.setText(newAlert.getText().toString());
            }
        });
    }

    private void populateListView() {
        if (!isExternalStorageReadable()) {
            System.out.println("not readable");
            return;
        }
        ArrayList<String> returnlist = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("GeoFences");
            ObjectInputStream ois = new ObjectInputStream(fis);
            returnlist = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        geofences = returnlist;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.textview, returnlist);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    private void updateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.textview, geofences);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveToStorage();
    }

    private void saveToStorage() {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("GeoFences", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(geofences);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        populateListView();
    }

    public void buttonClicked(View view) {
        startActivity(new Intent(MainActivity.this, NewGeofence.class));
    }
}
