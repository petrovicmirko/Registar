package org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.unibl.etf.mr_projektni_zadatak.registar_db.RegistarDatabase;
import org.unibl.etf.mr_projektni_zadatak.registar_db.dao.OsnovnoSredstvoDao;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OsnovnaSredstvaViewModel extends AndroidViewModel {
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private LiveData<List<OsnovnoSredstvo>> osnovnaSredstva = new MutableLiveData<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public OsnovnaSredstvaViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        osnovnoSredstvoDao = db.osnovnoSredstvoDao();

        osnovnaSredstva = osnovnoSredstvoDao.getOsnovnaSredstva();
        //loadOsnovnaSredstva();
    }

//    private void loadOsnovnaSredstva() {
//        executor.execute(() -> {
//            List<OsnovnoSredstvo> svaOsnovnaSredstva = osnovnoSredstvoDao.getOsnovnaSredstva();
//            osnovnaSredstva.postValue(svaOsnovnaSredstva);
//        });
//    }

    public LiveData<List<OsnovnoSredstvo>> getOsnovnaSredstva() {
        return osnovnaSredstva;
    }

    public LiveData<OsnovnoSredstvo> getOsnovnoSredstvoById(int id) {
        MutableLiveData<OsnovnoSredstvo> osnovnoSredstvoLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            OsnovnoSredstvo osnovnoSredstvo = osnovnoSredstvoDao.getOsnovnoSredstvoById(id);
            osnovnoSredstvoLiveData.postValue(osnovnoSredstvo);
        });
        return osnovnoSredstvoLiveData;
    }

    public void insert(OsnovnoSredstvo osnovnoSredstvo) {
        executor.execute(() -> {
            osnovnoSredstvoDao.insert(osnovnoSredstvo);
        });
        //loadOsnovnaSredstva();
    }

    public void update(OsnovnoSredstvo osnovnoSredstvo) {
        executor.execute(() -> {
            osnovnoSredstvoDao.update(osnovnoSredstvo);
        });
        //loadOsnovnaSredstva();
    }

    public void delete(OsnovnoSredstvo osnovnoSredstvo) {
        executor.execute(() -> {
            osnovnoSredstvoDao.delete(osnovnoSredstvo);
        });
        //loadOsnovnaSredstva();
    }

    public LiveData<List<OsnovnoSredstvo>> getOsnovnaSredstvaByLokacijaId(int locationId) {
        MutableLiveData<List<OsnovnoSredstvo>> osnovnaSredstvaLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            List<OsnovnoSredstvo> osnovnaSredstva = osnovnoSredstvoDao.getOsnovnaSredstvaByLokacijaId(locationId);
            osnovnaSredstvaLiveData.postValue(osnovnaSredstva);
        });
        return osnovnaSredstvaLiveData;
    }

    public LiveData<OsnovnoSredstvo> getOsnovnoSredstvoByBarkod(long barkod) {
        MutableLiveData<OsnovnoSredstvo> osnovnoSredstvoLiveData = new MutableLiveData<>();
        executor.execute(() -> {
            OsnovnoSredstvo osnovnoSredstvo = osnovnoSredstvoDao.getOsnovnoSredstvoByBarkod(barkod);
            osnovnoSredstvoLiveData.postValue(osnovnoSredstvo);
        });
        return osnovnoSredstvoLiveData;
    }

    public OsnovnoSredstvo checkBarkod(String barkod) {
        return osnovnoSredstvoDao.checkBarkod(barkod) ;
    }
}