package com.example.dell.menu.reports.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.reports.objects.Report;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends Fragment {

    @Bind(R.id.dateTextView)
    TextView dateTextView;
    @Bind(R.id.vegetablesQuantity)
    TextView vegetablesQuantity;
    @Bind(R.id.fruitsQuantity)
    TextView fruitsQuantity;
    @Bind(R.id.liquidQuantity)
    TextView liquidQuantity;
    @Bind(R.id.meatQuantity)
    TextView meatQuantity;
    @Bind(R.id.fatQuantity)
    TextView fatQuantity;
    @Bind(R.id.dry_goodsQuantity)
    TextView dryGoodsQuantity;
    @Bind(R.id.spiceQuantity)
    TextView spiceQuantity;
    @Bind(R.id.dairy_productsQuantity)
    TextView dairyProductsQuantity;
    @Bind(R.id.fishQuantity)
    TextView fishQuantity;
    @Bind(R.id.bread_goodsQuantity)
    TextView breadGoodsQuantity;
    private ReportsManager reportsManager;

    public ReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        reportsManager = ((App) getActivity().getApplication()).getReportsManager();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        reportsManager.onAttach(this);
        reportsManager.loadReport();
    }

    @Override
    public void onStop() {
        super.onStop();
        reportsManager.onStop();
    }

    public void reportGenerateSuccess() {
        Toast.makeText(getActivity(), "New report generated", Toast.LENGTH_SHORT).show();
    }

    public void reportGenerateFailed() {
        Toast.makeText(getActivity(), "Error while generating report", Toast.LENGTH_SHORT).show();
    }

    public void showReport(Report report) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        dateTextView.setText(dateFormat.format(new Date()));
        vegetablesQuantity.setText(String.valueOf(report.getValues(Report.VEGETABLES)));
        fruitsQuantity.setText(String.valueOf(report.getValues(Report.FRUITS)));
        liquidQuantity.setText(String.valueOf(report.getValues(Report.LIQUID)));
        meatQuantity.setText(String.valueOf(report.getValues(Report.MEAT)));
        fatQuantity.setText(String.valueOf(report.getValues(Report.FAT)));
        dryGoodsQuantity.setText(String.valueOf(report.getValues(Report.DRY_GOODS)));
        spiceQuantity.setText(String.valueOf(report.getValues(Report.SPICE)));
        dairyProductsQuantity.setText(String.valueOf(report.getValues(Report.DAIRY_PRODUCTS)));
        fishQuantity.setText(String.valueOf(report.getValues(Report.FISH)));
        breadGoodsQuantity.setText(String.valueOf(report.getValues(Report.BAKED_GOODS)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
