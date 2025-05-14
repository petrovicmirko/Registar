package org.unibl.etf.mr_projektni_zadatak.ui.zaposleni;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.adapters.LokacijaAdapter;
import org.unibl.etf.mr_projektni_zadatak.adapters.OsobaAdapter;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.ui.lokacije.LokacijeViewModel;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ZaposleniFragment extends Fragment {
    public static final String ZAPOSLENI_ID = "zaposleniId";
    private List<Osoba> zaposleni;
    private ZaposleniViewModel zaposleniViewModel;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private OsobaAdapter adapter;

    public static ZaposleniFragment newInstance() {
        return new ZaposleniFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_zaposleni, container, false);

        zaposleni = new ArrayList<>();

        root.findViewById(R.id.fab_dodaj_zaposlenog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_nav_zaposleni_to_nav_dodaj_zaposlenog);
            }
        });

        searchView = root.findViewById(R.id.sv_zaposleni);
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

        adapter = new OsobaAdapter(zaposleni, new OsobaAdapter.OnItemClickListener() {
            @Override
            public void onViseClick(Osoba osoba) {
                setAlertDialog(osoba);
            }
        });

        recyclerView = root.findViewById(R.id.rv_zaposleni);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        zaposleniViewModel = new ViewModelProvider(this).get(ZaposleniViewModel.class);
        zaposleniViewModel.getZaposlene().observe(getViewLifecycleOwner(), zaposleni1 -> {
            zaposleni.clear();
            zaposleni.addAll(zaposleni1);
            adapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        zaposleniViewModel = new ViewModelProvider(this).get(ZaposleniViewModel.class);

        zaposleniViewModel.getZaposlene().observe(getViewLifecycleOwner(), zaposleni -> {
            adapter.setFiltriraniZaposleni(zaposleni);
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
            adapter.setFiltriraniZaposleni(zaposleni);
        }
    }

    private void filtrirajListu(String text) {
        List<Osoba> filtriranaLista = new ArrayList<>();
        for(Osoba osoba : zaposleni) {
            if(
                    osoba.getIme().toLowerCase().contains(text.toLowerCase())
                            || osoba.getPrezime().toLowerCase().contains(text.toLowerCase())
                            || osoba.getPozicija().toLowerCase().contains(text.toLowerCase())
            ) {
                filtriranaLista.add(osoba);
            }
        }

        adapter.setFiltriraniZaposleni(filtriranaLista);
    }

    public void setAlertDialog(Osoba osoba) {
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
//        builder.setMessage("Šta želiš učiniti sa zaposlenim?");

        builder.setPositiveButton(azurirajAkcija, (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(ZAPOSLENI_ID, osoba.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_nav_zaposleni_to_nav_dodaj_zaposlenog, bundle);
        });

        builder.setNeutralButton(obrisiAkcija, (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(potvrdaBrisanja)
                    .setMessage(potvrdaBrisanjaPitanje)
                    .setPositiveButton(da, (dialog1, which1) -> {
                        // Obriši osnovno sredstvo
                        zaposleniViewModel.delete(osoba);
                        zaposleni.remove(osoba);
                        adapter.setFiltriraniZaposleni(zaposleni);
                        Toast.makeText(requireContext(), uspjesnoObrisano, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(ne, null)
                    .show();
        });

        builder.show();
    }
}