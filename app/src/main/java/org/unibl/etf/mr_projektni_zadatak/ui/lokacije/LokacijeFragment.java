package org.unibl.etf.mr_projektni_zadatak.ui.lokacije;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.adapters.LokacijaAdapter;
import org.unibl.etf.mr_projektni_zadatak.adapters.OsnovnoSredstvoAdapter;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva.OsnovnaSredstvaViewModel;

import java.util.ArrayList;
import java.util.List;

public class LokacijeFragment extends Fragment {
    public static final String LOKACIJA_ID = "lokacijaId";
    private List<Lokacija> lokacije;
    private LokacijeViewModel lokacijeViewModel;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private LokacijaAdapter adapter;
    public static LokacijeFragment newInstance() {
        return new LokacijeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lokacije, container, false);

        lokacije = new ArrayList<>();

        root.findViewById(R.id.fab_dodaj_lokaciju).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_nav_lokacije_to_nav_dodaj_lokaciju);
            }
        });

        searchView = root.findViewById(R.id.sv_lokacije);
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

        adapter = new LokacijaAdapter(lokacije, new LokacijaAdapter.OnItemClickListener() {
            @Override
            public void onViseClick(Lokacija lokacija) {
                setAlertDialog(lokacija);
            }
        });

        recyclerView = root.findViewById(R.id.rv_lokacije);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        lokacijeViewModel = new ViewModelProvider(this).get(LokacijeViewModel.class);
        lokacijeViewModel.getLokacije().observe(getViewLifecycleOwner(), lokacije1 -> {
            lokacije.clear();
            lokacije.addAll(lokacije1);
            adapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lokacijeViewModel = new ViewModelProvider(this).get(LokacijeViewModel.class);

        lokacijeViewModel.getLokacije().observe(getViewLifecycleOwner(), lokacije -> {
            adapter.setFiltriraneLokacije(lokacije);
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }

        if (adapter != null) {
            adapter.setFiltriraneLokacije(lokacije);
        }
    }

    private void filtrirajListu(String text) {
        List<Lokacija> filtriranaLista = new ArrayList<>();
        for(Lokacija lokacija : lokacije) {
            if(
                    lokacija.getGrad().toLowerCase().contains(text.toLowerCase())
                            || String.valueOf(lokacija.getGeografskaSirina()).contains(text)
                            || String.valueOf(lokacija.getGeografskaDuzina()).contains(text)
            ) {
                filtriranaLista.add(lokacija);
            }
        }

        adapter.setFiltriraneLokacije(filtriranaLista);
    }

    public void setAlertDialog(Lokacija lokacija) {
        String odaberiAkciju = getString(R.string.odaberi_akciju);
        String azurirajAkcija = getString(R.string.azuriraj_akcija);
        String obrisiAkcija = getString(R.string.obrisi_akcija);
        String potvrdaBrisanja = getString(R.string.potvrda_brisanja);
        String potvrdaBrisanjaPitanje = getString(R.string.potvrda_brisanja_pitanje);
        String da = getString(R.string.da);
        String ne = getString(R.string.ne);
        String uspjesnoObrisano = getString(R.string.uspjesno_obrisano);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(odaberiAkciju);
//        builder.setMessage("Šta želiš učiniti s lokacijom?");

        builder.setPositiveButton(azurirajAkcija, (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(LOKACIJA_ID, lokacija.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_nav_lokacije_to_nav_dodaj_lokaciju, bundle);
        });

        builder.setNeutralButton(obrisiAkcija, (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(potvrdaBrisanja)
                    .setMessage(potvrdaBrisanjaPitanje)
                    .setPositiveButton(da, (dialog1, which1) -> {
                        lokacijeViewModel.delete(lokacija);
                        lokacije.remove(lokacija);
                        adapter.setFiltriraneLokacije(lokacije);
                        Toast.makeText(requireContext(), uspjesnoObrisano, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(ne, null)
                    .show();
        });

        builder.show();
    }
}