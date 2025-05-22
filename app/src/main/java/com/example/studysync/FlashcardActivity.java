package com.example.studysync;

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
import com.example.studysync.models.Flashcard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Flashcard> flashcards = new ArrayList<>();
    private FlashcardPagerAdapter adapter;
    private String deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard);

        db = FirebaseFirestore.getInstance();
        deckId = getIntent().getStringExtra("deck_id");

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        adapter = new FlashcardPagerAdapter(this, flashcards);
        viewPager.setAdapter(adapter);

        loadFlashcardsFromFirestore(deckId);

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

    private void loadFlashcardsFromFirestore(String deckId) {
        db.collection("decks").document(deckId).collection("flashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    flashcards.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Flashcard card = doc.toObject(Flashcard.class);
                        flashcards.add(card);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void saveFlashcardToFirestore(Flashcard flashcard) {
        db.collection("decks").document(deckId).collection("flashcards")
                .document(flashcard.getId())
                .set(flashcard)
                .addOnSuccessListener(aVoid -> {
                    flashcards.add(flashcard);
                    adapter.notifyItemInserted(flashcards.size() - 1);
                });
    }
}
