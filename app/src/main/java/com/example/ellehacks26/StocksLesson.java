package com.example.ellehacks26;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Fragment for the Budgeting Lesson using reusable header + dynamic content.
 */
public class StocksLesson extends Fragment {

    public StocksLesson() {}

    public static StocksLesson newInstance() {
        return new StocksLesson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks_lesson, container, false);

        Button nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an instance of the fragment you want to navigate to
                Fragment quizFragment = QuizQuestionsStocks.newInstance(R.layout.layout_stocks_quiz);

                // Begin the fragment transaction
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace the current fragment with the new one
                transaction.replace(R.id.changeFragments, quizFragment);
                // Make sure R.id.fragment_container is the ID of your FrameLayout or container in your activity

                // Optional: add to back stack so user can press back
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return view;
    }
}