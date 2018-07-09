package rs.aleph.android.test.aplikacija.activity;


import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;

import android.preference.PreferenceManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import rs.aleph.android.test.R;
import rs.aleph.android.test.aplikacija.db.ORMLightHelper;
import rs.aleph.android.test.aplikacija.db.model.Notes;

import static rs.aleph.android.test.aplikacija.activity.ListActivity.NOTIF_STATUS;
import static rs.aleph.android.test.aplikacija.activity.ListActivity.NOTIF_TOAST;

public class Detail extends AppCompatActivity {

    private ORMLightHelper databaseHelper;
    private SharedPreferences prefs;

    private TextView tvNaslov;
    private TextView tvOpis;
    private TextView tvDatum;

    private Notes notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int key = getIntent().getExtras().getInt(ListActivity.ACTOR_KEY);

        try {
            notes = getDatabaseHelper().getNotesDao().queryForId(key);

            tvNaslov = (TextView) findViewById(R.id.notes_naslov);
            tvOpis = (TextView)findViewById(R.id.notes_opis);
            tvDatum= (TextView)findViewById(R.id.notes_datum);


            tvNaslov.setText(notes.getmNaslov());
            tvOpis.setText(notes.getmOpis());
            tvDatum.setText(notes.getmDatum());

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

    private void showStatusMesage(String message) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Notes");
        mBuilder.setContentText(message);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_add);

        mBuilder.setLargeIcon(bm);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void showMessage(String message) {
        //provera
        boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
        boolean status = prefs.getBoolean(NOTIF_STATUS, false);

        if (toast) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        if (status) {
            showStatusMesage(message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.priprema_edit:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.edit_notes_layout);
                dialog.setCanceledOnTouchOutside(false);

                Button edit=(Button)dialog.findViewById(R.id.edit_Notes_edit_btn);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                        EditText naslov = (EditText) dialog.findViewById(R.id.notes_naslov_edit);
                        EditText opis = (EditText) dialog.findViewById(R.id.notes_opis_edit);
                        EditText datum = (EditText) dialog.findViewById(R.id.notes_datum_edit);



                        notes.setmNaslov(naslov.getText().toString());
                        notes.setmOpis(opis.getText().toString());
                        notes.setmDatum(datum.getText().toString());

                        tvNaslov.setText(notes.getmNaslov());
                        tvOpis.setText(notes.getmOpis());
                        tvDatum.setText(notes.getmDatum());


                            getDatabaseHelper().getNotesDao().update(notes);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        //URADITI REFRESH

                        showMessage("Notes is Changed");

                        dialog.dismiss();
                        finish();
                    }
                });
                Button cancel = (Button) dialog.findViewById(R.id.cancel_notes_edit_btn);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.show();

                break;
            case R.id.priprema_remove:
                //OVTVARAMO DIALOG ZA UNOS INFORMACIJA
                final Dialog dialogRemove = new Dialog(this);
                dialogRemove.setContentView(R.layout.remove_notes_layout);
                dialogRemove.setCanceledOnTouchOutside(false);

                TextView textView =(TextView) findViewById(R.id.text_dialog);
                Button deleteDialog = (Button) dialogRemove.findViewById(R.id.delete_notes_btn_dialog);
                deleteDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            getDatabaseHelper().getNotesDao().delete(notes);
                            showMessage("Notes Deleted");
                            finish();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
//                        showMessage("Actor Deleted");

//                        finish(); //moramo pozvati da bi se vratili na prethodnu aktivnost
                    }
                });
                Button cancelDialog = (Button) dialogRemove.findViewById(R.id.cancel_notes_btn_dialog);
                cancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogRemove.dismiss();

                        finish(); //moramo pozvati da bi se vratili na prethodnu aktivnost
                    }
                });
                dialogRemove.show();
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

