package org.unibl.etf.mr_projektni_zadatak.registar_db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;

import java.util.List;

@Dao
public interface OsobaDao {
    @Query("SELECT * FROM osoba")
    LiveData<List<Osoba>> getOsobe();
    @Query("SELECT * FROM osoba WHERE id = :id")
    Osoba getOsobaById(int id);
    @Insert
    void insert(Osoba osoba);
    @Update
    void update(Osoba osoba);
    @Delete
    void delete(Osoba osoba);
}
