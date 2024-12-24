package com.example.cashbook;
import android.graphics.Color;
import android.os.Environment;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;

import java.util.ArrayList;


public class Generate_Report {
    public static void createPdf(ArrayList<Transaction> transactions,String Date){
        Document document=new Document();
        try {
            long timestamp = System.currentTimeMillis();

            //  filename based on the timestamp
            String fileName = "transaction_report_" + timestamp + ".pdf";

            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"+fileName;


           //  PdfWriter instance to write the document to the specified file path
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            // Open the document for writing
            document.open();

            //  Add content to the document
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Paragraph title = new Paragraph("Transaction Report "+Date+"\n \n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            PdfPTable table = new PdfPTable(6); // Assuming 4 columns for Transaction ID, Date, Remark, and Amount

            // Set column widths (adjust as needed)
            float[] columnWidths = {1f, 1.2f, 2f, 1f,1f,1f};
            table.setWidths(columnWidths);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Sr.No", tableHeaderFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Date", tableHeaderFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Remark", tableHeaderFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Income", tableHeaderFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Expense", tableHeaderFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Balance", tableHeaderFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            int i=1;
            for(Transaction transaction:transactions){

                table.addCell(String.valueOf(i));
                table.addCell(transaction.getDate());
                table.addCell(transaction.getRemark());
                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(transaction.getIncome()), new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN)));
                PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(transaction.getExpense()), new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED)));
                PdfPCell cell3 = new PdfPCell(new Phrase(String.valueOf(transaction.getBalance()), new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLUE)));

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                i++;
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void Create_Chart(long total_income, long total_expense, PieChart pieChart){

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(total_income, "Income"));
        entries.add(new PieEntry(total_expense, "Expense"));
        PieDataSet dataSet = new PieDataSet(entries, "Pie Chart");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN); // Green color for Income
        colors.add(Color.RED);
        dataSet.setColors(colors); // Set colors for the slices
        dataSet.setValueTextSize(12f);

        // Create a PieData object from the PieDataSet
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart)); // Format values as percentages

// Customize the appearance of the pie chart
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false); // Disable description label
        pieChart.setDrawHoleEnabled(true); // Draw a hole in the center (donut chart)
        pieChart.setHoleRadius(30f); // Set the radius of the hole
        pieChart.setTransparentCircleRadius(40f); // Set the radius of the transparent circle around the hole
        pieChart.animateY(1000); // Add animation

// Refresh the chart
        pieChart.invalidate();
    }
}
