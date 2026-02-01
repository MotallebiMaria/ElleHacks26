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
public class BudgetingLesson extends Fragment {

    public BudgetingLesson() {}

    public static BudgetingLesson newInstance() {
        return new BudgetingLesson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budgeting_lesson, container, false);

        Button nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("lesson_content", getLessonContent());
            args.putString("topic", getTopic()); // "Credit Cards", "Budgeting", "Stocks"

            Fragment quizFragment = new BudgetingQuizFragment();

            quizFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.changeFragments, quizFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private String getLessonContent() {
        return "A budget is a plan for how you will use your money. It helps you decide how much to spend, save, and share. For example, if you get $20, you might budget $10 for spending, $5 for saving, and $5 for gifts or charity. A budget helps you make sure your money doesn’t run out too fast.";
    }

    private String getTopic() {
        return "Budgeting";
    }
}