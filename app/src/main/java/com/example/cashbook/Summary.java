package com.example.cashbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class Summary extends AppCompatActivity {
    private String start_date,end_date;
    PieChart pieChart;
    private String user_id;
    private long total_income=0,total_expense=0,income=0,expense=0;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Intent i=getIntent();
        start_date=i.getStringExtra("startDate");
        end_date=i.getStringExtra("endDate");
        user_id=FirebaseAuth.getInstance().getUid();
        pieChart= findViewById(R.id.pieChart);
        databaseReference= FirebaseDatabase.getInstance().getReference("Transaction");
        Query query=databaseReference.orderByChild("user_Id").equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot:snapshot.getChildren()){

                    String date=shot.child("date").getValue(String.class);


                    if(date.compareTo(start_date)>=0 && date.compareTo(end_date)<=0){
                        Transaction transaction=shot.getValue(Transaction.class);
                         income=transaction.getIncome();
                        expense=transaction.getExpense();
                        total_income+=income;
                        total_expense+=expense;


                    }

                }
                Generate_Report.Create_Chart(total_income,total_expense,pieChart);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

// Initialize the PieChart


// Create a list of PieEntries (slices of the pie chart)
//        ArrayList<PieEntry> entries = new ArrayList<>();
//        entries.add(new PieEntry(30f, "Category 1"));
//        entries.add(new PieEntry(20f, "Category 2"));
//        entries.add(new PieEntry(50f, "Category 3"));

// Create a PieDataSet with the entries
//        PieDataSet dataSet = new PieDataSet(entries, "Pie Chart");
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS); // Set colors for the slices
//
//// Create a PieData object from the PieDataSet
//        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter(pieChart)); // Format values as percentages
//
//// Customize the appearance of the pie chart
//        pieChart.setData(data);
//        pieChart.getDescription().setEnabled(false); // Disable description label
//        pieChart.setDrawHoleEnabled(true); // Draw a hole in the center (donut chart)
//        pieChart.setHoleRadius(30f); // Set the radius of the hole
//        pieChart.setTransparentCircleRadius(40f); // Set the radius of the transparent circle around the hole
//        pieChart.animateY(1000); // Add animation
//
//// Refresh the chart
//        pieChart.invalidate();

    }
}