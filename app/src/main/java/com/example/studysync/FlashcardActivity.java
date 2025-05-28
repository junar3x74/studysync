package com.example.studysync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.studysync.Managers.FlashcardManager;
import com.example.studysync.fragments.CreateFlashcardDialogFragment;
import com.example.studysync.models.Flashcard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {
    private static final String TAG = "FlashcardActivity";

    private List<Flashcard> flashcards = new ArrayList<>();
    private FlashcardPagerAdapter adapter;
    private String deckId;
    private ViewPager2 viewPager;
    private FlashcardManager flashcardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard);

        deckId = getIntent().getStringExtra("deck_id");
        flashcardManager = new FlashcardManager(this);

        if (deckId == null) {
            Toast.makeText(this, "Error: No deck selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewPager = findViewById(R.id.viewPager);
        adapter = new FlashcardPagerAdapter(this, flashcards);
        viewPager.setAdapter(adapter);

        loadFlashcardsFromLocal(deckId);

        if (isNetworkAvailable()) {
            syncUnsyncedFlashcardsToFirebase();
            syncFlashcardsFromFirebase();
        }

        ImageButton goback = findViewById(R.id.buttonBack);
        goback.setOnClickListener(view -> finish());

        FloatingActionButton fab = findViewById(R.id.createFlashcardFab);
        fab.setOnClickListener(v -> {
            new CreateFlashcardDialogFragment(deckId, newFlashcard -> {
                saveFlashcardLocallyAndSync(newFlashcard);
                Toast.makeText(this, "Flashcard saved locally!", Toast.LENGTH_SHORT).show();
            }).show(getSupportFragmentManager(), "create_flashcard");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFlashcardsFromLocal(String deckId) {
        flashcards.clear();
        flashcards.addAll(flashcardManager.getAllFlashcards(deckId));
        adapter.notifyDataSetChanged();
    }

    private void saveFlashcardLocallyAndSync(Flashcard card) {
        flashcardManager.insertOrUpdateFlashcard(card, 0); // not synced
        loadFlashcardsFromLocal(deckId);
        if (isNetworkAvailable()) syncUnsyncedFlashcardsToFirebase();
    }

    private void syncUnsyncedFlashcardsToFirebase() {
        List<Flashcard> unsynced = flashcardManager.getUnsyncedFlashcards();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (Flashcard card : unsynced) {
            db.collection("decks").document(deckId)
                    .collection("flashcards").document(card.getId())
                    .set(card)
                    .addOnSuccessListener(aVoid -> flashcardManager.updateFlashcardSyncStatus(card.getId(), 1));
        }
    }

    private void syncFlashcardsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("decks").document(deckId).collection("flashcards").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Flashcard card = doc.toObject(Flashcard.class);
                        card.setSync_status(1); // mark as synced
                        flashcardManager.insertOrUpdateFlashcard(card, 1);
                    }
                    loadFlashcardsFromLocal(deckId);
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}