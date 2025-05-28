package com.example.studysync;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

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
    private static final String TAG = "FlashcardActivity";

    private FirebaseFirestore db;
    private List<Flashcard> flashcards = new ArrayList<>();
    private FlashcardPagerAdapter adapter;
    private String deckId;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard);

        db = FirebaseFirestore.getInstance();
        deckId = getIntent().getStringExtra("deck_id");

        if (deckId == null) {
            Toast.makeText(this, "Error: No deck selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewPager = findViewById(R.id.viewPager);
        adapter = new FlashcardPagerAdapter(this, flashcards);
        viewPager.setAdapter(adapter);

        loadFlashcardsFromFirestore(deckId);

        ImageButton goback = findViewById(R.id.buttonBack);
        goback.setOnClickListener(view -> finish());

        FloatingActionButton fab = findViewById(R.id.createFlashcardFab);
        fab.setOnClickListener(v -> {
            new CreateFlashcardDialogFragment(deckId, newFlashcard -> {
                saveFlashcardToFirestore(newFlashcard);
            }).show(getSupportFragmentManager(), "create_flashcard");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFlashcardsFromFirestore(String deckId) {
        Log.d(TAG, "Loading flashcards for deck: " + deckId);

        db.collection("decks").document(deckId).collection("flashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    flashcards.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Flashcard card = doc.toObject(Flashcard.class);
                            // Ensure the flashcard has an ID from the document
                            if (card.getId() == null || card.getId().isEmpty()) {
                                // Create new flashcard with document ID and all fields
                                card = new Flashcard(
                                        doc.getId(),
                                        card.getDeck_id(),
                                        card.getQuestion(),
                                        card.getAnswer(),
                                        card.getCreatedAt(),
                                        card.getIsFavorite(),
                                        1 // Assume downloaded from Firebase = synced
                                );
                            }
                            flashcards.add(card);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing flashcard: " + doc.getId(), e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + flashcards.size() + " flashcards");

                    // Update deck card count
                    updateDeckCardCount(deckId, flashcards.size());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading flashcards from Firebase", e);
                    Toast.makeText(this, "Failed to load flashcards", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveFlashcardToFirestore(Flashcard flashcard) {
        Log.d(TAG, "Saving flashcard: " + flashcard.getQuestion());

        db.collection("decks").document(deckId).collection("flashcards")
                .document(flashcard.getId())
                .set(flashcard)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Flashcard saved successfully");
                    flashcards.add(flashcard);
                    adapter.notifyItemInserted(flashcards.size() - 1);

                    // Move to the new flashcard
                    viewPager.setCurrentItem(flashcards.size() - 1, true);

                    // Update deck card count
                    updateDeckCardCount(deckId, flashcards.size());

                    Toast.makeText(this, "Flashcard created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving flashcard to Firebase", e);
                    Toast.makeText(this, "Failed to create flashcard", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDeckCardCount(String deckId, int cardCount) {
        db.collection("decks").document(deckId)
                .update("cardCount", cardCount)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Updated deck card count to: " + cardCount);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating deck card count", e);
                });
    }
}