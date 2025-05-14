package org.unibl.etf.mr_projektni_zadatak;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.unibl.etf.mr_projektni_zadatak.databinding.ActivityMainBinding;
import org.unibl.etf.mr_projektni_zadatak.registar_db.RegistarDatabase;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private RegistarDatabase registarDatabase;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public RegistarDatabase getRegistarDatabase() {
        return registarDatabase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppLanguage();

        super.onCreate(savedInstanceState);

        registarDatabase = RegistarDatabase.getInstance(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String poruka = getString(R.string.dobrodosli);
        if (savedInstanceState == null) {
            Toast.makeText(this, poruka, Toast.LENGTH_SHORT).show();
        }

        setSupportActionBar(binding.appBarMain.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_osnovna_sredstva, R.id.nav_zaposleni, R.id.nav_lokacije, R.id.nav_popisne_liste, R.id.nav_podesavanja)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//

        binding.appBarMain.toolbar.setNavigationOnClickListener(v -> {
            if (navController.getCurrentDestination() != null &&
                    (navController.getCurrentDestination().getId() == R.id.nav_osnovna_sredstva ||
                            navController.getCurrentDestination().getId() == R.id.nav_zaposleni ||
                            navController.getCurrentDestination().getId() == R.id.nav_lokacije ||
                            navController.getCurrentDestination().getId() == R.id.nav_popisne_liste ||
                            navController.getCurrentDestination().getId() == R.id.nav_podesavanja)) {
                drawer.openDrawer(GravityCompat.START);
            } else {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setAppLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString(SELECTED_LANGUAGE, Locale.getDefault().getLanguage());
        setLocale(this, lang);
    }

    public void changeLanguage(String languageCode) {
        setLocale(this, languageCode);
        saveLanguagePreference(languageCode);
        recreate();
    }

    private void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }
    private void saveLanguagePreference(String languageCode) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(SELECTED_LANGUAGE, languageCode);
        editor.apply();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String lang = preferences.getString(SELECTED_LANGUAGE, Locale.getDefault().getLanguage());

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}