package com.example.dell.menu.virtualfridge.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.menu.App;
import com.example.dell.menu.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 28.10.2017.
 */

public class VirtualFridgeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    public static final String ADD_TO_FRIDGE_KEY = "Add to fridge";
    @Bind(R.id.shelvesTabLayout)
    TabLayout shelvesTabLayout;
    @Bind(R.id.shelvesViewPager)
    ViewPager shelvesViewPager;
    @Bind(R.id.messageTextView)
    TextView messageTextView;
    @Bind(R.id.startFromEditText)
    EditText startFromEditText;
    @Bind(R.id.endEditText)
    EditText endEditText;
    @Bind(R.id.okButton)
    Button okButton;
    @Bind(R.id.choosePeriodRelativeLayout)
    RelativeLayout choosePeriodRelativeLayout;

    EditText currentEditText;
    @Bind(R.id.startOfPeriodLabelTextView)
    TextView startOfPeriodLabelTextView;
    @Bind(R.id.endOfPeriodLabelTextView)
    TextView endOfPeriodLabelTextView;
    @Bind(R.id.startOfPeriodImageButton)
    ImageButton startOfPeriodImageButton;
    @Bind(R.id.endOfPeriodCalendarImageButton)
    ImageButton endOfPeriodCalendarImageButton;
    @Bind(R.id.extraShelfButton)
    Button extraShelfButton;
    private VirtualFridgeManager manager;

    private Date startOfPeriod, endOfPeriod;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = ((App) getActivity().getApplication()).getVirtualFridgeManager();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_fridge, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Virtual Fridge");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_virtual_fridge, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            addNewProduct();
            return true;
        } else if (item.getItemId() == R.id.choosePeriodToDisplay) {
            manager.clearShelves();
            setAdapter();
            shelvesViewPager.setVisibility(View.GONE);
            choosePeriodRelativeLayout.setVisibility(View.VISIBLE);


            setLayoutToChoosePeriod();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewProduct() {
        // TODO: 29.11.2017 nalezy podac, do ktorego dnia ma byc dodany produkt
        //Intent intent = new Intent(getActivity(), AddProductActivity.class);
        //intent.putExtra(ADD_TO_FRIDGE_KEY, true);
        //startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        manager.onAttach(this);
        setLayoutToChoosePeriod();
    }

    @Override
    public void onStop() {
        super.onStop();
        manager.onStop();

    }


    private void setLayoutToChoosePeriod() {
        messageTextView.setText("Choose the extra shelf (that is not connected to any day) or " +
                "the period to display content of virtual fridge. \n\n" +
                "*Shelves from past are being kept in stock only for 3 months.");
    }

    public void setAdapter() {
        shelvesViewPager.setVisibility(View.VISIBLE);
        choosePeriodRelativeLayout.setVisibility(View.GONE);
        ShelvesPagerAdapter adapter =
                new ShelvesPagerAdapter(getActivity().getSupportFragmentManager(),
                        manager.getShelves());
        shelvesViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        shelvesTabLayout.setupWithViewPager(shelvesViewPager);
    }

    public void fridgeEmpty() {
        Toast.makeText(getContext(), "Your fridge is empty", Toast.LENGTH_LONG).show();
    }

    public void loadingContentFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to load the content",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void updateSuccess() {
        Toast.makeText(getContext(), "Successfully updated quantity of product", Toast.LENGTH_SHORT).show();
        manager.loadContent(endOfPeriod, startOfPeriod);
    }

    public void updateFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to update quantity of " +
                "product", Toast.LENGTH_LONG).show();
    }

    public void deleteSuccess() {
        Toast.makeText(getContext(), "Successfully deleted product", Toast.LENGTH_SHORT).show();
        manager.loadContent(endOfPeriod, startOfPeriod);
    }

    public void deleteFailed() {
        Toast.makeText(getContext(), "An error occurred while an attempt to delete product",
                Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.extraShelfButton)
    public void onExtraShelfButtonClicked(){
        manager.loadExtraShelf();
    }

    @OnClick(R.id.okButton)
    public void onOkButtonClicked() {
        boolean hasErrors = false;

        if (startFromEditText.length() == 0) {
            hasErrors = true;
            Toast.makeText(getContext(), "You must choose start date!", Toast.LENGTH_SHORT).show();
        }

        if (endEditText.length() == 0) {
            hasErrors = true;
            Toast.makeText(getContext(), "You must choose end date!", Toast.LENGTH_SHORT).show();
        }

        if (!hasErrors) {
            if (endOfPeriod.before(startOfPeriod)) {
                manager.loadContent(endOfPeriod, startOfPeriod);
            } else manager.loadContent(startOfPeriod, endOfPeriod);
        }
    }

    private void pickDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = String.format("%s-%s-%s", dayOfMonth, month + 1, year);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        if (currentEditText == startFromEditText) {
            startFromEditText.setText(date);
            startOfPeriod = calendar.getTime();
            startOfPeriodLabelTextView.setVisibility(View.VISIBLE);
        } else if (currentEditText == endEditText) {
            endEditText.setText(date);
            endOfPeriod = calendar.getTime();
            endOfPeriodLabelTextView.setVisibility(View.VISIBLE);
        }

    }

    @OnClick({R.id.startOfPeriodImageButton, R.id.endOfPeriodCalendarImageButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.startOfPeriodImageButton:
                currentEditText = startFromEditText;
                pickDate();
                break;
            case R.id.endOfPeriodCalendarImageButton:
                currentEditText = endEditText;
                pickDate();
                break;
        }
    }

    public void emptyExtraShelf() {
        Toast.makeText(getContext(), "You extra shelf is empty! Add some products",
                Toast.LENGTH_LONG).show();
    }
}
