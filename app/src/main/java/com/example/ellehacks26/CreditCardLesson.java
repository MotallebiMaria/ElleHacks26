package com.example.ellehacks26;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an instance of the fragment you want to navigate to
                Fragment quizFragment = QuizQuestionsFragment.newInstance(R.layout.layout_credit_card_quiz);

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
