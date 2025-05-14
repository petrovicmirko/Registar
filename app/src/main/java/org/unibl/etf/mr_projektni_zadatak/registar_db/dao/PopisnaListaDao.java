package org.unibl.etf.mr_projektni_zadatak.registar_db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.PopisnaLista;

import java.util.List;

@Dao
public interface PopisnaListaDao {
    @Query("SELECT * FROM popisna_lista")
    LiveData<List<PopisnaLista>> getPopisneListe();
    @Query("SELECT * FROM popisna_lista WHERE id = :id")
    PopisnaLista getPopisnaListaById(int id);
    @Insert
    void insert(PopisnaLista popisnaLista);
    @Update
    void update(PopisnaLista popisnaLista);
    @Delete
    void delete(PopisnaLista popisnaLista);
}
