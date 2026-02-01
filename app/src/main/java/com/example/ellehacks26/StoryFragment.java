package com.example.ellehacks26;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StoryFragment extends Fragment {

    public StoryFragment() { }

    public static StoryFragment newInstance(String param1, String param2) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_story_page, container, false);

        // Get the LinearLayouts inside the CardViews
        View decision1 = view.findViewById(R.id.decision1_layout);
        View decision2 = view.findViewById(R.id.decision2_layout);
        View decision3 = view.findViewById(R.id.decision3_layout);
        View decision4 = view.findViewById(R.id.decision4_layout);

        View[] decisions = new View[]{decision1, decision2, decision3, decision4};
        final View[] selectedDecision = {null};

        // Set click listener for each decision
        for (View decision : decisions) {
            decision.setOnClickListener(v -> {
                // Reset all to default background
                for (View d : decisions) {
                    d.setBackgroundResource(R.drawable.border);
                }
                // Highlight the selected one
                v.setBackgroundResource(R.drawable.border_selected);
                selectedDecision[0] = v;
            });
        }

        // Choose button
        View chooseButton = view.findViewById(R.id.choose_button);
        chooseButton.setOnClickListener(v -> {
            if (selectedDecision[0] != null) {
                int choiceIndex = java.util.Arrays.asList(decisions).indexOf(selectedDecision[0]) + 1;
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Your Choice")
                        .setMessage("You selected Decision " + choiceIndex)
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("No Selection")
                        .setMessage("Please select an option first!")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        return view;
    }
}
