package com.asndeveloper.expensestracker.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ExpensesEntity.class},version = 14)
public abstract class ExpenseDB extends RoomDatabase {
    // 1. Declare a static variable to hold the instance.
    private static volatile ExpenseDB instance;
    public abstract ExpensesDAO expensesDAO();
// The getInstance method takes a context to create the DB if needed.
    public static synchronized ExpenseDB getInstance(Context context) {
        // 3. Check if the instance is null.
        if (instance == null) {
            // 4. If it's null, build the database using the provided (non-null) context.
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ExpenseDB.class, "expenses_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()    // For simple MCA project
                    .build();
        }
        // 5. Return the single instance.
        return instance;
    }
}
