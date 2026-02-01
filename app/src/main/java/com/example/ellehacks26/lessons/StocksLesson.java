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

        nextButton.setOnClickListener(v -> {
            // Prepare lesson content to pass to quiz
            Bundle args = new Bundle();
            args.putString("lesson_content", getLessonContent());
            args.putString("topic", getTopic()); // "Credit Cards", "Budgeting", "Stocks"

            // Create appropriate quiz fragment
            Fragment quizFragment = new StocksQuizFragment();

            quizFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.changeFragments, quizFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private String getLessonContent() {
        return "A stock means you own a small piece of a company. When you buy a stock, you are investing your money and becoming a part-owner. For example, if a company sells toys and does well, the value of your stock might go up. If the company doesn’t do well, the value might go down. Stocks can help people grow their money over time, but they also come with risks.";
    }

    private String getTopic() {
        return "Stocks";
    }
}