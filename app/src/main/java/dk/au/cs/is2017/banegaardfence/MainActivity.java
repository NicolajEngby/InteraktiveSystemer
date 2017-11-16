package dk.au.cs.is2017.banegaardfence;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private FloatingActionButton fab;
    public static final String TAG = "IT2-g08";
    public static final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        populateListView();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.textview, returnlist);
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
    public void onResume(){
        super.onResume();
        populateListView();
    }

    public void buttonClicked(View view) {
        startActivity(new Intent(MainActivity.this, NewGeofence.class));
    }
}
