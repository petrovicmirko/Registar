package org.unibl.etf.mr_projektni_zadatak.ui.zaposleni;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.unibl.etf.mr_projektni_zadatak.registar_db.RegistarDatabase;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.OsobaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZaposleniViewModel extends AndroidViewModel {
    private OsobaDao osobaDao;
    private LiveData<List<Osoba>> osobe = new MutableLiveData<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ZaposleniViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        osobaDao = db.osobaDao();

        osobe = osobaDao.getOsobe();
//        loadOsobe();
    }

//    private void loadOsobe() {
//        executor.execute(() -> {
//            List<Osoba> sveOsobe = osobaDao.getOsobe();
//            osobe.postValue(sveOsobe);
//        });
//    }

    public LiveData<List<Osoba>> getZaposlene() {
        return osobe;
    }

    public LiveData<Osoba> getZaposleniById(int id) {
        MutableLiveData<Osoba> zaposleniLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            Osoba osoba = osobaDao.getOsobaById(id);
            zaposleniLiveData.postValue(osoba);
        });
        return zaposleniLiveData;
    }

    public void insert(Osoba osoba) {
        executor.execute(() -> {
            osobaDao.insert(osoba);
        });
//        loadOsobe();
    }

    public void update(Osoba osoba) {
        executor.execute(() -> {
            osobaDao.update(osoba);
        });
//        loadOsobe();
    }

    public void delete(Osoba osoba) {
        executor.execute(() -> {
            osobaDao.delete(osoba);
        });
//        loadOsobe();
    }
}