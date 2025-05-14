package org.unibl.etf.mr_projektni_zadatak.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;

import java.util.List;

public class OsobaAdapter extends RecyclerView.Adapter<OsobaAdapter.OsobaViewHolder> {
    private List<Osoba> osobe;
    private OnItemClickListener listener;

    public OsobaAdapter(List<Osoba> osobe, OnItemClickListener listener) {
        this.osobe = osobe;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OsobaAdapter.OsobaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.osoba_item, parent, false);
        return new OsobaAdapter.OsobaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OsobaAdapter.OsobaViewHolder holder, int position) {
        Osoba osoba = osobe.get(position);

        String imeOsobe = osoba.getIme() + " " + osoba.getPrezime();

        holder.tvOsoba.setText(imeOsobe);
        holder.tvPozicija.setText(osoba.getPozicija());

        holder.btnOsobaVise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViseClick(osoba);
            }
        });
    }

    @Override
    public int getItemCount() {
        return osobe.size();
    }

    public void setFiltriraniZaposleni(List<Osoba> filtriranaLista) {
        this.osobe = filtriranaLista;
        notifyDataSetChanged();
    }

    public static class OsobaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOsoba;
        private TextView tvPozicija;
        private Button btnOsobaVise;

        public OsobaViewHolder(View view) {
            super(view);
            tvOsoba = view.findViewById(R.id.tv_osoba);
            tvPozicija = view.findViewById(R.id.tv_pozicija);
            btnOsobaVise = view.findViewById(R.id.btn_osoba_vise);
        }
    }

    public interface OnItemClickListener {
        void onViseClick(Osoba osoba);
    }
}
