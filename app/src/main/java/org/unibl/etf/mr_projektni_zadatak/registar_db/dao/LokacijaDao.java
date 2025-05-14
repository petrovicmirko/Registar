package org.unibl.etf.mr_projektni_zadatak.registar_db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;

import java.util.List;

@Dao
public interface LokacijaDao {

    @Insert
    void insert(Lokacija lokacija);
    @Query("SELECT * FROM lokacija")
    LiveData<List<Lokacija>> getLokacije();

    @Query("SELECT * FROM lokacija WHERE id = :id")
    Lokacija getLokacijaById(int id);

    @Update
    void update(Lokacija lokacija);

    @Delete
    void delete(Lokacija lokacija);
}
