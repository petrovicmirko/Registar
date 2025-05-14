package org.unibl.etf.mr_projektni_zadatak.ui.stavka;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.unibl.etf.mr_projektni_zadatak.registar_db.RegistarDatabase;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.StavkaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Stavka;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StavkeViewModel extends AndroidViewModel {
    private StavkaDao stavkaDao;
    private LiveData<List<Stavka>> stavke = new MutableLiveData<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public StavkeViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        stavkaDao = db.stavkaDao();

        stavke = stavkaDao.getStavke();
//        loadStavke();
    }

//    private void loadStavke() {
//        executor.execute(() -> {
//            List<Stavka> sveStavke = stavkaDao.getStavke();
//            stavke.postValue(sveStavke);
//        });
//    }

    public LiveData<List<Stavka>> getStavke() {
        return stavke;
    }

    public LiveData<Stavka> getStavkaById(int id) {
        MutableLiveData<Stavka> stavkaLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            Stavka stavka = stavkaDao.getStavkaById(id);
            stavkaLiveData.postValue(stavka);
        });
        return stavkaLiveData;
    }

    public void insert(Stavka stavka) {
        executor.execute(() -> {
            stavkaDao.insert(stavka);
        });
//        loadStavke();
    }

    public void update(Stavka stavka) {
        executor.execute(() -> {
            stavkaDao.update(stavka);
        });
//        loadStavke();
    }

    public void delete(Stavka stavka) {
        executor.execute(() -> {
            stavkaDao.delete(stavka);
        });
//        loadStavke();
    }

    public LiveData<List<Stavka>> getStavkeByPopisnaListaId(int popisnaListaId) {
        MutableLiveData<List<Stavka>> stavkeLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            List<Stavka> stavke = stavkaDao.getStavkeByPopisnaListaId(popisnaListaId);
            stavkeLiveData.postValue(stavke);
        });
        return stavkeLiveData;
    }

    public void deleteStavkeByPopisnaListaId(int popisnaListaId) {
        executor.execute(() -> {
            stavkaDao.deleteStavkeByPopisnaListaId(popisnaListaId);
        });
    }

    public void deleteStavkeByOsnovnoSredstvoId(int osnovnoSredstvoId) {
        executor.execute(() -> {
            stavkaDao.deleteStavkeByOsnovnoSredstvoId(osnovnoSredstvoId);
        });
    }
}