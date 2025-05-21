package com.example.studysync.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studysync.fragments.FlashcardFragment;
import com.example.studysync.models.Flashcard;

import java.util.List;

public class FlashcardPagerAdapter extends FragmentStateAdapter {
    private final List<Flashcard> flashcards;

    public FlashcardPagerAdapter(@NonNull FragmentActivity fa, List<Flashcard> flashcards) {
        super(fa);
        this.flashcards = flashcards;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Flashcard card = flashcards.get(position);
        return FlashcardFragment.newInstance(card.getFrontText(), card.getBackText());
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }
}
