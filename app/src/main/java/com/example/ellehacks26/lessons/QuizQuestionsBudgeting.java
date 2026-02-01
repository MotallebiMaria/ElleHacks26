package com.example.ellehacks26.lessons;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ellehacks26.R;

/**
 * A reusable fragment for quizzes.
 */
public class QuizQuestionsBudgeting extends Fragment {

    private static final String ARG_LAYOUT_ID = "layoutId";

    private int layoutId;

    public QuizQuestionsBudgeting() {
        // Required empty constructor
    }

    /** Factory method to create a fragment with a specific quiz layout */
    public static QuizQuestionsBudgeting newInstance(int layoutId) {
        QuizQuestionsBudgeting fragment = new QuizQuestionsBudgeting();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            layoutId = getArguments().getInt(ARG_LAYOUT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the main fragment layout
        View rootView = inflater.inflate(R.layout.layout_budgeting_quiz, container, false);

        Button nextButton = rootView.findViewById(R.id.nextButton);

        // Set click listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to fragment_lesson_page
                Fragment lessonFragment = new LessonFragment(); // replace with your fragment class
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.changeFragments, lessonFragment); // fragment_container is your Activity's FrameLayout
                transaction.addToBackStack(null); // optional: allows back button to go back
                transaction.commit();
            }
        });


        return rootView;
    }
}

