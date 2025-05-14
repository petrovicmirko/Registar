package org.unibl.etf.mr_projektni_zadatak.registar_db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.LokacijaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.OsnovnoSredstvoDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.OsobaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.PopisnaListaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.StavkaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.PopisnaLista;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Stavka;

@Database(entities = {OsnovnoSredstvo.class, Osoba.class, Lokacija.class, PopisnaLista.class, Stavka.class},
        version = 43)
public abstract class RegistarDatabase extends RoomDatabase {
    public abstract OsnovnoSredstvoDao osnovnoSredstvoDao();

    public abstract OsobaDao osobaDao();

    public abstract LokacijaDao lokacijaDao();

    public abstract PopisnaListaDao popisnaListaDao();

    public abstract StavkaDao stavkaDao();

    private static volatile RegistarDatabase registarDatabase;
    public static RegistarDatabase getInstance(Context context) {
        if (registarDatabase == null) {
            synchronized (RegistarDatabase.class) {
                if (registarDatabase == null) {
                    registarDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    RegistarDatabase.class, "registar_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return registarDatabase;
    }

    public void cleanDatatabase() {
        registarDatabase = null;
    }
}
