package com.example.dell.menu.screens.reports;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.dell.menu.MenuDataBase;
import com.example.dell.menu.objects.Report;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dell on 09.06.2017.
 */

public class ReportsManager {
    private ReportsFragment reportsFragment;
    private Report report;
    public void onAttach(ReportsFragment reportsFragment){
        this.reportsFragment = reportsFragment;
    }

    public void onStop(){
        reportsFragment = null;
    }

    public void loadReport() {
        if(reportsFragment != null){
            new LoadReport().execute();
        }
    }

    class LoadReport extends AsyncTask<Void, Integer, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            MenuDataBase menuDataBase = MenuDataBase.getInstance(reportsFragment.getActivity());
            report = new Report();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

            String breakfastsReportQuery = String.format("SELECT type, sum(quantity) from Products p JOIN " +
                    "Meals_Products mp ON p.productsId = mp.productId " +
                    "WHERE mp.mealId IN" +
                    "(SELECT mealId from Breakfast WHERE dailyMenuId IN" +
                    "(SELECT dailyMenuId FROM DailyMenus WHERE " +
                    "DailyMenus.date > %s))" +
                    "GROUP BY type", dateFormat.format(new Date()));
            Cursor breakfastCursor = menuDataBase.downloadData(breakfastsReportQuery);
            if(breakfastCursor.getCount() > 0){
                breakfastCursor.moveToPosition(-1);
                while (breakfastCursor.moveToNext()){
                    report.addValues(Report.BREAKFAST_KEY, breakfastCursor.getString(0), breakfastCursor.getInt(1));
                }
            }

            String lunchesReportQuery = String.format("SELECT type, sum(quantity) from Products p JOIN " +
                    "Meals_Products mp ON p.productsId = mp.productId " +
                    "WHERE mp.mealId IN" +
                    "(SELECT mealId from Lunch WHERE dailyMenuId IN" +
                    "(SELECT dailyMenuId FROM DailyMenus WHERE " +
                    "DailyMenus.date > %s))" +
                    "GROUP BY type", dateFormat.format(new Date()));
            Cursor lunchCursor = menuDataBase.downloadData(lunchesReportQuery);
            if(lunchCursor.getCount() > 0){
                lunchCursor.moveToPosition(-1);
                while (lunchCursor.moveToNext()){
                    report.addValues(Report.LUNCH_KEY, lunchCursor.getString(0), lunchCursor.getInt(1));
                }
            }


            String dinnerReportQuery = String.format("SELECT type, sum(quantity) from Products p JOIN " +
                    "Meals_Products mp ON p.productsId = mp.productId " +
                    "WHERE mp.mealId IN" +
                    "(SELECT mealId from Dinner WHERE dailyMenuId IN" +
                    "(SELECT dailyMenuId FROM DailyMenus WHERE " +
                    "DailyMenus.date > %s))" +
                    "GROUP BY type", dateFormat.format(new Date()));
            Cursor dinnerCursor = menuDataBase.downloadData(dinnerReportQuery);
            if(dinnerCursor.getCount() > 0){
                dinnerCursor.moveToPosition(-1);
                while (dinnerCursor.moveToNext()){
                    report.addValues(Report.DINNER_KEY, dinnerCursor.getString(0), dinnerCursor.getInt(1));
                }
            }

            String teatimeReportQuery = String.format("SELECT type, sum(quantity) from Products p JOIN " +
                    "Meals_Products mp ON p.productsId = mp.productId " +
                    "WHERE mp.mealId IN" +
                    "(SELECT mealId from Teatime WHERE dailyMenuId IN" +
                    "(SELECT dailyMenuId FROM DailyMenus WHERE " +
                    "DailyMenus.date > %s))" +
                    "GROUP BY type", dateFormat.format(new Date()));
            Cursor teatimeCursor = menuDataBase.downloadData(teatimeReportQuery);
            if(teatimeCursor.getCount() > 0){
                teatimeCursor.moveToPosition(-1);
                while (teatimeCursor.moveToNext()){
                    report.addValues(Report.TEATIME_KEY, teatimeCursor.getString(0), teatimeCursor.getInt(1));
                }
            }


            String suppersReportQuery = String.format("SELECT type, sum(quantity) from Products p JOIN " +
                    "Meals_Products mp ON p.productsId = mp.productId " +
                    "WHERE mp.mealId IN" +
                    "(SELECT mealId from Supper WHERE dailyMenuId IN" +
                    "(SELECT dailyMenuId FROM DailyMenus WHERE " +
                    "DailyMenus.date > %s))" +
                    "GROUP BY type", dateFormat.format(new Date()));
            Cursor supperCursor = menuDataBase.downloadData(suppersReportQuery);
            if(supperCursor.getCount() > 0){
                supperCursor.moveToPosition(-1);
                while (supperCursor.moveToNext()){
                    report.addValues(Report.SUPPER_KEY, supperCursor.getString(0), supperCursor.getInt(1));
                }
            }

            menuDataBase.close();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == 0){
                reportsFragment.reportGenerateSuccess();
                reportsFragment.showReport(report);
            }else{
                reportsFragment.reportGenerateFailed();
            }
        }
    }
}
