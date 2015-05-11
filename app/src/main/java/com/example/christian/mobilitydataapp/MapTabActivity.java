package com.example.christian.mobilitydataapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.christian.mobilitydataapp.persistence.DataCapture;
import com.example.christian.mobilitydataapp.persistence.DataCaptureDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Christian Cintrano on 8/05/15.
 *
 * Maps Activity with tabs
 */
public class MapTabActivity extends ActionBarActivity implements ActionBar.TabListener/*, MapTabFragment.OnHeadlineSelectedListener*/{

    private ViewPager viewPager;
    private android.support.v7.app.ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Map", "Log"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_map);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.fragment_container);
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        actionBar = getSupportActionBar();
        TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_settings:
                sendSettings();
                return true;
            case R.id.action_save_file:
                saveFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendSettings() {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void saveFile() {
        Toast.makeText(this, "Saving file...", Toast.LENGTH_SHORT).show();
        Log.i("DB", "Saving file...");
        FileOutputStream out = null;
        DataCaptureDAO db = new DataCaptureDAO(this);
        db.open();
        List<DataCapture> data = db.getAll();
        String fileName = "datos.txt";
        String folderName = "/mdaFolder";
        try {
            if(isExternalStorageWritable()) {
                String path = Environment.getExternalStorageDirectory().toString();
                File dir = new File(path + folderName);
                dir.mkdirs();
                File file = new File (dir, fileName);
                out = new FileOutputStream(file);
            } else {
                out = openFileOutput(fileName, Context.MODE_PRIVATE);
            }
            String head = "_id,latitude,longitude,street,stoptype,comment,date";
            out.write(head.getBytes());
            for(DataCapture dc : data) {
                out.write((String.valueOf(dc.getId()) + ",").getBytes());
                out.write((String.valueOf(dc.getLatitude()) + ",").getBytes());
                out.write((String.valueOf(dc.getLongitude()) + ",").getBytes());
                if(dc.getAddress() != null) {
                    out.write(("\"" + dc.getAddress() + "\",").getBytes());
                } else {
                    out.write(("null,").getBytes());
                }
                if(dc.getStopType() != null) {
                    out.write(("\"" + dc.getStopType() + "\",").getBytes());
                } else {
                    out.write(("null,").getBytes());
                }
                if(dc.getComment() != null) {
                    out.write(("\"" + dc.getComment() + "\",").getBytes());
                } else {
                    out.write(("null,").getBytes());
                }
                out.write(("\"" + dc.getDate() + "\"\n").getBytes());
            }
            out.flush();
            out.close();
            Log.i("DB", "File saved");
            Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                db.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {

    }

/*
    public void onArticleSelected(int position) {
        // El usuario seleccionó el encabezado de un artículo del fragmento HeadlinesFragment
        // Haz algo para mostrar ese artículo

        Fragment articleFrag = (Fragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        Log.i("----", "");
//        if (articleFrag != null) {
//            // Si el fragmento de artículos está disponible, estamos en una interfaz
//            // con dos paneles...
//
//            // Llama a un método de ArticleFragment para actualizar su contenido
//            articleFrag.updateArticleView(position);
//        } else {
//            // En caso contrario, estamos en una interfaz con un solo panel y tenemos
//            // que intercambiar fragmentos...
//
//            // Crea un fragmento y le pasa como argumento el artículo seleccionado
//            ArticleFragment newFragment = new ArticleFragment();
//            Bundle args = new Bundle();
//            args.putInt(ArticleFragment.ARG_POSITION, position);
//            newFragment.setArguments(args);
//
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//            // Reemplazamos lo que haya en la vista fragment_container con este fragmento,
//            // y añadimos la transacción a la pila de vuelta para que el usuario pueda volver
//            transaction.replace(R.id.fragment_container, newFragment);
//            transaction.addToBackStack(null);
//
//            // Aplica la transacción
//            transaction.commit();
//        }
    }
*/
}