package org.unibl.etf.mr_projektni_zadatak.ui.detalji.osnovno_sredstvo;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.ui.lokacije.LokacijeViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva.OsnovnaSredstvaViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.zaposleni.ZaposleniViewModel;

import java.io.File;

public class DetaljiOsnovnogSredstvaFragment extends Fragment {
    public static final String OSNOVNO_SREDSTVO_ID = "osnovnoSredstvoId";
    public static final String LOKACIJA_ID = "lokacijaId";
    private ImageView ivSlika;
    private TextView tvNaziv, tvOpis, tvBarkod, tvCijena, tvDatumKreiranja, tvZaposleni, tvLokacija;
    private Button btnMapa;
    private OsnovnoSredstvo osnovnoSredstvo;
    private OsnovnaSredstvaViewModel osnovnaSredstvaViewModel;
    private ZaposleniViewModel zaposleniViewModel;
    private LokacijeViewModel lokacijeViewModel;
    private View root;

    public static DetaljiOsnovnogSredstvaFragment newInstance() {
        return new DetaljiOsnovnogSredstvaFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireView()).popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_detalji_osnovnog_sredstva, container, false);

        ivSlika = root.findViewById(R.id.iv_slika_detalji_os);
        tvNaziv = root.findViewById(R.id.tv_naziv_detalji_os);
        tvOpis = root.findViewById(R.id.tv_opis_detalji_os);
        tvBarkod = root.findViewById(R.id.tv_barkod_detalji_os);
        tvCijena = root.findViewById(R.id.tv_cijena_detalji_os);
        tvDatumKreiranja = root.findViewById(R.id.tv_datum_kreiranja_detalji_os);
        tvZaposleni = root.findViewById(R.id.tv_zaposleni_detalji_os);
        tvLokacija = root.findViewById(R.id.tv_lokacija_detalji_os);
        btnMapa = root.findViewById(R.id.btn_mapa_detalji_os);

        osnovnaSredstvaViewModel = new ViewModelProvider(this).get(OsnovnaSredstvaViewModel.class);
        zaposleniViewModel = new ViewModelProvider(this).get(ZaposleniViewModel.class);
        lokacijeViewModel = new ViewModelProvider(this).get(LokacijeViewModel.class);

        if (getArguments() != null && getArguments().containsKey(OSNOVNO_SREDSTVO_ID)) {
            int osnovnoSredstvoId = getArguments().getInt(OSNOVNO_SREDSTVO_ID);
            osnovnaSredstvaViewModel.getOsnovnoSredstvoById(osnovnoSredstvoId).observe(getViewLifecycleOwner(), osnovnoSredstvo -> {
                if (osnovnoSredstvo != null) {
                    this.osnovnoSredstvo = osnovnoSredstvo;
                    prikaziDetalje(osnovnoSredstvo);
                }
            });
        }

        btnMapa.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(LOKACIJA_ID, osnovnoSredstvo.getLokacijaId());
            Navigation.findNavController(v).navigate(R.id.action_nav_detalji_osnovnog_sredstva_to_nav_mapa, bundle);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void prikaziDetalje(OsnovnoSredstvo osnovnoSredstvo) {
        tvNaziv.setText(osnovnoSredstvo.getNaziv());
        tvOpis.setText(osnovnoSredstvo.getOpis());
        tvBarkod.setText(String.valueOf(osnovnoSredstvo.getBarkod()));
        tvCijena.setText(osnovnoSredstvo.getCijena());
        tvDatumKreiranja.setText(osnovnoSredstvo.getDatumKreiranja());

        if (osnovnoSredstvo.getSlika() != null && !osnovnoSredstvo.getSlika().isEmpty()) {
            File imageFile = new File(osnovnoSredstvo.getSlika());
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                ivSlika.setImageBitmap(bitmap);
            }
        }

        zaposleniViewModel.getZaposleniById(osnovnoSredstvo.getZaposleniId()).observe(getViewLifecycleOwner(), zaposleni -> {
            if (zaposleni != null) {
                String imeZaposleni = zaposleni.getIme() + " " + zaposleni.getPrezime();
                tvZaposleni.setText(imeZaposleni);
            }
        });

        lokacijeViewModel.getLokacijaById(osnovnoSredstvo.getLokacijaId()).observe(getViewLifecycleOwner(), lokacija -> {
            if (lokacija != null) {
                String grad = lokacija.getGrad();
                tvLokacija.setText(grad);
            }
        });
    }
}