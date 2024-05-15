package com.example.fyp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class EndUserExerciseFragment extends Fragment {

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_exercise, container, false);
        exerciseRecyclerView = view.findViewById(R.id.exerciseRecyclerView);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList, this::navigateToExerciseDetails);
        exerciseRecyclerView.setAdapter(exerciseAdapter);

        fetchExercises();

        return view;
    }

    private void fetchExercises() {
        Exercise.fetchRandomExercises(new Exercise.FirestoreCallback() {
            @Override
            public void onSuccess(List<Exercise> exercises) {
                exerciseList.clear();
                exerciseList.addAll(exercises);
                exerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FirestoreError", "Error getting exercises: ", e);
            }
        });
    }
    private void navigateToExerciseDetails(Exercise exercise) {
        EndUserExerciseDetailsFragment detailsFragment = new EndUserExerciseDetailsFragment();
        Bundle args = new Bundle();
        args.putString("name", exercise.getName());
        args.putString("time", exercise.getTime());
        args.putInt("caloriesBurnt", exercise.getCaloriesBurnt());
        args.putString("howTo", exercise.getHowTo());
        detailsFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.endUserFragmentContainerView, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
