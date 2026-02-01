package com.example.ellehacks26.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ellehacks26.R;

public class CreditCardLesson extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public CreditCardLesson() {
        // Required empty public constructor
    }

    public static CreditCardLesson newInstance(String param1, String param2) {
        CreditCardLesson fragment = new CreditCardLesson();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit_card_lesson, container, false);

        Button nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            // prep lesson content to pass to quiz
            Bundle args = new Bundle();
            args.putString("lesson_content", getLessonContent());
            args.putString("topic", getTopic()); // "Credit Cards", "Budgeting", "Stocks"

            Fragment quizFragment = new CreditCardQuizFragment();

            quizFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.changeFragments, quizFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private String getLessonContent() {
        // extract text from lesson - HARDCODED!!! need to store in strings.xml later
        return "A credit card lets you buy things even if you don't have the money right now. For example, you might use it to buy a video game or school supplies. The bank pays for it first, and then you promise to pay the bank back within a limited time. Using a credit card responsibly can be helpful, but spending more than you can pay back can lead to debt which means you owe money and have to pay it back later, sometimes with extra fees called interest.";
    }

    private String getTopic() {
        return "Credit Cards";
    }
}
