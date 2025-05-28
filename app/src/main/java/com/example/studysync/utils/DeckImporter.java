package com.example.studysync.utils;

import android.content.Context;
import android.widget.Toast;
import com.example.studysync.Managers.DeckManager;
import com.example.studysync.Managers.FlashcardManager;
import com.example.studysync.models.Deck;
import com.example.studysync.models.Flashcard;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class DeckImporter {
    public static void importDeckWithCards(Context context, String deckId, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DeckManager deckManager = new DeckManager(context);
        FlashcardManager flashcardManager = new FlashcardManager(context);

        db.collection("decks").document(deckId).get()
                .addOnSuccessListener(deckSnap -> {
                    if (deckSnap.exists()) {
                        Deck deck = deckSnap.toObject(Deck.class);
                        deck.setUser_id(userId);
                        deck.setSync_status(0); // mark as unsynced
                        deckManager.insertOrUpdateDeck(deck, 0);
                        // Now fetch cards for this deck
                        db.collection("cards").whereEqualTo("deck_id", deckId).get()
                                .addOnSuccessListener(cardsSnap -> {
                                    ArrayList<Flashcard> cards = new ArrayList<>();
                                    for (QueryDocumentSnapshot cardDoc : cardsSnap) {
                                        Flashcard card = cardDoc.toObject(Flashcard.class);
                                        card.setDeck_id(deck.getId());
                                        card.setUser_id(userId);
                                        card.setSync_status(0);
                                        flashcardManager.insertOrUpdateCard(card, 0);
                                    }
                                    Toast.makeText(context, "Deck and cards imported!", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(context, "Deck not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}