package rs.aleph.android.test.aplikacija.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


import rs.aleph.android.test.aplikacija.db.model.Notes;


public class ORMLightHelper extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME    = "notes.db";
    private static final int    DATABASE_VERSION = 1;


    private Dao<Notes, Integer> mNotesDao = null;

    public ORMLightHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, Notes.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            TableUtils.dropTable(connectionSource, Notes.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Dao<Notes, Integer> getNotesDao() throws SQLException {
        if (mNotesDao == null) {
            mNotesDao = getDao(Notes.class);
        }

        return mNotesDao;
    }

    //obavezno prilikom zatvarnaj rada sa bazom osloboditi resurse
    @Override
    public void close() {

        mNotesDao = null;

        super.close();
    }
}
