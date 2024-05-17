package com.example.fyp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.List;

public class EndUserLogFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    View view;
    TextView dateTextView;
    ImageButton previousDateButton;
    ImageButton nextDateButton;
    ImageButton addBreakfastImageButton;
    ImageButton addLunchImageButton;
    ImageButton addDinnerImageButton;
    ListView breakfastListView;
    ListView lunchListView;
    ListView dinnerListView;
    AddMealRecordFragment addMealRecordFragment;
    ViewMealRecordFragment viewMealRecordFragment;
    Calendar calendar;
    String date;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_end_user_log, container, false);

        dateTextView = (TextView)view.findViewById(R.id.dateTextView);
        previousDateButton = (ImageButton)view.findViewById(R.id.previousDateButton);
        nextDateButton = (ImageButton)view.findViewById(R.id.nextDateButton);
        addBreakfastImageButton = (ImageButton)view.findViewById(R.id.addBreakfastImageButton);
        addLunchImageButton = (ImageButton)view.findViewById(R.id.addLunchImageButton);
        addDinnerImageButton = (ImageButton)view.findViewById(R.id.addDinnerImageButton);
        breakfastListView = (ListView)view.findViewById(R.id.breakfastListView);
        lunchListView = (ListView)view.findViewById(R.id.lunchListView);
        dinnerListView = (ListView)view.findViewById(R.id.dinnerListView);
        addMealRecordFragment = new AddMealRecordFragment();
        viewMealRecordFragment = new ViewMealRecordFragment();

        dateTextView.setOnClickListener(this);
        previousDateButton.setOnClickListener(this);
        nextDateButton.setOnClickListener(this);
        addBreakfastImageButton.setOnClickListener(this);
        addLunchImageButton.setOnClickListener(this);
        addDinnerImageButton.setOnClickListener(this);

        breakfastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedFromList = (String)(breakfastListView.getItemAtPosition(position));
                String[] splitted = selectedFromList.split("\n");
                Toast.makeText(getActivity(), "Meal: " + splitted[0], Toast.LENGTH_SHORT).show();
                // Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
                Bundle args = new Bundle();
                args.putString("Date", date);
                args.putString("Meal Type", "Breakfast");
                args.putString("Meal Name", splitted[0]);
                viewMealRecordFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.endUserFragmentContainerView, viewMealRecordFragment)
                        .commit();
            }
        });

        lunchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedFromList = (String)(lunchListView.getItemAtPosition(position));
                String[] splitted = selectedFromList.split("\n");
                Toast.makeText(getActivity(), "Meal: " + splitted[0], Toast.LENGTH_SHORT).show();
                // Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
                Bundle args = new Bundle();
                args.putString("Date", date);
                args.putString("Meal Type", "Lunch");
                args.putString("Meal Name", splitted[0]);
                viewMealRecordFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.endUserFragmentContainerView, viewMealRecordFragment)
                        .commit();
            }
        });

        dinnerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedFromList = (String)(dinnerListView.getItemAtPosition(position));
                String[] splitted = selectedFromList.split("\n");
                Toast.makeText(getActivity(), "Meal: " + splitted[0], Toast.LENGTH_SHORT).show();
                // Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
                Bundle args = new Bundle();
                args.putString("Date", date);
                args.putString("Meal Type", "Dinner");
                args.putString("Meal Name", splitted[0]);
                viewMealRecordFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.endUserFragmentContainerView, viewMealRecordFragment)
                        .commit();
            }
        });


        calendar = Calendar.getInstance();
        date = DateFormat.getDateInstance(DateFormat.FULL)
                .format(calendar.getTime());
        dateTextView.setText(date);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        populateListViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListViews();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.previousDateButton) {
            calendar.add(Calendar.DATE, -1);
            date = DateFormat.getDateInstance(DateFormat.FULL)
                    .format(calendar.getTime());
            dateTextView.setText(date);
            populateListViews();
        } else if (id == R.id.nextDateButton) {
            calendar.add(Calendar.DATE, 1);
            date = DateFormat.getDateInstance(DateFormat.FULL)
                    .format(calendar.getTime());
            dateTextView.setText(date);
            populateListViews();
        } else if (id == R.id.addBreakfastImageButton) {
            Bundle args = new Bundle();
            args.putString("Date", date);
            args.putString("Type", "Breakfast");
            addMealRecordFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, addMealRecordFragment)
                    .commit();
        } else if (id == R.id.addLunchImageButton) {
            Bundle args = new Bundle();
            args.putString("Date", date);
            args.putString("Type", "Lunch");
            addMealRecordFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, addMealRecordFragment)
                    .commit();
        } else if (id == R.id.addDinnerImageButton) {
            Bundle args = new Bundle();
            args.putString("Date", date);
            args.putString("Type", "Dinner");
            addMealRecordFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, addMealRecordFragment)
                    .commit();
        } else if (id == R.id.dateTextView) {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.onDateSetListener(this);
            newFragment.show(getActivity().getSupportFragmentManager(), "date picker");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL)
                .format(calendar.getTime());
        dateTextView.setText(currentDateString);
    }

    private void populateListViews() {

        MealRecord mealRecord = new MealRecord();
        mealRecord.getMealRecordsByMealType(email, date, "Breakfast",
                new MealRecord.MealRecordCallbackWithType<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> mealRecords) {
                ArrayList<String> breakfast = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : mealRecords) {
                    String entry = documentSnapshot.getString("mealName") + "\n"
                            + documentSnapshot.getLong("calories").toString() + " calories";
                    breakfast.add(entry);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, breakfast);
                breakfastListView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                breakfastListView.setAdapter(null);
            }
        });

        mealRecord.getMealRecordsByMealType(email, date, "Lunch",
                new MealRecord.MealRecordCallbackWithType<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> mealRecords) {
                        ArrayList<String> lunch = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : mealRecords) {
                            String entry = documentSnapshot.getString("mealName") + "\n"
                                    + documentSnapshot.getLong("calories").toString() + " calories";
                            lunch.add(entry);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, lunch);
                        lunchListView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        lunchListView.setAdapter(null);
                    }
                });

        mealRecord.getMealRecordsByMealType(email, date, "Dinner",
                new MealRecord.MealRecordCallbackWithType<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> mealRecords) {
                        ArrayList<String> dinner = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : mealRecords) {
                            String entry = documentSnapshot.getString("mealName") + "\n"
                                    + documentSnapshot.getLong("calories").toString() + " calories";
                            dinner.add(entry);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, dinner);
                        dinnerListView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        dinnerListView.setAdapter(null);
                    }
                });
    }
}