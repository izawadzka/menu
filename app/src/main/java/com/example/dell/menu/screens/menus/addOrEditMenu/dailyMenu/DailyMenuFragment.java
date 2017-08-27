package com.example.dell.menu.screens.menus.addOrEditMenu.dailyMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.example.dell.menu.R;
import com.example.dell.menu.colors.ColorsBase;
import com.example.dell.menu.objects.DailyMenu;
import com.example.dell.menu.objects.Meal;
import com.example.dell.menu.screens.menus.addOrEditMenu.DailyMenusActivity;

import java.util.ArrayList;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 03.08.2017.
 */

public class DailyMenuFragment extends Fragment {


    public static final String BREAKFAST_KEY = "breakfast";
    public static final String LUNCH_KEY = "lunch";
    public static final String DINNER_KEY = "dinner";
    public static final String TEATIME_KEY = "teatime";
    public static final String SUPPER_KEY = "supper";
    public static final String MEAL_TYPE_KEY = "mealType";
    public static final String DAILY_MENU_ID_KEY = "dailyMenuId";
    public static final int RESULT_OK = 0;
    public static final int REQUEST_CODE_ADD = 1;
    public static final String EDIT_MODE_KEY = "edit_mode";

    @Bind(R.id.dateTextView)
    TextView dateTextView;
    @Bind(R.id.breakfastTags)
    TagView breakfastTags;
    @Bind(R.id.lunchTags)
    TagView lunchTags;
    @Bind(R.id.dinnerTags)
    TagView dinnerTags;
    @Bind(R.id.teatimeTags)
    TagView teatimeTags;
    @Bind(R.id.supperTags)
    TagView supperTags;
    @Bind(R.id.editDailyMenuButton)
    Button editDailyMenuButton;
    @Bind(R.id.kcalLabelTextView)
    TextView kcalLabelTextView;
    @Bind(R.id.kcalTextView)
    TextView kcalTextView;
    @Bind(R.id.deleteDailyMenuButton)
    Button deleteDailyMenuButton;

    private DailyMenu dailyMenu;
    public static final String DAILY_MENU_KEY = "dailyMenu";


    ArrayList<Tag> breakfastTagList = new ArrayList<>();
    ArrayList<Tag> lunchTagList = new ArrayList<>();
    ArrayList<Tag> dinnerTagList = new ArrayList<>();
    ArrayList<Tag> teatimeTagList = new ArrayList<>();
    ArrayList<Tag> supperTagList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_menu, container, false);
        ButterKnife.bind(this, view);

        dailyMenu = (DailyMenu) getArguments().getSerializable(DAILY_MENU_KEY);

        return view;
    }

    public static DailyMenuFragment newInstance(DailyMenu dailyMenu) {
        Bundle args = new Bundle();
        args.putSerializable(DAILY_MENU_KEY, dailyMenu);
        DailyMenuFragment fragment = new DailyMenuFragment();
        fragment.setArguments(args);  //saving a daily menu while creating new instance of fragment
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnTagClickListener(breakfastTags, BREAKFAST_KEY);
        setOnTagClickListener(lunchTags, LUNCH_KEY);
        setOnTagClickListener(dinnerTags, DINNER_KEY);
        setOnTagClickListener(teatimeTags, TEATIME_KEY);
        setOnTagClickListener(supperTags, SUPPER_KEY);

        showDailyMenu();
        setShowMode();
    }

    private void setOnTagClickListener(TagView tagView, final String mealType) {

        tagView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                // TODO: 15.08.2017
                Log.d("Tag", "klikniety");
            }
        });
    }

    private void setShowMode() {
        getState();
        dateTextView.setInputType(InputType.TYPE_NULL);
    }


    private void getState() {
        dateTextView.setText(dailyMenu.getDate());
        kcalTextView.setText(String.valueOf(dailyMenu.getCumulativeNumberOfKcal()) + "kcal");
    }

    private void showDailyMenu() {
        dateTextView.setText(dailyMenu.getDate());

        setTags(breakfastTags, dailyMenu.getBreakfast(), breakfastTagList);
        setTags(lunchTags, dailyMenu.getLunch(), lunchTagList);
        setTags(dinnerTags, dailyMenu.getDinner(), dinnerTagList);
        setTags(teatimeTags, dailyMenu.getTeatime(), teatimeTagList);
        setTags(supperTags, dailyMenu.getSupper(), supperTagList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.editDailyMenuButton)
    public void onWideButtonClicked(View view) {
        Intent intent = new Intent(getActivity(), CreateNewDailyMenuActivity.class);
        intent.putExtra(EDIT_MODE_KEY, true);
        Bundle args = new Bundle();
        args.putSerializable(DAILY_MENU_KEY, dailyMenu);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void setTags(TagView tagView, Vector<Meal> meals, ArrayList<Tag> tags) {
        tags.clear();

        for (int i = 0; i < meals.size(); i++) {
            tags.add(new Tag(meals.get(i).getName()));
            tags.get(i).layoutColor = ColorsBase.getRandomColor();
        }
        tagView.addTags(tags);
    }

    @OnClick(R.id.deleteDailyMenuButton)
    public void onDeleteClicked() {
        ((DailyMenusActivity)getActivity()).deleteDailyMenu(dailyMenu.getDailyMenuId());
    }
}
