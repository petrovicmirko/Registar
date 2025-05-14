package org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import org.unibl.etf.mr_projektni_zadatak.MainActivity;
import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.adapters.OsnovnoSredstvoAdapter;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.ui.stavka.StavkeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OsnovnaSredstvaFragment extends Fragment {
    public static final String OSNOVNO_SREDSTVO_ID = "osnovnoSredstvoId";
    private List<OsnovnoSredstvo> osnovnaSredstva;
    private OsnovnaSredstvaViewModel osnovnaSredstvaViewModel;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private OsnovnoSredstvoAdapter adapter;
    private StavkeViewModel stavkeViewModel;

    public static OsnovnaSredstvaFragment newInstance() {
        return new OsnovnaSredstvaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_osnovna_sredstva, container, false);

        osnovnaSredstva = new ArrayList<>();
        osnovnaSredstvaViewModel = new ViewModelProvider(this).get(OsnovnaSredstvaViewModel.class);

        root.findViewById(R.id.fab_dodaj_osnovno_sredstvo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_nav_osnovna_sredstva_to_nav_dodaj_osnovno_sredstvo);
            }
        });

        searchView = root.findViewById(R.id.sv_osnovna_sredstva);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrirajListu(newText);
                return true;
            }
        });
        adapter = new OsnovnoSredstvoAdapter(osnovnaSredstva, requireContext(), new OsnovnoSredstvoAdapter.OnItemClickListener() {
            @Override
            public void onViseClick(OsnovnoSredstvo osnovnoSredstvo) {
                setAlertDialog(osnovnoSredstvo);
            }
        });

        recyclerView = root.findViewById(R.id.rv_osnovna_sredstva);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        stavkeViewModel = new ViewModelProvider(this).get(StavkeViewModel.class);

        osnovnaSredstvaViewModel.getOsnovnaSredstva().observe(getViewLifecycleOwner(), osnovnaSredstva1 -> {
            osnovnaSredstva.clear();
            osnovnaSredstva.addAll(osnovnaSredstva1);
            adapter.notifyDataSetChanged();
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }

        if (adapter != null) {
            adapter.setFiltriranaOsnovnaSredstva(osnovnaSredstva);
        }
    }

    private void filtrirajListu(String text) {
        List<OsnovnoSredstvo> filtriranaLista = new ArrayList<>();
        for(OsnovnoSredstvo os : osnovnaSredstva) {
            if(os.getNaziv().toLowerCase().contains(text.toLowerCase()) || String.valueOf(os.getBarkod()).contains(text)) {
                filtriranaLista.add(os);
            }
        }

        adapter.setFiltriranaOsnovnaSredstva(filtriranaLista);
    }

    public void setAlertDialog(OsnovnoSredstvo osnovnoSredstvo) {
        String odaberiAkciju = getString(R.string.odaberi_akciju);
        String azurirajAkcija = getString(R.string.azuriraj_akcija);
        String obrisiAkcija = getString(R.string.obrisi_akcija);
        String potvrdaBrisanja = getString(R.string.potvrda_brisanja);
        String potvrdaBrisanjaPitanje = getString(R.string.potvrda_brisanja_pitanje);
        String da = getString(R.string.da);
        String ne = getString(R.string.ne);
        String uspjesnoObrisano = getString(R.string.uspjesno_obrisano);
        String detaljiAkcija = getString(R.string.detalji_akcija);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(odaberiAkciju);
//        builder.setMessage("Šta želiš učiniti s osnovnim sredstvom?");

        builder.setPositiveButton(azurirajAkcija, (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(OSNOVNO_SREDSTVO_ID, osnovnoSredstvo.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_nav_osnovna_sredstva_to_nav_dodaj_osnovno_sredstvo, bundle);
        });

        builder.setNeutralButton(obrisiAkcija, (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(potvrdaBrisanja)
                    .setMessage(potvrdaBrisanjaPitanje)
                    .setPositiveButton(da, (dialog1, which1) -> {
                        osnovnaSredstvaViewModel.delete(osnovnoSredstvo);
                        stavkeViewModel.deleteStavkeByOsnovnoSredstvoId(osnovnoSredstvo.getId());
                        Toast.makeText(requireContext(), uspjesnoObrisano, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(ne, null)
                    .show();
        });

        builder.setNegativeButton(detaljiAkcija, (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(OSNOVNO_SREDSTVO_ID, osnovnoSredstvo.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_nav_osnovna_sredstva_to_nav_detalji_osnovnog_sredstva, bundle);
        });

        // Prikaži AlertDialog
        builder.show();
    }
}