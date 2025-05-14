package org.unibl.etf.mr_projektni_zadatak.registar_db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "popisna_lista")
public class PopisnaLista {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "naziv_liste")
    private String nazivListe;
    @ColumnInfo(name = "datum_kreiranja")
    private String datumKreiranja;
    @ColumnInfo(name = "opis")
    private String opis;

    public PopisnaLista(String nazivListe, String datumKreiranja, String opis) {
        this.nazivListe = nazivListe;
        this.datumKreiranja = datumKreiranja;
        this.opis = opis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazivListe() {
        return nazivListe;
    }

    public void setNazivListe(String nazivListe) {
        this.nazivListe = nazivListe;
    }

    public String getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(String datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
