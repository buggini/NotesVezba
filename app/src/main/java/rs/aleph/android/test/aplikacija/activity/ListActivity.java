package rs.aleph.android.test.aplikacija.activity;


import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import rs.aleph.android.test.R;
import rs.aleph.android.test.aplikacija.db.ORMLightHelper;
import rs.aleph.android.test.aplikacija.db.model.Notes;
import rs.aleph.android.test.aplikacija.dilog.AboutDialog;
import rs.aleph.android.test.aplikacija.preferences.Prefererences;

public class ListActivity extends AppCompatActivity {

    private ORMLightHelper databaseHelper;
    private SharedPreferences prefs;

    public static String ACTOR_KEY = "ACTOR_KEY";
    public static String NOTIF_TOAST = "notif_toast";
    public static String NOTIF_STATUS = "notif_statis";
    private int itemId = 0; // Selected item ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final ListView listView = (ListView) findViewById(R.id.notes_list);

        try {
            List<Notes> list = getDatabaseHelper().getNotesDao().queryForAll();

            ListAdapter adapterr = new ArrayAdapter<>(ListActivity.this, R.layout.list_item, list);
            listView.setAdapter(adapterr);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Notes p = (Notes) listView.getItemAtPosition(position);

                    Intent intent = new Intent(ListActivity.this, Detail.class);
                    intent.putExtra(ACTOR_KEY, p.getmId());
                    startActivity(intent);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Metoda koja komunicira sa bazom podataka
    public ORMLightHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, ORMLightHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.notes_list);

        if (listview != null){
            ArrayAdapter<Notes> adapter = (ArrayAdapter<Notes>) listview.getAdapter();

            if(adapter!= null)
            {
                try {
                    adapter.clear();
                    List<Notes> list = getDatabaseHelper().getNotesDao().queryForAll();

                    adapter.addAll(list);

                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void showStatusMesage(String message){
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Pripremni test");
        mBuilder.setContentText(message);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_add);

        mBuilder.setLargeIcon(bm);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_new_notes:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.add_notes_layout);

                Button add = (Button) dialog.findViewById(R.id.add_notes_add_btn);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText edNaslov = (EditText) dialog.findViewById(R.id.notes_naslov_add);
                        EditText edOpis = (EditText) dialog.findViewById(R.id.notes_opis_add);
                        EditText edDatum = (EditText) dialog.findViewById(R.id.notes_datum_add);


                        Notes n = new Notes();

                        n.setmNaslov(edNaslov.getText().toString());
                        n.setmOpis(edOpis.getText().toString());
                        n.setmDatum(edDatum.getText().toString());


                        try {
                            getDatabaseHelper().getNotesDao().create(n);

                            //provera podesenja
                            boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
                            boolean status = prefs.getBoolean(NOTIF_STATUS, false);

                            if (toast) {
                                Toast.makeText(ListActivity.this, "Added new Notes", Toast.LENGTH_SHORT).show();
                            }

                            if (status) {
                                showStatusMesage("Added new Notes");
                            }

                            //REFRESH
                            refresh();


                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.cancel_notes_add_btn);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            case R.id.about:

                AlertDialog alertDialog = new AboutDialog(this).prepareDialog();
                alertDialog.show();
                break;
            case R.id.preferences:
                startActivity(new Intent(ListActivity.this, Prefererences.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // nakon rada sa bazo podataka potrebno je obavezno
        //osloboditi resurse!
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
