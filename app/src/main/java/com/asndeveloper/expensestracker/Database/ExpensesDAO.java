package com.asndeveloper.expensestracker.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpensesDAO {
    // insert
    @Insert
    void insert(ExpensesEntity expensesEntity);

    // update
    @Update
    void updateE(ExpensesEntity expensesEntity);
    @Delete
    void delete(ExpensesEntity expensesEntity);
    @Query("SELECT * FROM ExpensesEntity")
    List<ExpensesEntity> getAllExpenses();
    //  id se delete
    @Query("DELETE FROM ExpensesEntity WHERE id = :id")
    void deleteExpenseById(int id);

    @Query("SELECT * FROM ExpensesEntity WHERE id = :id")
    ExpensesEntity getExpenseById(int id);
    @Query("SELECT * FROM ExpensesEntity WHERE title = :title")
    ExpensesEntity getExpenseByTitle(String title);
    @Query("SELECT * FROM ExpensesEntity WHERE category = :category")
    List<ExpensesEntity> getExpenseByCategory(String category);
    @Query("SELECT * FROM ExpensesEntity WHERE amount = :amount")
    List<ExpensesEntity> getExpenseByAmount(String amount);
    @Query("SELECT * FROM ExpensesEntity WHERE time = :time")
    List<ExpensesEntity> getExpenseByTime(String time);
    @Query("SELECT * FROM ExpensesEntity ORDER BY time DESC")
    List<ExpensesEntity> getallbytime();
    // GET total amount
    // ▼▼▼ THIS IS THE FIX ▼▼▼
    @Query("SELECT SUM(CAST(amount AS DOUBLE)) FROM ExpensesEntity")
    double getTotalExpense(); // Changed return type to double

    // Category total for chart show
    @Query("SELECT category, SUM(CAST(amount AS DOUBLE)) AS totalAmount FROM ExpensesEntity GROUP BY category")
    List<CategoryTotal> getCategoryTotals();
}
