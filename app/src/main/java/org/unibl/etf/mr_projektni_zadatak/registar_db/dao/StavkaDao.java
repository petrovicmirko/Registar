package org.unibl.etf.mr_projektni_zadatak.registar_db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.PopisnaLista;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Stavka;

import java.util.List;

@Dao
public interface StavkaDao {

    @Query("SELECT * FROM stavka")
    LiveData<List<Stavka>> getStavke();
    @Query("SELECT * FROM stavka WHERE id = :id")
    Stavka getStavkaById(int id);
    @Insert
    void insert(Stavka stavka);
    @Update
    void update(Stavka stavka);
    @Delete
    void delete(Stavka stavka);
    @Query("SELECT * FROM stavka WHERE popisna_lista_id = :popisnaListaId")
    List<Stavka> getStavkeByPopisnaListaId(int popisnaListaId);
    @Query("DELETE FROM stavka WHERE popisna_lista_id = :popisnaListaId")
    void deleteStavkeByPopisnaListaId(int popisnaListaId);

    @Query("DELETE FROM stavka WHERE osnovno_sredstvo_id = :osnovnoSredstvoId")
    void deleteStavkeByOsnovnoSredstvoId(int osnovnoSredstvoId);
}
