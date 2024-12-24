package com.example.cashbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class MainMenu extends AppCompatActivity implements ItemClickListner{
    DrawerLayout drawerLayout;
    private ArrayList<Transaction> transactions;
    RecyclerView list;
    private long income=0,expense=0,balance=0;
    NavigationView navigationView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    Toolbar toolbar;
    private DatabaseReference database;
    TextView txt_income_expense,txt_date,txt_balance,txt_income,txt_expense,txt_start_date,txt_end_date;
    AppCompatButton btn_income,btn_expense,btn_save,btn_delete,btn_update,btn_ok,btn_cancel;
    TextInputEditText txt_amt,txt_remark;
    TextInputLayout txt_amt_layout,txt_remark_layout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        drawerLayout=findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView=findViewById(R.id.navigation_view);
        btn_income=findViewById(R.id.btn_income);
        btn_expense=findViewById(R.id.btn_expense);
        txt_income=findViewById(R.id.txt_income_rs);
        btn_delete=findViewById(R.id.btn_delete);
        btn_update=findViewById(R.id.btn_update);
        txt_expense=findViewById(R.id.txt_expense_rs);
        txt_balance=findViewById(R.id.txt_balance_rs);
        list=findViewById(R.id.list);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance().getReference("Transaction");
        transactions=new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        Adapter adapter=new Adapter(this,transactions);
        list.setAdapter(adapter);
        adapter.setClickListner(this);
        Query query=database.orderByChild("user_Id").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total_income=0,total_expense=0;
                    for(DataSnapshot transaction:snapshot.getChildren()){
                        DataSnapshot incomeSnapshot = transaction.child("income");
                        DataSnapshot expenseSnapshot = transaction.child("expense");
                        if(incomeSnapshot.exists()){
                            income=incomeSnapshot.getValue(Long.class);
                            total_income=total_income+income;
                        }
                        if(expenseSnapshot.exists()){
                            expense=expenseSnapshot.getValue(Long.class);
                            total_expense=total_expense+expense;
                        }
                    }
                    balance=total_income-total_expense;
                    txt_income.setText(String.valueOf(total_income));
                    txt_expense.setText(String.valueOf(total_expense));
                    txt_balance.setText(String.valueOf(balance));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
                Displaying all Transaction off the user
         */
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for (DataSnapshot transaction_snapshot : snapshot.getChildren()) {
                    Transaction transaction = transaction_snapshot.getValue(Transaction.class);
                    String nodeid=transaction_snapshot.getKey();
                    transaction.setNode_Id(nodeid);
                    transactions.add(transaction);
                }
                Collections.reverse(transactions);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id= item.getItemId();
                if(id==R.id.nav_cal){
                    startActivity(new Intent(getApplicationContext(),Calender_wise.class));
                    finish();

                } else if (id==R.id.nav_logout) {
                    auth.signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                } else if (id == R.id.nav_pdf) {
                    Dialog dialog = new Dialog(MainMenu.this);
                    dialog.setContentView(R.layout.pdf_dialog);
                    txt_start_date=dialog.findViewById(R.id.txt_start_date);
                    txt_end_date=dialog.findViewById(R.id.txt_end_date);
                    btn_ok=dialog.findViewById(R.id.btn_ok);
                    btn_cancel=dialog.findViewById(R.id.btn_cancel);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String currentDate = sdf.format(new Date());
                    txt_start_date.setText(currentDate);
                    txt_end_date.setText(currentDate);
                    txt_start_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(txt_start_date);
                        }
                    });
                    txt_end_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(txt_end_date);
                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           database=FirebaseDatabase.getInstance().getReference("Transaction");
                          Query date_query=database.orderByChild("user_Id").equalTo(user.getUid());
                          date_query.addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot snapshot) {
                                  transactions=new ArrayList<>();
                                  for(DataSnapshot datasnapshot:snapshot.getChildren()){
                                      String date=datasnapshot.child("date").getValue(String.class);
                                      if(date.compareTo(txt_start_date.getText().toString())>=0  && date.compareTo(txt_end_date.getText().toString())<=0 ){
                                          Transaction transaction=datasnapshot.getValue(Transaction.class);
                                          Log.d("check","h"+transaction.getRemark());
                                          transactions.add(transaction);
                                      }else{

                                      }
                                  }
                                  String date="(From: "+txt_start_date.getText().toString()+" To "+txt_end_date.getText().toString()+")";
                                  Generate_Report.createPdf(transactions,date);
                                  Toast.makeText(MainMenu.this, "Pdf Downloaded Check Download Folder", Toast.LENGTH_SHORT).show();
                                  dialog.dismiss();
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError error) {

                              }
                          });
                        }
                    });

                    dialog.show();
                } else if (id == R.id.nav_chart) {
                    Dialog dialog = new Dialog(MainMenu.this);
                    dialog.setContentView(R.layout.pdf_dialog);
                    TextView txt_title=dialog.findViewById(R.id.txt_title);
                    txt_start_date=dialog.findViewById(R.id.txt_start_date);
                    txt_end_date=dialog.findViewById(R.id.txt_end_date);
                    btn_ok=dialog.findViewById(R.id.btn_ok);
                    btn_cancel=dialog.findViewById(R.id.btn_cancel);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String currentDate = sdf.format(new Date());
                    txt_start_date.setText(currentDate);
                    txt_end_date.setText(currentDate);
                    txt_title.setText("Summary");
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    txt_start_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(txt_start_date);
                        }
                    });
                    txt_end_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(txt_end_date);
                        }
                    });
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(getApplicationContext(),Summary.class);
                            i.putExtra("startDate",txt_start_date.getText().toString());
                            i.putExtra("endDate",txt_end_date.getText().toString());
                            startActivity(i);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                } else if (id == R.id.nav_follow) {
                    Uri uri=Uri.parse("");
                    Intent i=new Intent(Intent.ACTION_VIEW,uri);
                    i.setPackage("com.instagram.android");
                    if(i.resolveActivity(getPackageManager())!=null){
                        startActivity(i);
                    }else{
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        //intent.setData(Uri.parse("https://www.instagram.com/parthh.28/");
                        startActivity(intent);
                    }

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        btn_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainMenu.this);
                dialog.setContentView(R.layout.income_expense_dialog);
                txt_income_expense = dialog.findViewById(R.id.txt_income_expense);
                txt_amt = dialog.findViewById(R.id.txt_amount);
                txt_remark = dialog.findViewById(R.id.txt_remark);
                txt_date=dialog.findViewById(R.id.txt_date);
                btn_save=dialog.findViewById(R.id.btn_save);
                txt_amt_layout=dialog.findViewById(R.id.textInputLayout);
                txt_remark_layout=dialog.findViewById(R.id.textInputLayout2);
                txt_income_expense.setText("Income");
                txt_income_expense.setTextColor(getColor(R.color.green));
                currentDate(txt_date);
                txt_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       showDatePickerDialog(txt_date);
                    }
                });
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Transaction transaction=new Transaction();
                        balance= Long.parseLong(txt_balance.getText().toString());
                        income= Long.parseLong(txt_amt.getText().toString());
                        balance=balance+income;
                        transaction.setUser_Id(user.getUid());
                        transaction.setDate(txt_date.getText().toString());
                        transaction.setRemark(txt_remark.getText().toString());
                        transaction.setIncome(income);
                        transaction.setExpense(0);
                        transaction.setBalance(balance);
                        transaction.setTransaction_Type("income");
                        database.push().setValue(transaction);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        });
        btn_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainMenu.this);
                dialog.setContentView(R.layout.income_expense_dialog);
                txt_income_expense = dialog.findViewById(R.id.txt_income_expense);
                txt_amt = dialog.findViewById(R.id.txt_amount);
                txt_remark = dialog.findViewById(R.id.txt_remark);
                txt_date=dialog.findViewById(R.id.txt_date);
                btn_save=dialog.findViewById(R.id.btn_save);
                txt_amt_layout=dialog.findViewById(R.id.textInputLayout);
                txt_remark_layout=dialog.findViewById(R.id.textInputLayout2);
                txt_income_expense.setText("Expense");
                txt_income_expense.setTextColor(getColor(R.color.red));
                currentDate(txt_date);
                txt_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(txt_date);
                    }
                });
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Transaction transaction=new Transaction();
                        balance= Long.parseLong(txt_balance.getText().toString());
                        expense= Long.parseLong(txt_amt.getText().toString());
                        balance=balance-expense;
                        transaction.setUser_Id(user.getUid());
                        transaction.setDate(txt_date.getText().toString());
                        transaction.setRemark(txt_remark.getText().toString());
                        transaction.setIncome(0);
                        transaction.setExpense(expense);
                        transaction.setBalance(balance);
                        transaction.setTransaction_Type("expense");
                        database.push().setValue(transaction);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        });


    }



    public void currentDate(TextView txt_date){
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
       String currentDate = sdf.format(new Date());
       txt_date.setText(currentDate);
   }
    private void showDatePickerDialog(TextView txt_date) {
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String dateFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String selectedDate = sdf.format(myCalendar.getTime());
                txt_date.setText(selectedDate);
            }
        };

        // Create and show the DatePickerDialog here
        // You can customize the DatePickerDialog creation as needed
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainMenu.this,
                dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view, int pos) {
        Transaction transaction=transactions.get(pos);
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.update_dialog);
        txt_amt=dialog.findViewById(R.id.amt);
        txt_remark=dialog.findViewById(R.id.remark);
        txt_date=dialog.findViewById(R.id.txt_date);
        btn_delete=dialog.findViewById(R.id.btn_delete);
        btn_update=dialog.findViewById(R.id.btn_update);
        txt_remark.setText(transaction.getRemark());
        txt_date.setText(transaction.getDate());
        if(transaction.getIncome()>=1){
            txt_amt.setText(String.valueOf(transaction.getIncome()));
        } else if (transaction.getExpense()>=1) {
            txt_amt.setText(String.valueOf(transaction.getExpense()));
        }
        dialog.show();
       btn_delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              Delete_Transaction(transaction);
                dialog.dismiss();
           }
       });

      btn_update.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Update_Transaction(transaction);
              dialog.dismiss();
          }
      });




    }
    public void Update_Transaction(Transaction transaction){
                // Get a reference to the specific transaction node
                DatabaseReference transactionToUpdateRef = FirebaseDatabase.getInstance().getReference()
                        .child("Transaction")
                        .child(transaction.getNode_Id()); // Replace with the ID of the transaction to update

                // Step 1: Update the specific transaction
                transactionToUpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Transaction updatedTransaction = dataSnapshot.getValue(Transaction.class);

                        // Modify the fields of the updatedTransaction as needed
                        updatedTransaction.setDate(txt_date.getText().toString());
                        updatedTransaction.setRemark(txt_remark.getText().toString());
                        if (transaction.getTransaction_Type().equals("income")) {
                            updatedTransaction.setIncome(Long.parseLong(txt_amt.getText().toString()));
                        } else if (transaction.getTransaction_Type().equals("expense")) {
                            updatedTransaction.setExpense(Long.parseLong(txt_amt.getText().toString()));
                        }

                        // Update the specific transaction with the changes
                        transactionToUpdateRef.setValue(updatedTransaction);

                        // Step 2: Retrieve all transactions for the current user and update balances
                        String userId = dataSnapshot.child("user_Id").getValue(String.class);
                        DatabaseReference userTransactionsRef = FirebaseDatabase.getInstance().getReference("Transaction");
                        Query userQuery = userTransactionsRef.orderByChild("user_Id").equalTo(userId);

                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot transactionsSnapshot) {
                                long updatedBalance = 0;

                                // Calculate the updated balance and update each transaction
                                for (DataSnapshot transactionSnapshot : transactionsSnapshot.getChildren()) {
                                    long income = transactionSnapshot.child("income").getValue(Long.class);
                                    long expense = transactionSnapshot.child("expense").getValue(Long.class);
                                    long balance = income - expense;
                                    updatedBalance += balance;

                                    // Update each transaction's balance
//                                    DatabaseReference updatedTransactionRef = transactionSnapshot.getRef();
//                                    updatedTransactionRef.child("balance").setValue(balance);
                                    transactionSnapshot.child("balance").getRef().setValue(updatedBalance);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle errors
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });

    }
    public void Delete_Transaction(Transaction transaction){
        // Assuming you have a DatabaseReference pointing to the transaction to delete
        DatabaseReference transactionToDeleteRef = FirebaseDatabase.getInstance().getReference()
                .child("Transaction")
                .child(transaction.getNode_Id());

        transactionToDeleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Step 1: Delete the transaction
                transactionToDeleteRef.removeValue();

                // Step 2: Retrieve all transactions for the user
                String userId = dataSnapshot.child("user_Id").getValue(String.class);
                DatabaseReference userTransactionsRef =  FirebaseDatabase.getInstance().getReference("Transaction");
                Query user_query=userTransactionsRef.orderByChild("user_Id").equalTo(userId);

                user_query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot transactionsSnapshot) {
                        long updatedBalance = 0;

                        // Step 3: Calculate the updated balance and update each transaction
                        for (DataSnapshot transactionSnapshot : transactionsSnapshot.getChildren()) {
                            long income = transactionSnapshot.child("income").getValue(Long.class);
                            long expense = transactionSnapshot.child("expense").getValue(Long.class);
                            long balance = income - expense;
                            updatedBalance += balance;

                            // Step 4: Update each transaction's balance
//                            DatabaseReference updatedTransactionRef = transactionSnapshot.getRef();
//                            updatedTransactionRef.child("balance").setValue(balance);
                            transactionSnapshot.child("balance").getRef().setValue(updatedBalance);
                        }

                         //You can optionally update the user's total balance if needed
//                        DatabaseReference userBalanceRef = FirebaseDatabase.getInstance().getReference()
//                                .child("Transaction")
//                                .child(transaction.getNode_Id())
//                                .child("balance");
//                        userBalanceRef.setValue(updatedBalance);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

}