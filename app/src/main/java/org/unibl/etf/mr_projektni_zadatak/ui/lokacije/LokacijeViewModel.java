package org.unibl.etf.mr_projektni_zadatak.ui.lokacije;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.unibl.etf.mr_projektni_zadatak.registar_db.RegistarDatabase;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.LokacijaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LokacijeViewModel extends AndroidViewModel {
    private LokacijaDao lokacijaDao;
    private LiveData<List<Lokacija>> lokacije = new MutableLiveData<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public LokacijeViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        lokacijaDao = db.lokacijaDao();

        lokacije = lokacijaDao.getLokacije();
//        loadLokacije();
    }

//    private void loadLokacije() {
//        executor.execute(() -> {
//            List<Lokacija> sveLokacije = lokacijaDao.getLokacije();
//            lokacije.postValue(sveLokacije);
//        });
//    }

    public LiveData<List<Lokacija>> getLokacije() {
        return lokacije;
    }

    public LiveData<Lokacija> getLokacijaById(int id) {
        MutableLiveData<Lokacija> lokacijaLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            Lokacija lokacija = lokacijaDao.getLokacijaById(id);
            lokacijaLiveData.postValue(lokacija);
        });
        return lokacijaLiveData;
    }

    public void insert(Lokacija lokacija) {
        executor.execute(() -> {
            lokacijaDao.insert(lokacija);
        });
//        loadLokacije();
    }

    public void update(Lokacija lokacija) {
        executor.execute(() -> {
            lokacijaDao.update(lokacija);
        });
//        loadLokacije();
    }

    public void delete(Lokacija lokacija) {
        executor.execute(() -> {
            lokacijaDao.delete(lokacija);
        });
//        loadLokacije();
    }
}