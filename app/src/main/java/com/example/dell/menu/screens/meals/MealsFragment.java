package com.example.dell.menu.screens.meals;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.objects.Meal;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 27.05.2017.
 */

public class MealsFragment extends Fragment {

    @Bind(R.id.mealsRecyclerView)
    RecyclerView mealsRecyclerView;
    private MealsFragmentManager mealsFragmentManager;
    private MealsAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        mealsFragmentManager.onAttach(this);
        mealsFragmentManager.loadMeals();
    }

    @Override
    public void onStop() {
        super.onStop();
        mealsFragmentManager.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MealsAdapter();
        mealsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealsFragmentManager = ((App) getActivity().getApplication()).getMealsFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meals, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void showMeals(List<Meal> result){
        adapter.setMeals(result);
        adapter.notifyDataSetChanged();
    }
}
