package rs.aleph.android.test.aplikacija.db.model;


import com.j256.ormlite.field.DatabaseField;

import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = Notes.TABLE_NAME_USERS)
public class Notes {

    public static final String TABLE_NAME_USERS = "notes";
    public static final String FIELD_NAME_ID     = "id";
    public static final String TABLE_NOTES_NASLOV = "naslov";
    public static final String TABLE_NOTES_OPIS = "opis";
    public static final String TABLE_NOTES_DATUM = "datum";


    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = TABLE_NOTES_NASLOV)
    private String mNaslov;

    @DatabaseField(columnName = TABLE_NOTES_OPIS)
    private String mOpis;

    @DatabaseField(columnName = TABLE_NOTES_DATUM)
    private String mDatum;


    public Notes() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmNaslov() {
        return mNaslov;
    }

    public void setmNaslov(String mNaslov) {
        this.mNaslov = mNaslov;
    }

    public String getmOpis() {
        return mOpis;
    }

    public void setmOpis(String mOpis) {
        this.mOpis = mOpis;
    }

    public String getmDatum() {
        return mDatum;
    }

    public void setmDatum(String mDatum) {
        this.mDatum = mDatum;
    }

    @Override
    public String toString() {
        return mNaslov;
    }
}
