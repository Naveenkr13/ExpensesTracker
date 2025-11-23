package com.asndeveloper.expensestracker.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ExpensesEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name="title")
    public String Title;
    @ColumnInfo(name="amount")
    public String Amount;

    @ColumnInfo(name="category")
    public String Category;
    @ColumnInfo(name="time")
    public String Time;
    // ✅ EMPTY CONSTRUCTOR for manual object creation
    @Ignore
    public ExpensesEntity() {}
    public ExpensesEntity(int id, String Title, String Amount, String Category, String Time) {

        this.id = id;
        this.Title = Title;
        this.Amount = Amount;
        this.Category = Category;
        this.Time = Time;


    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
