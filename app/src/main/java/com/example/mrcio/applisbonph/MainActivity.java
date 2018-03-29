package com.example.mrcio.applisbonph;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import database.DBHelper;

public class MainActivity extends AppCompatActivity {

    Bundle extras;
    int userId;

    ListView list;
    Integer[] imageId = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5
    };

    Integer[] imageRealId = {
            R.drawable.image1real,
            R.drawable.image2real,
            R.drawable.image3real,
            R.drawable.image4real,
            R.drawable.image5real
    };

    String[] eventNames = {
            "Pharma Tour",
            "Terapeutica Anti-Hipertensora",
            "Ramps",
            "5th International Iberian Biophysics Congress",
            "Decisão em Saúde"
    };

    final Context context = this;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mydb = new DBHelper(this);
        extras = getIntent().getExtras();

        final Cursor userLogin;
        userId = extras.getInt("userId");

        userLogin = mydb.getUserById(userId);
        userLogin.moveToFirst();

        MainArrayAdapter adapter = new MainArrayAdapter(this, imageId);

        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("imageId", imageRealId[+ position].intValue());
                dataBundle.putString("eventName", eventNames[+ position]);
                dataBundle.putInt("userId", userId);
                Intent eventPage = new Intent(context, EventActivity.class);
                eventPage.putExtras(dataBundle);
                startActivity(eventPage);
            }
        });

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
        if (id == R.id.action_profile) {
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("userId", userId);
            Intent profilePage = new Intent(context, ProfileActivity.class);
            profilePage.putExtras(dataBundle);
            startActivity(profilePage);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Tem a certeza que pretende sair da aplicação?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

}
