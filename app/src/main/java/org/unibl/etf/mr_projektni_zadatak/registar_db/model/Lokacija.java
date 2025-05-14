package org.unibl.etf.mr_projektni_zadatak.registar_db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lokacija")
public class Lokacija {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "grad")
    public String grad;
    @ColumnInfo(name = "geografska_sirina")
    public double geografskaSirina;
    @ColumnInfo(name = "geografska_duzina")
    public double geografskaDuzina;

    public Lokacija(String grad, double geografskaSirina, double geografskaDuzina) {
        this.grad = grad;
        this.geografskaSirina = geografskaSirina;
        this.geografskaDuzina = geografskaDuzina;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGrad() {
        return grad;
    }

    public void setGrad(String grad) {
        this.grad = grad;
    }

    public double getGeografskaSirina() {
        return geografskaSirina;
    }

    public void setGeografskaSirina(double geografskaSirina) {
        this.geografskaSirina = geografskaSirina;
    }

    public double getGeografskaDuzina() {
        return geografskaDuzina;
    }

    public void setGeografskaDuzina(double geografskaDuzina) {
        this.geografskaDuzina = geografskaDuzina;
    }
}
