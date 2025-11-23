package com.asndeveloper.expensestracker.Fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.asndeveloper.expensestracker.Database.ExpenseDB;
import com.asndeveloper.expensestracker.Database.ExpensesEntity;
import com.asndeveloper.expensestracker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ProfileFragment extends Fragment {
ImageView Pdfimage;
ExpenseDB expenseDB;

    public ProfileFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Pdfimage=view.findViewById(R.id.pdimage);
        expenseDB=ExpenseDB.getInstance(getContext());
// pdf covert on click
Pdfimage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ExportIntoPdf();
    }
});

    }

    private void ExportIntoPdf() {
        new Thread(() -> {

            List<ExpensesEntity> list = expenseDB.expensesDAO().getAllExpenses();

            if (list.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "No expenses found", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            try {
                // Folder: Documents/ExpenseReports
                File dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), "ExpenseReports");

                if (!dir.exists()) dir.mkdirs();

                // Filename
                String fileName = "Expense_Report_" + System.currentTimeMillis() + ".pdf";
                File pdfFile = new File(dir, fileName);

                PdfDocument document = new PdfDocument();
                Paint paint = new Paint();

                int pageWidth = 600;
                int pageHeight = 800;

                int y = 60;

                PdfDocument.PageInfo pageInfo =
                        new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                paint.setTextSize(20);
                canvas.drawText("All Expense Report", 180, y, paint);

                y += 40;
                paint.setTextSize(14);

                for (ExpensesEntity e : list) {

                    String line = e.getTitle() + " | ₹" + e.getAmount() +
                            " | " + e.getCategory() + " | " + e.getTime();

                    canvas.drawText(line, 20, y, paint);
                    y += 22;

                    // New page if full
                    if (y > 760) {
                        document.finishPage(page);

                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();
                        y = 60;
                    }
                }

                document.finishPage(page);

                FileOutputStream fos = new FileOutputStream(pdfFile);
                document.writeTo(fos);
                fos.close();
                document.close();

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "PDF Saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show()
                );

                sharePDF(pdfFile);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void sharePDF(File file) {

        Uri uri = FileProvider.getUriForFile(
                getContext(),
                getActivity().getPackageName() + ".provider",
                file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share PDF"));
    }


}