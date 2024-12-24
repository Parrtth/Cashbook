package com.example.cashbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Calender_wise extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference database;
    ArrayList<Transaction> transactions;
    RecyclerView list;
    Adapter adapter;
    SimpleDateFormat sdf;
    String currentDate;
    CalendarView calendarView;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_wise);
        calendarView=findViewById(R.id.calender_view);
        img_back=findViewById(R.id.img_back);
        list=findViewById(R.id.list);
        user= FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance().getReference("Transaction");
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), MainMenu.class);
                startActivity(i);
                finish();
            }
        });
        transactions=new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        Adapter adapter=new Adapter(this,transactions);
        list.setAdapter(adapter);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currentDate = sdf.format(new Date());
        Date_Wise_Transaction(currentDate,adapter);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate =Date_Format(year,month,dayOfMonth);
                Date_Wise_Transaction(selectedDate,adapter);
            }
        });
    }
    private String Date_Format(int y,int m,int d){
        final Calendar myCalendar = Calendar.getInstance();
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        myCalendar.set(Calendar.YEAR, y);
        myCalendar.set(Calendar.MONTH, m);
        myCalendar.set(Calendar.DAY_OF_MONTH, d);
        String selectedDate = sdf.format(myCalendar.getTime());
        return selectedDate;
    }
    private void Date_Wise_Transaction(String selected_Date,Adapter adapter){
        Query query=database.orderByChild("user_Id").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for(DataSnapshot transaction_shot:snapshot.getChildren()){
                    String tr_date=transaction_shot.child("date").getValue(String.class);
                    if(tr_date!=null && tr_date.equals(selected_Date)){
                        Transaction transaction=transaction_shot.getValue(Transaction.class);
                        transactions.add(transaction);

                    }
                }
                Collections.reverse(transactions);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}