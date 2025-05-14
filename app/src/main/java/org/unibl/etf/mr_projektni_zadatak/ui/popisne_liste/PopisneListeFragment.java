package org.unibl.etf.mr_projektni_zadatak.ui.popisne_liste;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.adapters.OsobaAdapter;
import org.unibl.etf.mr_projektni_zadatak.adapters.PopisnaListaAdapter;
import org.unibl.etf.mr_projektni_zadatak.databinding.FragmentPopisneListeBinding;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.PopisnaLista;
import org.unibl.etf.mr_projektni_zadatak.ui.stavka.StavkeViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.zaposleni.ZaposleniViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PopisneListeFragment extends Fragment {
    public static final String POPISNA_LISTA_ID = "popisnaListaId";
    public static final String DATUM_PATTERN = "dd.MM.yyyy.";
    private List<PopisnaLista> popisneListe;
    private PopisneListeViewModel popisneListeViewModel;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private PopisnaListaAdapter adapter;
    private StavkeViewModel stavkeViewModel;
    public static PopisneListeFragment newInstance() {
        return new PopisneListeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_popisne_liste, container, false);

        popisneListe = new ArrayList<>();
        root.findViewById(R.id.fab_dodaj_popisnu_listu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziAddListDialog();
                //Navigation.findNavController(root).navigate(R.id.action_nav_popisne_liste_to_nav_dodaj_popisnu_listu);
            }
        });

        searchView = root.findViewById(R.id.sv_popisne_liste);
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

        adapter = new PopisnaListaAdapter(popisneListe, new PopisnaListaAdapter.OnItemClickListener() {
            @Override
            public void onViseClick(PopisnaLista popisnaLista) {
                setAlertDialog(popisnaLista);
            }
        });

        recyclerView = root.findViewById(R.id.rv_popisne_liste);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        stavkeViewModel = new ViewModelProvider(this).get(StavkeViewModel.class);

        popisneListeViewModel = new ViewModelProvider(this).get(PopisneListeViewModel.class);
        popisneListeViewModel.getPopisneListe().observe(getViewLifecycleOwner(), popisneListe1 -> {
            popisneListe.clear();
            popisneListe.addAll(popisneListe1);
            adapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        popisneListeViewModel = new ViewModelProvider(this).get(PopisneListeViewModel.class);

        popisneListeViewModel.getPopisneListe().observe(getViewLifecycleOwner(), popisneListe -> {
            adapter.setFiltriranePopisneListe(popisneListe);
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
            adapter.setFiltriranePopisneListe(popisneListe);
        }
    }

    private void prikaziAddListDialog() {
        String dodajNovuPopisnuListu = getString(R.string.dodaj_novu_popisnu_listu);
        String sacuvaj = getString(R.string.sacuvaj);
        String otkaziAkcija = getString(R.string.otkazi_akcija);
        String nazivObavezan = getString(R.string.naziv_obavezan);

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_dodaj_popisnu_listu, null);

        EditText etNaziv = dialogView.findViewById(R.id.et_naziv_pl);
        EditText etOpis = dialogView.findViewById(R.id.et_opis_pl);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(dodajNovuPopisnuListu)
                .setView(dialogView)
                .setPositiveButton(sacuvaj, (d, which) -> {
                    String naziv = etNaziv.getText().toString().trim();
                    String opis = etOpis.getText().toString().trim();

                    if (!naziv.isEmpty()) {
                        String datumKreiranja = new SimpleDateFormat(DATUM_PATTERN, Locale.getDefault()).format(new Date());
                        PopisnaLista popisnaLista = new PopisnaLista(naziv, datumKreiranja, opis);
                        popisneListeViewModel.insert(popisnaLista);
                    } else {
                        Toast.makeText(requireContext(),
                                nazivObavezan, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(otkaziAkcija, null)
                .create();

        dialog.show();

        etNaziv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(!s.toString().trim().isEmpty());
            }
        });
    }

    private void prikaziUpdateListDialog(PopisnaLista popisnaLista) {
        String azurirajPopisnuListu = getString(R.string.azuriraj_popisnu_listu);
        String sacuvaj = getString(R.string.sacuvaj);
        String otkaziAkcija = getString(R.string.otkazi_akcija);
        String nazivObavezan = getString(R.string.naziv_obavezan);

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_dodaj_popisnu_listu, null);

        EditText etNaziv = dialogView.findViewById(R.id.et_naziv_pl);
        EditText etOpis = dialogView.findViewById(R.id.et_opis_pl);

        etNaziv.setText(popisnaLista.getNazivListe());
        etOpis.setText(popisnaLista.getOpis());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(azurirajPopisnuListu)
                .setView(dialogView)
                .setPositiveButton(sacuvaj, (d, which) -> {
                    String naziv = etNaziv.getText().toString().trim();
                    String opis = etOpis.getText().toString().trim();

                    if (!naziv.isEmpty()) {
                        String datumKreiranja = new SimpleDateFormat(DATUM_PATTERN, Locale.getDefault()).format(new Date());
                        PopisnaLista popisnaLista1 = new PopisnaLista(naziv, datumKreiranja, opis);
                        popisnaLista1.setId(popisnaLista.getId());
                        popisneListeViewModel.update(popisnaLista1);
                    } else {
                        Toast.makeText(requireContext(),
                                nazivObavezan, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(otkaziAkcija, null)
                .create();

        dialog.show();

        etNaziv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(!s.toString().trim().isEmpty());
            }
        });
    }
    private void setAlertDialog(PopisnaLista popisnaLista) {
        String odaberiAkciju = getString(R.string.odaberi_akciju);
        String azurirajAkcija = getString(R.string.azuriraj_akcija);
        String obrisiAkcija = getString(R.string.obrisi_akcija);
        String prikaziAkcija = getString(R.string.prikazi_akcija);
        String potvrdaBrisanja = getString(R.string.potvrda_brisanja);
        String potvrdaBrisanjaPitanje = getString(R.string.potvrda_brisanja_pitanje);
        String da = getString(R.string.da);
        String ne = getString(R.string.ne);
        String uspjesnoObrisano = getString(R.string.uspjesno_obrisano);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(odaberiAkciju);
//        builder.setMessage("Šta želiš učiniti sa popisnom listom?");

        builder.setPositiveButton(azurirajAkcija, (dialog, which) -> {
            prikaziUpdateListDialog(popisnaLista);
        });

        builder.setNeutralButton(obrisiAkcija, (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(potvrdaBrisanja)
                    .setMessage(potvrdaBrisanjaPitanje)
                    .setPositiveButton(da, (dialog1, which1) -> {
                        popisneListeViewModel.delete(popisnaLista);
                        stavkeViewModel.deleteStavkeByPopisnaListaId(popisnaLista.getId());
                        Toast.makeText(requireContext(), uspjesnoObrisano, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(ne, null)
                    .show();
        });

        builder.setNegativeButton(prikaziAkcija, (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(POPISNA_LISTA_ID, popisnaLista.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_nav_popisne_liste_to_nav_stavke, bundle);
        });

        builder.show();
    }

    private void filtrirajListu(String text) {
        List<PopisnaLista> filtriranaLista = new ArrayList<>();
        for(PopisnaLista pl : popisneListe) {
            if(pl.getNazivListe().toLowerCase().contains(text.toLowerCase()) || pl.getOpis().toLowerCase().contains((text.toLowerCase()))) {
                filtriranaLista.add(pl);
            }
        }

        adapter.setFiltriranePopisneListe(filtriranaLista);
    }
}