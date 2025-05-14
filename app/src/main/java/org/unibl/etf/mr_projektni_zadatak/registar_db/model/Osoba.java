package org.unibl.etf.mr_projektni_zadatak.registar_db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "osoba")
public class Osoba {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "ime")
    private String ime;
    @ColumnInfo(name = "prezime")
    private String prezime;
    @ColumnInfo(name = "pozicija")
    private String pozicija;

    public Osoba(String ime, String prezime, String pozicija) {
        this.ime = ime;
        this.prezime = prezime;
        this.pozicija = pozicija;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getPozicija() {
        return pozicija;
    }

    public void setPozicija(String pozicija) {
        this.pozicija = pozicija;
    }
}
