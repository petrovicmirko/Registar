package org.unibl.etf.mr_projektni_zadatak.registar_db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stavka")
public class Stavka {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "osnovno_sredstvo_id")
    private int osnovnoSredstvoId;
    @ColumnInfo(name = "trenutno_zaduzena_osoba_id")
    private int trenutnoZaduzenaOsobaId;
    @ColumnInfo(name = "nova_zaduzena_osoba_id")
    private int novaZaduzenaOsobaId;
    @ColumnInfo(name = "trenutna_lokacija_id")
    private int trenutnaLokacijaId;
    @ColumnInfo(name = "nova_lokacija_id")
    private int novaLokacijaId;
    @ColumnInfo(name = "popisna_lista_id")
    private int popisnaListaId;

    public Stavka(int osnovnoSredstvoId, int trenutnoZaduzenaOsobaId, int novaZaduzenaOsobaId, int trenutnaLokacijaId, int novaLokacijaId, int popisnaListaId) {
        this.osnovnoSredstvoId = osnovnoSredstvoId;
        this.trenutnoZaduzenaOsobaId = trenutnoZaduzenaOsobaId;
        this.novaZaduzenaOsobaId = novaZaduzenaOsobaId;
        this.trenutnaLokacijaId = trenutnaLokacijaId;
        this.novaLokacijaId = novaLokacijaId;
        this.popisnaListaId = popisnaListaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOsnovnoSredstvoId() {
        return osnovnoSredstvoId;
    }

    public void setOsnovnoSredstvoId(int osnovnoSredstvoId) {
        this.osnovnoSredstvoId = osnovnoSredstvoId;
    }

    public int getTrenutnoZaduzenaOsobaId() {
        return trenutnoZaduzenaOsobaId;
    }

    public void setTrenutnoZaduzenaOsobaId(int trenutnoZaduzenaOsobaId) {
        this.trenutnoZaduzenaOsobaId = trenutnoZaduzenaOsobaId;
    }

    public int getNovaZaduzenaOsobaId() {
        return novaZaduzenaOsobaId;
    }

    public void setNovaZaduzenaOsobaId(int novaZaduzenaOsobaId) {
        this.novaZaduzenaOsobaId = novaZaduzenaOsobaId;
    }

    public int getTrenutnaLokacijaId() {
        return trenutnaLokacijaId;
    }

    public void setTrenutnaLokacijaId(int trenutnaLokacijaId) {
        this.trenutnaLokacijaId = trenutnaLokacijaId;
    }

    public int getNovaLokacijaId() {
        return novaLokacijaId;
    }

    public void setNovaLokacijaId(int novaLokacijaId) {
        this.novaLokacijaId = novaLokacijaId;
    }

    public int getPopisnaListaId() {
        return popisnaListaId;
    }

    public void setPopisnaListaId(int popisnaListaId) {
        this.popisnaListaId = popisnaListaId;
    }
}
