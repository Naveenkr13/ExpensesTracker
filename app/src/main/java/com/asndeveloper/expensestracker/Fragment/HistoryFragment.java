package com.asndeveloper.expensestracker.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asndeveloper.expensestracker.Database.ExpenseDB;
import com.asndeveloper.expensestracker.Database.ExpensesEntity;
import com.asndeveloper.expensestracker.ExpensesAdpator.expenseadptor;
import com.asndeveloper.expensestracker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {

    RecyclerView RecylerHistory;
    EditText SearChBar;
    Spinner SpinnerS, SpinnerFilter;

    ExpenseDB expenseDB;

    List<ExpensesEntity> fullList = new ArrayList<>();
    expenseadptor adapter;

    public HistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecylerHistory = view.findViewById(R.id.historyrecyler);
        SearChBar = view.findViewById(R.id.search);
        SpinnerS = view.findViewById(R.id.sortSpinner);
        SpinnerFilter = view.findViewById(R.id.filterSpinner);

        expenseDB = ExpenseDB.getInstance(getContext());

        loadAllTransactions();
        setSortFilterListeners();
        enableDeleteSwipe();
    }


    // -------------------- LOAD DATA ---------------------
    private void loadAllTransactions() {

        new Thread(() -> {

            fullList = expenseDB.expensesDAO().getAllExpenses();

            requireActivity().runOnUiThread(() -> {
                RecylerHistory.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new expenseadptor(getContext(), fullList);
                RecylerHistory.setAdapter(adapter);
            });

        }).start();
    }


    // -------------------- SORT + FILTER + SEARCH ---------------------
    private void setSortFilterListeners() {

        // SORT
        SpinnerS.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int pos, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {}
        });

        // FILTER CATEGORY
        SpinnerFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int pos, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {}
        });

        // SEARCH Bar
        SearChBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
        });
    }


    // -------------------- APPLY SORT + FILTER + SEARCH ---------------------
    private void applyFilters() {

        String searchText = SearChBar.getText().toString().toLowerCase();
        String selectedCategory = SpinnerFilter.getSelectedItem().toString();
        String selectedSort = SpinnerS.getSelectedItem().toString();

        List<ExpensesEntity> filtered = new ArrayList<>();

        for (ExpensesEntity e : fullList) {

            boolean matchesCategory =
                    selectedCategory.equals("All") ||
                            e.getCategory().equalsIgnoreCase(selectedCategory);

            boolean matchesSearch =
                    e.getTitle().toLowerCase().contains(searchText) ||
                            e.getAmount().contains(searchText);

            if (matchesCategory && matchesSearch) {
                filtered.add(e);
            }
        }

        // SORT
        if (selectedSort.equals("Latest First")) {
            Collections.reverse(filtered);
        }

        adapter.updateList(filtered);
    }


    // -------------------- SWIPE TO DELETE ---------------------
    private void enableDeleteSwipe() {

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int direction) {

                int position = vh.getAdapterPosition();

                ExpensesEntity itemToDelete = adapter.getItem(position);

                new Thread(() -> {
                    expenseDB.expensesDAO().delete(itemToDelete);

                    fullList.remove(itemToDelete);

                    requireActivity().runOnUiThread(() -> {
                        adapter.removeItem(position);
                    });

                }).start();
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(RecylerHistory);
    }
}
