package com.example.studysync.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studysync.R;

public class FlashcardFragment extends Fragment {
    private static final String ARG_FRONT = "front";
    private static final String ARG_BACK = "back";
    private boolean showingFront = true;

    public static FlashcardFragment newInstance(String front, String back) {
        FlashcardFragment fragment = new FlashcardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRONT, front);
        args.putString(ARG_BACK, back);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard, container, false);
        TextView cardText = view.findViewById(R.id.cardText);

        Bundle args = getArguments();
        String front = args != null ? args.getString(ARG_FRONT) : "";
        String back = args != null ? args.getString(ARG_BACK) : "";
        cardText.setText(front);

        view.setOnClickListener(v -> {
            showingFront = !showingFront;
            cardText.setText(showingFront ? front : back);
        });

        return view;
    }
}