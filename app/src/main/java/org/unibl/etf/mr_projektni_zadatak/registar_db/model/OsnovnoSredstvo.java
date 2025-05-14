package org.unibl.etf.mr_projektni_zadatak.registar_db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "osnovno_sredstvo")
public class OsnovnoSredstvo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "naziv")
    private String naziv;
    @ColumnInfo(name = "opis")
    private String opis;
    @ColumnInfo(name = "barkod")
    private long barkod;
    @ColumnInfo(name = "cijena")
    private String cijena;
    @ColumnInfo(name = "datum_kreiranja")
    private String datumKreiranja;
    @ColumnInfo(name = "zaposleni_id")
    private int zaposleniId;
    @ColumnInfo(name = "lokacija_id")
    private int lokacijaId;
    @ColumnInfo(name = "slika")
    private String slika;

    public OsnovnoSredstvo(String naziv, String opis, long barkod, String cijena, String datumKreiranja, int zaposleniId, int lokacijaId, String slika) {
        this.naziv = naziv;
        this.opis = opis;
        this.barkod = barkod;
        this.cijena = cijena;
        this.datumKreiranja = datumKreiranja;
        this.zaposleniId = zaposleniId;
        this.lokacijaId = lokacijaId;
        this.slika = slika;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public long getBarkod() {
        return barkod;
    }

    public void setBarkod(int barkod) {
        this.barkod = barkod;
    }

    public String getCijena() {
        return cijena;
    }

    public void setCijena(String cijena) {
        this.cijena = cijena;
    }

    public String getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(String datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    public int getZaposleniId() {
        return zaposleniId;
    }

    public void setZaposleniId(int zaposleniId) {
        this.zaposleniId = zaposleniId;
    }

    public int getLokacijaId() {
        return lokacijaId;
    }

    public void setLokacijaId(int lokacijaId) {
        this.lokacijaId = lokacijaId;
    }

    public String getSlika() {
        return slika;
    }

    public void setSlika(String slika) {
        this.slika = slika;
    }
}
