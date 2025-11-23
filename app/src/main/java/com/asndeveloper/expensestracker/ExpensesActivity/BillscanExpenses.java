package com.asndeveloper.expensestracker.ExpensesActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.asndeveloper.expensestracker.Database.ExpenseDB;
import com.asndeveloper.expensestracker.Database.ExpensesEntity;
import com.asndeveloper.expensestracker.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillscanExpenses extends AppCompatActivity {
TextView txtview;
Button scanbtn,Billsavebtn;
    private static final int PICK_IMAGE = 1;


    private String extractAmount(String text) {
// 1) Pattern for ₹ or Rs with comma-formatted amounts
        Pattern p1 = Pattern.compile("(₹|Rs\\.?\\s?)(\\d{1,3}(,\\d{3})*(\\.\\d+)?|\\d+(\\.\\d+)?)");
        Matcher m1 = p1.matcher(text);
        if (m1.find()) {
            return m1.group(2).replace(",", ""); // remove commas
        }

        // 2) Pattern for any standalone amount with commas
        Pattern p2 = Pattern.compile("\\d{1,3}(,\\d{3})+(\\.\\d+)?");
        Matcher m2 = p2.matcher(text);
        if (m2.find()) {
            return m2.group(0).replace(",", "");
        }

        // 3) Pattern for simple numbers (last fallback)
        Pattern p3 = Pattern.compile("\\d+\\.\\d+|\\d+");
        Matcher m3 = p3.matcher(text);
        if (m3.find()) {
            return m3.group(0);
        }

        return "0";

    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_billscan_expenses);
        txtview=findViewById(R.id.txtr);
        scanbtn=findViewById(R.id.scn);
Billsavebtn=findViewById(R.id.btntsave);
        // Set ActionBar Title
        getSupportActionBar().setTitle("Back ");
        // Enable back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
scanbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        openGallery();
    }
});
Billsavebtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        SaveScanBill();
    }
});

    }
//
    private void SaveScanBill() {
// extract amount only save in expenses

        String ScanTxt=extractAmount(txtview.getText().toString());
        if (ScanTxt.isEmpty()) {
            Toast.makeText(this, "Please Scan Bill", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create Entity Object
        ExpensesEntity entity = new ExpensesEntity();
        entity.setAmount(String.valueOf(Double.parseDouble(ScanTxt)));
        entity.setTitle(ScanTxt);
        // Insert into ROOM
        ExpenseDB.getInstance(this)
                .expensesDAO()
                .insert(entity);

        Toast.makeText(this, "Bill Saved ", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Step 1: Open gallery to choose bill picture
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Step 2: Receive selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                runTextRecognition(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Step 3: ML Kit OCR
    private void runTextRecognition(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        com.google.mlkit.vision.text.TextRecognizer recognizer =
                TextRecognition.getClient(new TextRecognizerOptions.Builder().build());

        recognizer.process(image)
                .addOnSuccessListener(this::displayText)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Step 4: Display scanned bill text
    private void displayText(Text visionText) {
        StringBuilder result = new StringBuilder();

        for (Text.TextBlock block : visionText.getTextBlocks()) {
            result.append(block.getText()).append("\n");
        }

        txtview.setText(result.toString());
    }




    //for button back
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}