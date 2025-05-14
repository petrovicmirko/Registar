package org.unibl.etf.mr_projektni_zadatak.ui.popisne_liste;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.unibl.etf.mr_projektni_zadatak.registar_db.RegistarDatabase;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.OsobaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.PopisnaListaDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.PopisnaLista;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PopisneListeViewModel extends AndroidViewModel {
    PopisnaListaDao popisnaListaDao;
    private LiveData<List<PopisnaLista>> popisneListe = new MutableLiveData<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    public PopisneListeViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        popisnaListaDao = db.popisnaListaDao();

        popisneListe = popisnaListaDao.getPopisneListe();
//        loadPopisneListe();
    }
//    private void loadPopisneListe() {
//        executor.execute(() -> {
//            List<PopisnaLista> svePopisneListe = popisnaListaDao.getPopisneListe();
//            popisneListe.postValue(svePopisneListe);
//        });
//    }

    public LiveData<List<PopisnaLista>> getPopisneListe() {
        return popisneListe;
    }

    public LiveData<PopisnaLista> getPopisnaListaById(int id) {
        MutableLiveData<PopisnaLista> popisnaListaLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            PopisnaLista popisnaLista = popisnaListaDao.getPopisnaListaById(id);
            popisnaListaLiveData.postValue(popisnaLista);
        });
        return popisnaListaLiveData;
    }

    public void insert(PopisnaLista popisnaLista) {
        executor.execute(() -> {
            popisnaListaDao.insert(popisnaLista);
        });
//        loadPopisneListe();
    }

    public void update(PopisnaLista popisnaLista) {
        executor.execute(() -> {
            popisnaListaDao.update(popisnaLista);
        });
//        loadPopisneListe();
    }

    public void delete(PopisnaLista popisnaLista) {
        executor.execute(() -> {
            popisnaListaDao.delete(popisnaLista);
        });
//        loadPopisneListe();
    }
}