package com.example.studysync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.studysync.adapters.FlashcardPagerAdapter;
import com.example.studysync.fragments.CreateFlashcardDialogFragment;
import com.example.studysync.fragments.DeckFragment;
import com.example.studysync.models.Flashcard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard);

        String deckId = getIntent().getStringExtra("deck_id");
        // TODO: load flashcards from DB for deckId
        List<Flashcard> flashcards = loadMockFlashcards(deckId);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        FlashcardPagerAdapter adapter = new FlashcardPagerAdapter(this, flashcards);
        viewPager.setAdapter(adapter);

        ImageButton goback = findViewById(R.id.buttonBack);

        goback.setOnClickListener(view -> finish());

        FloatingActionButton fab = findViewById(R.id.createFlashcardFab);
        fab.setOnClickListener(v -> {
            new CreateFlashcardDialogFragment(deckId, newFlashcard -> {
                flashcards.add(newFlashcard);
                adapter.notifyItemInserted(flashcards.size() - 1);
            }).show(getSupportFragmentManager(), "create_flashcard");
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private List<Flashcard> loadMockFlashcards(String deckId) {
        List<Flashcard> list = new ArrayList<>();
        list.add(new Flashcard("1", deckId, "What is Java?", "A programming language."));
        list.add(new Flashcard("2", deckId, "What is XML?", "A markup language."));
        return list;
    }


}