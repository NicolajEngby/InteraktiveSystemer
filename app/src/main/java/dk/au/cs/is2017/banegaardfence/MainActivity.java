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
    private Button showMap;

    private GeofenceObjects currentObject;

    private ArrayList<String> geofences;
    private ArrayList<String> alerts;
    public static final String TAG = "IT2-g08";
    public static final int LOCATION_REQUEST_CODE = 1;
    private ArrayList<GeofenceObjects> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        //find stuff
        listView = findViewById(R.id.listView);
        address = findViewById(R.id.address);
        newAlert = findViewById(R.id.newAlert);
        currentAlert = findViewById(R.id.currentAlert);
        save = findViewById(R.id.saveNewAlert);
        delete = findViewById(R.id.deleteGeoFence);
        showMap = findViewById(R.id.showMap);
        newAlert.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);


        //initialize array to hold addresses of geofences
        geofences = new ArrayList<>();

        //listener for listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);

                //sets chosen address in textview
                address.setText(item);

                //loops through list of geofences (objects with addresses and alerts)
                //sets currentObject so we know which object we are deleting/updating
                for (GeofenceObjects geofenceObjects : currentList) {
                    if (item == geofenceObjects.getGeofenceName()) {
                        currentObject = geofenceObjects;
                    }
                }

                //displays current alert for currentObject
                currentAlert.setText(currentObject.getAlert());

                //shows field to edit alert
                newAlert.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            }
        });

        //listener for deletebutton
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentObject == null) return;
                //removes currentObject from currentList (list fetched from internal storage)
                //clears list with addresses
                currentList.remove(currentObject);
                geofences.clear();
                //updates list with addresses
                for (GeofenceObjects geofenceObjects : currentList) {
                    geofences.add(geofenceObjects.getGeofenceName());
                }

                //clears fields
                currentAlert.setText("");
                address.setText("");

                //updates listview
                updateListView();

                newAlert.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);

            }
        });

        //listener for saveButton
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentObject == null) return;
                //sets new alert for currentObject and field
                currentObject.setAlert(newAlert.getText().toString());
                currentAlert.setText(newAlert.getText().toString());

                newAlert.setVisibility(View.GONE);
            }
        });

        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapOfGeofences.class));
            }
        });
    }

    //fetches arraylist with GeofenceObjects from internal storage
    //saved in currentList which is used in other methods
    private void readFromInternalStorage() {
        ArrayList<GeofenceObjects> returnlist = new ArrayList<>();
        if (!isExternalStorageReadable()) {
            System.out.println("not readable");
        } else {
            returnlist = new ArrayList<>();
            try {
                FileInputStream fis = openFileInput("GeoFences");
                ObjectInputStream ois = new ObjectInputStream(fis);
                returnlist = (ArrayList<GeofenceObjects>) ois.readObject();
                ois.close();
                System.out.println(returnlist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentList = returnlist;
    }

    //adds addresses from GeofenceObjects to gefences-list
    private void populateGeofencesList() {
        ArrayList<GeofenceObjects> returnlist = currentList;
        geofences.clear();
        for (GeofenceObjects geofenceObjects : returnlist) {
            geofences.add(geofenceObjects.getGeofenceName());
        }
    }

    //updates listview with addresses from geofence-list
    private void updateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.textview, geofences);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    //checks if storage is readable
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

    //saves currentList to storage
    private void saveToStorage() {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("GeoFences", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(currentList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        readFromInternalStorage();
        populateGeofencesList();
        updateListView();
        System.out.println("**********");
        System.out.println(currentList);
    }

    public void buttonClicked(View view) {
        startActivity(new Intent(MainActivity.this, NewGeofence.class));
    }
}
