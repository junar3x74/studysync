package com.example.studysync.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.studysync.R;
import com.example.studysync.adapters.FlashcardAdapter;
import com.example.studysync.models.Flashcard;

import java.util.List;

public class FlashcardFragment extends Fragment {

    private ViewPager2 viewPager;
    private List<Flashcard> flashcards; // Pass this from previous fragment/activity!

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flashcard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        viewPager = root.findViewById(R.id.viewPagerFlashcards);

        // TODO: Get flashcards from arguments or ViewModel
        flashcards = getMockFlashcards();

        FlashcardAdapter adapter = new FlashcardAdapter(flashcards);
        viewPager.setAdapter(adapter);
    }

    // Mock data for demo
    private List<Flashcard> getMockFlashcards() {
        return java.util.Arrays.asList(
                new Flashcard("What is the capital of France?", "Paris"),
                new Flashcard("Largest planet in the Solar System?", "Jupiter"),
                new Flashcard("2 + 2?", "4")
        );
    }
}