package com.asndeveloper.expensestracker.ExpensesActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.asndeveloper.expensestracker.Database.ExpenseDB;
import com.asndeveloper.expensestracker.Database.ExpensesEntity;
import com.asndeveloper.expensestracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class ManualExpense extends AppCompatActivity {
TextInputEditText Datepicker,tileed,cateed,amouted;
MaterialButton savebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual_expense);
        // Set ActionBar Title
        getSupportActionBar().setTitle("Back");
        // Enable back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // id find
        Datepicker=findViewById(R.id.eddate);
        tileed=findViewById(R.id.edtitle);
        cateed=findViewById(R.id.edtca);
        amouted=findViewById(R.id.ednum);
        savebtn=findViewById(R.id.btnSave);

        // datepicker open
        Datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });

// make opeation on save button
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMYEx();
            }
        });


    }

    private void SaveMYEx() {
        String title = tileed.getText().toString().trim();
        String cat = cateed.getText().toString().trim();
        String amount = amouted.getText().toString().trim();
        String date = Datepicker.getText().toString().trim();

        if (title.isEmpty() || cat.isEmpty() || amount.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

// Create Entity Object
        ExpensesEntity entity = new ExpensesEntity();
        entity.setTitle(title);
        entity.setCategory(cat);
        entity.setAmount(amount);
        entity.setTime(date);

        // Insert into ROOM
        ExpenseDB.getInstance(this)
                .expensesDAO()
                .insert(entity);

        Toast.makeText(this, "Expense Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openCalendar() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> Datepicker.setText(d + "-" + (m+1) + "-" + y),
                year, month, day
        );
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}