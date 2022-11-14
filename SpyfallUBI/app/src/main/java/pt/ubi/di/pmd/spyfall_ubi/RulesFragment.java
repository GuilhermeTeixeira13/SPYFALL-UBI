package pt.ubi.di.pmd.spyfall_ubi;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class RulesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.RulesFragment));
        return inflater.inflate(R.layout.fragment_rules, container, false);
    }
}
