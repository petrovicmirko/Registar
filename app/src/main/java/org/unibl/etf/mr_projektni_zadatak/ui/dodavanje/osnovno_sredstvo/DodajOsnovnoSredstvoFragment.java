package org.unibl.etf.mr_projektni_zadatak.ui.dodavanje.osnovno_sredstvo;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.unibl.etf.mr_projektni_zadatak.MainActivity;
import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.ui.lokacije.LokacijeViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva.OsnovnaSredstvaViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.zaposleni.ZaposleniViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DodajOsnovnoSredstvoFragment extends Fragment {
    public static final String OSNOVNO_SREDSTVO_ID = "osnovnoSredstvoId";
    public static final String DATUM_PATTERN = "dd.MM.yyyy.";
    public static final String TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss";
    public static final String NAME_PATTERN_1 = "JPEG_";
    public static final String NAME_PATTERN_2 = ".jpg";
    private EditText etNaziv, etOpis, etBarkod, etCijena;
    private Spinner spinnerZaposleni, spinnerLokacija;
    private ImageView ivSlika;
    private Button btnSkenirajBarkod, btnOdaberiSliku, btnUslikaj, btnSacuvaj;

    private List<Integer> lokacijeIds = new ArrayList<>();
    private List<String> gradovi = new ArrayList<>();
    private List<Integer> zaposleniIds = new ArrayList<>();
    private List<String> zaposleni = new ArrayList<>();

    private LokacijeViewModel lokacijeViewModel;
    private ZaposleniViewModel zaposleniViewModel;
    private OsnovnaSredstvaViewModel osnovnaSredstvaViewModel;

    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1001;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int IMAGE_CAPTURE_REQUEST = 2;
    private String imagePath;
    private String currentPhotoPath;
    private Bitmap tempBitmap;

    private View root;

    private DodajOsnovnoSredstvoViewModel viewModel;

    public static DodajOsnovnoSredstvoFragment newInstance() {
        return new DodajOsnovnoSredstvoFragment();
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
        root = inflater.inflate(R.layout.fragment_dodaj_osnovno_sredstvo, container, false);

        etNaziv = root.findViewById(R.id.et_naziv_os);
        etOpis = root.findViewById(R.id.et_opis_os);
        etBarkod = root.findViewById(R.id.et_barkod_os);
        etCijena = root.findViewById(R.id.et_cijena_os);
        spinnerZaposleni = root.findViewById(R.id.spinner_zaposleni_os);
        spinnerLokacija = root.findViewById(R.id.spinner_lokacija_os);
        ivSlika = root.findViewById(R.id.iv_slika_os);
        btnSkenirajBarkod = root.findViewById(R.id.btn_skeniraj_barkod_os);
        btnOdaberiSliku = root.findViewById(R.id.btn_odaberi_sliku_os);
        btnUslikaj = root.findViewById(R.id.btn_uslikaj_os);
        btnSacuvaj = root.findViewById(R.id.btn_sacuvaj_os);

        viewModel = new ViewModelProvider(this).get(DodajOsnovnoSredstvoViewModel.class);

        btnSacuvajSetOnClickListener();
        btnOdaberiSlikuSetOnClickListener();
        btnSkenirajBarkod.setOnClickListener(v -> pokreniSkener());
        btnUslikajSetOnClickListener();

        spinnerZaposleniSetOnClickListener();
        spinnerLokacijeSetOnClickListener();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lokacijeViewModel = new ViewModelProvider(this).get(LokacijeViewModel.class);
        zaposleniViewModel = new ViewModelProvider(this).get(ZaposleniViewModel.class);

        String odaberiLokaciju = getString(R.string.odaberi_lokaciju);
        String odaberiZaposlenog = getString(R.string.odaberi_zaposlenog);

        osnovnaSredstvaViewModel = new ViewModelProvider(this).get(OsnovnaSredstvaViewModel.class);

        lokacijeViewModel.getLokacije().observe(getViewLifecycleOwner(), lokacijeList -> {
            lokacijeIds.clear();
            gradovi.clear();

            gradovi.add(odaberiLokaciju);
            lokacijeIds.add(-1);

            for (Lokacija lokacija : lokacijeList) {
                lokacijeIds.add(lokacija.getId());
                gradovi.add(lokacija.getGrad());
            }

            ArrayAdapter<String> lokacijeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, gradovi);
            lokacijeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLokacija.setAdapter(lokacijeAdapter);

            if (getArguments() != null && getArguments().containsKey(OSNOVNO_SREDSTVO_ID)) {
                int osnovnoSredstvoId = getArguments().getInt(OSNOVNO_SREDSTVO_ID);

                osnovnaSredstvaViewModel.getOsnovnoSredstvoById(osnovnoSredstvoId).observe(getViewLifecycleOwner(), osnovnoSredstvo -> {
                    if (osnovnoSredstvo != null) {
                        int lokacijaPosition = lokacijeIds.indexOf(osnovnoSredstvo.getLokacijaId());
                        if (lokacijaPosition != -1) {
                            spinnerLokacija.setSelection(lokacijaPosition);
                        } else {
                            Log.e("SpinnerError", "Lokacija ID " + osnovnoSredstvo.getLokacijaId() + " nije pronađena u listi.");
                        }
                    }
                });
            }
        });

        zaposleniViewModel.getZaposlene().observe(getViewLifecycleOwner(), zaposleniList -> {
            zaposleniIds.clear();
            zaposleni.clear();

            zaposleni.add(odaberiZaposlenog);
            zaposleniIds.add(-1);

            for (Osoba osoba : zaposleniList) {
                zaposleniIds.add(osoba.getId());
                zaposleni.add(osoba.getIme() + " " + osoba.getPrezime());
            }

            ArrayAdapter<String> zaposleniAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, zaposleni);
            zaposleniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerZaposleni.setAdapter(zaposleniAdapter);

            if (getArguments() != null && getArguments().containsKey(OSNOVNO_SREDSTVO_ID)) {
                int osnovnoSredstvoId = getArguments().getInt(OSNOVNO_SREDSTVO_ID);

                osnovnaSredstvaViewModel.getOsnovnoSredstvoById(osnovnoSredstvoId).observe(getViewLifecycleOwner(), osnovnoSredstvo -> {
                    if (osnovnoSredstvo != null) {
                        int zaposleniPosition = zaposleniIds.indexOf(osnovnoSredstvo.getZaposleniId());
                        if (zaposleniPosition != -1) {
                            spinnerZaposleni.setSelection(zaposleniPosition);
                        } else {
                            Log.e("SpinnerError", "Zaposleni ID " + osnovnoSredstvo.getZaposleniId() + " nije pronađen u listi.");
                        }
                    }
                });
            }
        });

        if (getArguments() != null && getArguments().containsKey(OSNOVNO_SREDSTVO_ID)) {
            int osnovnoSredstvoId = getArguments().getInt(OSNOVNO_SREDSTVO_ID);

            osnovnaSredstvaViewModel.getOsnovnoSredstvoById(osnovnoSredstvoId).observe(getViewLifecycleOwner(), osnovnoSredstvo -> {
                if(osnovnoSredstvo != null) {
                    etNaziv.setText(osnovnoSredstvo.getNaziv());
                    etOpis.setText(osnovnoSredstvo.getOpis());
                    etBarkod.setText(String.valueOf(osnovnoSredstvo.getBarkod()));
                    etCijena.setText(osnovnoSredstvo.getCijena());

                    if (osnovnoSredstvo.getSlika() != null && !osnovnoSredstvo.getSlika().isEmpty()) {
                        File imageFile = new File(osnovnoSredstvo.getSlika());
                        if (imageFile.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            ivSlika.setImageBitmap(bitmap);
                            tempBitmap = bitmap;
                        }
                    }
                }
            });
        }
    }

    public void btnSacuvajSetOnClickListener() {
        String popunitePolja = getString(R.string.popunite_polja);

        btnSacuvaj.setOnClickListener(v -> {
            String naziv = etNaziv.getText().toString();
            String opis = etOpis.getText().toString();
            String barkodText = etBarkod.getText().toString();
            String cijena = etCijena.getText().toString();

            int selectedLokacijaPosition = spinnerLokacija.getSelectedItemPosition();
            int selectedZaposleniPosition = spinnerZaposleni.getSelectedItemPosition();

            if (naziv.isEmpty() || opis.isEmpty() || barkodText.isEmpty() || cijena.isEmpty()) {
                Toast.makeText(requireContext(), popunitePolja, Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedLokacijaPosition == 0) {
                String odaberiteLokaciju = getString(R.string.odaberite_lokaciju);
                Toast.makeText(requireContext(), odaberiteLokaciju, Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedZaposleniPosition == 0) {
                String odaberiteZaposlenog = getString(R.string.odaberite_zaposlenog);
                Toast.makeText(requireContext(), odaberiteZaposlenog, Toast.LENGTH_SHORT).show();
                return;
            }

            long barkod;
            try {
                barkod = Long.parseLong(barkodText);
            } catch (NumberFormatException e) {
                String barkodUslov = getString(R.string.barkod_uslov);
                Toast.makeText(requireContext(), barkodUslov, Toast.LENGTH_SHORT).show();
                return;
            }

            if (getArguments() != null && getArguments().containsKey(OSNOVNO_SREDSTVO_ID)) {
                int currentId = getArguments().getInt(OSNOVNO_SREDSTVO_ID);
                new Thread(() -> {
                    OsnovnoSredstvo existingWithSameBarkod = osnovnaSredstvaViewModel.checkBarkod(barkodText);

                    requireActivity().runOnUiThread(() -> {
                        if (existingWithSameBarkod != null && existingWithSameBarkod.getId() != currentId) {
                            etBarkod.setText("");
                            String barkodPostoji = getString(R.string.barkod_postoji);
                            String ok = getString(R.string.ok);
                            Snackbar.make(requireView(),
                                            barkodPostoji,
                                            Snackbar.LENGTH_LONG)
                                    .setAction(ok, v1 -> {})
                                    .show();
                        } else {
                            sacuvajOsnovnoSredstvo(naziv, opis, barkodText, cijena,
                                    selectedLokacijaPosition, selectedZaposleniPosition);
                        }
                    });
                }).start();
            }
            else {
                new Thread(() -> {
                    OsnovnoSredstvo existing = osnovnaSredstvaViewModel.checkBarkod(barkodText);

                    requireActivity().runOnUiThread(() -> {
                        if (existing != null) {
                            etBarkod.setText("");
                            String osnovnoSredstvoPostoji = getString(R.string.osnovno_sredstvo_postoji);
                            String ok = getString(R.string.ok);
                            Snackbar.make(requireView(),
                                            osnovnoSredstvoPostoji,
                                            Snackbar.LENGTH_LONG)
                                    .setAction(ok, v1 -> {})
                                    .show();
                        } else {
                            sacuvajOsnovnoSredstvo(naziv, opis, barkodText, cijena,
                                    selectedLokacijaPosition, selectedZaposleniPosition);
                        }
                    });
                }).start();
            }
        });
    }

    private void sacuvajOsnovnoSredstvo(String naziv, String opis, String barkodText, String cijena,
                                     int selectedLokacijaPosition, int selectedZaposleniPosition) {
        int selectedLokacijaId = lokacijeIds.get(selectedLokacijaPosition);
        int selectedZaposleniId = zaposleniIds.get(selectedZaposleniPosition);

        String datumKreiranja = new SimpleDateFormat(DATUM_PATTERN, Locale.getDefault()).format(new Date());

        if (tempBitmap == null) {
            String dodajFotografijuUslov = getString(R.string.dodaj_fotografiju_uslov);
            Toast.makeText(requireContext(), dodajFotografijuUslov, Toast.LENGTH_SHORT).show();
            return;
        }

        imagePath = sacuvajSlikuUInternuMemoriju(tempBitmap);
        if (imagePath == null) {
            String dodajFotografijuGreska = getString(R.string.dodaj_fotografiju_greska);
            Toast.makeText(requireContext(), dodajFotografijuGreska, Toast.LENGTH_SHORT).show();
            return;
        }

        OsnovnoSredstvo osnovnoSredstvo = new OsnovnoSredstvo(
                naziv, opis, Long.parseLong(barkodText), cijena,
                datumKreiranja, selectedZaposleniId, selectedLokacijaId, imagePath
        );

        if (getArguments() != null && getArguments().containsKey(OSNOVNO_SREDSTVO_ID)) {
            int osnovnoSredstvoId = getArguments().getInt(OSNOVNO_SREDSTVO_ID);
            osnovnoSredstvo.setId(osnovnoSredstvoId);
            osnovnaSredstvaViewModel.update(osnovnoSredstvo);
        } else {
            osnovnaSredstvaViewModel.insert(osnovnoSredstvo);
        }

        Navigation.findNavController(requireView()).popBackStack();
        //Navigation.findNavController(root).navigate(R.id.action_nav_dodaj_osnovno_sredstvo_to_nav_osnovna_sredstva);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if(result.getContents() != null) {
                    String barkod = result.getContents();
                    etBarkod.setText(result.getContents());
                }
            });

    private void pokreniSkener() {
        String skenirajKod = getString(R.string.skeniraj_kod);
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt(skenirajKod);
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private void btnOdaberiSlikuSetOnClickListener() {
        String odaberiteFotografiju = getString(R.string.odaberite_fotografiju);
        String type = "image/*";
        btnOdaberiSliku.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType(type);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, odaberiteFotografiju), PICK_IMAGE_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    File photoFile = kreirajFajlZaSliku();
                    int size = 1024;
                    currentPhotoPath = photoFile.getAbsolutePath();

                    InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                    FileOutputStream outputStream = new FileOutputStream(photoFile);
                    byte[] buffer = new byte[size];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    inputStream.close();

                    tempBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                    tempBitmap = rotateImageIfRequired(tempBitmap, currentPhotoPath);
                    ivSlika.setImageBitmap(tempBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    String ucitavanjeFotografijeGreska = getString(R.string.ucitavanje_fotografije_greska);
                    Toast.makeText(requireContext(), ucitavanjeFotografijeGreska, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == IMAGE_CAPTURE_REQUEST) {
                if (currentPhotoPath != null) {
                    File photoFile = new File(currentPhotoPath);
                    if (photoFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        if (bitmap != null) {
                            tempBitmap = bitmap;
                            tempBitmap = rotateImageIfRequired(tempBitmap, currentPhotoPath);
                            ivSlika.setImageBitmap(tempBitmap);
                        } else {
                            String ucitavanjeFotografijeGreska = getString(R.string.ucitavanje_fotografije_greska);
                            Toast.makeText(requireContext(), ucitavanjeFotografijeGreska, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String dodajFotografijuGreska = getString(R.string.dodaj_fotografiju_greska);
                        Toast.makeText(requireContext(), dodajFotografijuGreska, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String putanjaFotografijeGreska = getString(R.string.putanja_fotografije_greska);
                    Toast.makeText(requireContext(), putanjaFotografijeGreska, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            String operacijaOtkazana = getString(R.string.operacija_otkazana);
            Toast.makeText(requireContext(), operacijaOtkazana, Toast.LENGTH_SHORT).show();
        }
    }
    private void btnUslikajSetOnClickListener() {
        btnUslikaj.setOnClickListener(v -> {
            provjeriDozvoluZaKameruIOtvoriKameru();
        });
    }

    private String sacuvajSlikuUInternuMemoriju(Bitmap bitmap) {
        String child = "images";
        File imagesDir = new File(requireContext().getFilesDir(), child);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat(TIMESTAMP_PATTERN, Locale.getDefault()).format(new Date());
        String imageFileName = NAME_PATTERN_1 + timeStamp + NAME_PATTERN_2;
        File imageFile = new File(imagesDir, imageFileName);
        int quality = 100;

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void otvoriKameruIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = kreirajFajlZaSliku();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                String fileProvider = ".fileprovider";
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + fileProvider,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST);
            }
        }
    }

    private File kreirajFajlZaSliku() throws IOException {
        String timeStamp = new SimpleDateFormat(TIMESTAMP_PATTERN, Locale.getDefault()).format(new Date());
        String imageFileName = NAME_PATTERN_1 + timeStamp + "_";
        String child = "images";

        File storageDir = new File(requireContext().getFilesDir(), child);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,
                NAME_PATTERN_2,
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void provjeriDozvoluZaKameruIOtvoriKameru() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            otvoriKameruIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                otvoriKameruIntent();
            } else {
                String kameraUslov = getString(R.string.kamera_uslov);
                Toast.makeText(requireContext(), kameraUslov, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap, String imagePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void spinnerZaposleniSetOnClickListener() {
        spinnerZaposleni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Toast.makeText(requireContext(), "Molimo odaberite zaposlenog!", Toast.LENGTH_SHORT).show();
                } else {
                    int selectedZaposleniId = zaposleniIds.get(position);
                    String odabraniZaposleni = zaposleni.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void spinnerLokacijeSetOnClickListener() {
        spinnerLokacija.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Toast.makeText(requireContext(), "Molimo odaberite lokaciju!", Toast.LENGTH_SHORT).show();
                } else {
                    int selectedLokacijaId = lokacijeIds.get(position);
                    String odabraniGrad = gradovi.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}