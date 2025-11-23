package com.asndeveloper.expensestracker.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asndeveloper.expensestracker.Database.ExpenseDB;
import com.asndeveloper.expensestracker.Database.ExpensesEntity;
import com.asndeveloper.expensestracker.ExpensesActivity.BillscanExpenses;
import com.asndeveloper.expensestracker.ExpensesActivity.ManualExpense;
import com.asndeveloper.expensestracker.ExpensesAdpator.expenseadptor;
import com.asndeveloper.expensestracker.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
ExpenseDB expenseDB;
FloatingActionButton btnadd;
TextView showexpensetxt;
RecyclerView recyclerView;
PieChart piechart;

    public HomeFragment() {}





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.recy);
        btnadd=view.findViewById(R.id.ft);
        piechart=view.findViewById(R.id.pie);
        showexpensetxt=view.findViewById(R.id.txte);
        // database get
        expenseDB=ExpenseDB.getInstance(getContext());
       // create method
TotalExpenseResult();
TotalPieChart();
RecentTranscation();

btnadd.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ShowOptionForAdd();
    }
});
    }
    // alert dialog for expensed add
    private void ShowOptionForAdd() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Please Select Option");
        String[] options={"Add Manually ","Scan Bill "};
        builder.setItems(options, (dialog, which) -> {
            if (which==0){
                // Here Fragment to Move Activity for add manually
                Intent intent=new Intent(getContext(), ManualExpense.class);
                startActivity(intent);
            }else if (which==1){
                // Here Fragment to Move Activity for Scan Bill
                Intent intent=new Intent(getContext(), BillscanExpenses.class);
                startActivity(intent);
            }
        });
        // this is important other wise not show alert dialog
        builder.show();
    }
// show recent transciton in recyler view from Adptor class expnses adaptor
    private void RecentTranscation() {
        new Thread(() -> {
            List<ExpensesEntity> list = expenseDB.expensesDAO().getAllExpenses();

            // reverse order → latest first
            List<ExpensesEntity> recent = new ArrayList<>();

            for (int i = list.size() - 1; i >= 0 && recent.size() < 5; i--) {
                recent.add(list.get(i));
            }

            requireActivity().runOnUiThread(() -> {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new expenseadptor(getContext(), recent));
            });

        }).start();
    }

// show total pie chart method
    private void TotalPieChart() {



            new Thread(() -> {

                List<ExpensesEntity> list = expenseDB.expensesDAO().getAllExpenses();

                // Map for Category → Total Amount
                HashMap<String, Double> map = new HashMap<>();

                for (ExpensesEntity e : list) {

                    String cat = e.getCategory();
                    if (cat == null || cat.trim().isEmpty()) continue;

                    double amt = 0;

                    try {
                        amt = Double.parseDouble(e.getAmount().trim());
                    } catch (Exception ignored) {
                    }

                    // Add amount to category
                    map.put(cat, map.getOrDefault(cat, 0.0) + amt);
                }

                // Prepare pie entries
                ArrayList<PieEntry> pieList = new ArrayList<>();
                for (String key : map.keySet()) {
                    pieList.add(new PieEntry(map.get(key).floatValue(), key));
                }

                requireActivity().runOnUiThread(() -> {

                    if (pieList.size() == 0) {
                        piechart.clear();
                        return;
                    }

                    PieDataSet pieDataSet = new PieDataSet(pieList, "");
                    pieDataSet.setSliceSpace(3f);
                    pieDataSet.setValueTextSize(12f);

                    // Colors
                    pieDataSet.setColors(new int[]{
                            R.color.pie1,
                            R.color.pie2,
                            R.color.pie3,
                            R.color.pie4,
                            R.color.pie5
                    }, getContext());

                    PieData pieData = new PieData(pieDataSet);

                    piechart.setData(pieData);
                    piechart.setUsePercentValues(true);
                    piechart.setDrawHoleEnabled(true);
                    piechart.setHoleRadius(40f);
                    piechart.setTransparentCircleRadius(45f);
                    piechart.getDescription().setEnabled(false);
                    piechart.animateY(1000);
                    piechart.invalidate(); // refresh
                });

            }).start();
        }



//  show total expense method
    private void TotalExpenseResult() {
        new Thread(() -> {

            double list = expenseDB.expensesDAO().getTotalExpense();


            double total = 0;
            for (ExpensesEntity entity : expenseDB.expensesDAO().getAllExpenses()) {
                try {
                    total += Double.parseDouble(entity.getAmount());
                } catch (Exception ignored) {}
            }


            double finalTotal = total;
            requireActivity().runOnUiThread(() -> {
                showexpensetxt.setText("₹ " + finalTotal);
            });

        }).start();
    }


    
    
    
}