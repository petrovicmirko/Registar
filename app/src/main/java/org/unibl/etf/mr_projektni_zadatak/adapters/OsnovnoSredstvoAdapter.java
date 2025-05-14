package org.unibl.etf.mr_projektni_zadatak.adapters;


import static androidx.core.content.ContextCompat.getString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OsnovnoSredstvoAdapter extends RecyclerView.Adapter<OsnovnoSredstvoAdapter.OsnovnoSredstvoViewHolder> {

    private List<OsnovnoSredstvo> osnovnaSredstva;
    private OnItemClickListener listener;
    private Context context;

    public OsnovnoSredstvoAdapter(List<OsnovnoSredstvo> osnovnaSredstva, Context context, OnItemClickListener listener) {
        this.osnovnaSredstva = osnovnaSredstva;
        this.listener = listener;
        this.context = context;
    }

    public void setFiltriranaOsnovnaSredstva(List<OsnovnoSredstvo> filtriranaOsnovnaSredstva) {
        this.osnovnaSredstva = filtriranaOsnovnaSredstva;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OsnovnoSredstvoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext()).inflate(R.layout.osnovno_sredstvo_item, parent, false);

        return new OsnovnoSredstvoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OsnovnoSredstvoViewHolder holder, int position) {
        OsnovnoSredstvo osnovnoSredstvo = osnovnaSredstva.get(position);

        String valuta = context.getString(R.string.valuta);

        holder.tvNaziv.setText(osnovnoSredstvo.getNaziv());
        holder.tvCijena.setText(osnovnoSredstvo.getCijena() + " " + valuta);
        //holder.ivSlika.setImageURI(Uri.parse(osnovnoSredstvo.getSlika()));

        holder.btnOsnovnoSredstvoVise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViseClick(osnovnoSredstvo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return osnovnaSredstva.size();
    }

    public static class OsnovnoSredstvoViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNaziv;
        public TextView tvCijena;
        public ImageView ivSlika;
        private Button btnOsnovnoSredstvoVise;

        public OsnovnoSredstvoViewHolder(View view) {
            super(view);
            tvNaziv = view.findViewById(R.id.tv_naziv_osnovnog_sredstva);
            tvCijena = view.findViewById(R.id.tv_cijena_osnovnog_sredstva);
            //ivSlika = view.findViewById(R.id.iv_slika_osnovnog_sredstva);
            btnOsnovnoSredstvoVise = view.findViewById(R.id.btn_osnovno_sredstvo_vise);
        }
    }

    public interface OnItemClickListener {
        void onViseClick(OsnovnoSredstvo osnovnoSredstvo);
    }
}
