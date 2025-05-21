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
import com.example.studysync.fragments.DeckFragment;
import com.example.studysync.models.Flashcard;

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

        goback.setOnClickListener(view -> {
            Intent intent = new Intent(FlashcardActivity.this , DeckFragment.class);
            startActivity(intent);
            finish();
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