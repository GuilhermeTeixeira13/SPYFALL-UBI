package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class MainPageFragment extends Fragment {
    Button btnPT;
    Button btnEN;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mainpage, container, false);

        btnPT = (Button) view.findViewById(R.id.changePT);
        btnEN = (Button) view.findViewById(R.id.changeENG);

        btnPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale myLocale = new Locale("pt");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                getActivity().getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                getActivity().recreate();
            }
        });

        btnEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale myLocale = new Locale("en");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                getActivity().getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
                getActivity().recreate();
            }
        });

        return view;
    }
}
