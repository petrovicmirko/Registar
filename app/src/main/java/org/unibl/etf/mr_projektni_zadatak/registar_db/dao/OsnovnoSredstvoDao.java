package org.unibl.etf.mr_projektni_zadatak.registar_db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;

import java.util.List;

@Dao
public interface OsnovnoSredstvoDao {
    @Query("SELECT * FROM osnovno_sredstvo")
    LiveData<List<OsnovnoSredstvo>> getOsnovnaSredstva();

    @Query("SELECT * FROM osnovno_sredstvo WHERE id = :id")
    OsnovnoSredstvo getOsnovnoSredstvoById(int id);

    @Insert
    void insert(OsnovnoSredstvo osnovnoSredstvo);

    @Update
    void update(OsnovnoSredstvo osnovnoSredstvo);

    @Delete
    void delete(OsnovnoSredstvo osnovnoSredstvo);

    @Query("SELECT * FROM osnovno_sredstvo WHERE lokacija_id = :lokacijaId")
    List<OsnovnoSredstvo> getOsnovnaSredstvaByLokacijaId(int lokacijaId);

    @Query("SELECT * FROM osnovno_sredstvo WHERE barkod = :barkod")
    OsnovnoSredstvo getOsnovnoSredstvoByBarkod(long barkod);

    @Query("SELECT * FROM osnovno_sredstvo WHERE barkod = :barkod")
    OsnovnoSredstvo checkBarkod(String barkod);
}
