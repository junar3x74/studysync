package com.example.studysync.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysync.FlashcardActivity;
import com.example.studysync.R;
import com.example.studysync.adapters.DeckAdapter;
import com.example.studysync.models.Deck;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeckFragment extends Fragment {
    private static final String TAG = "DeckFragment";

    private DeckAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private TabLayout tabLayout;
    private TextInputEditText searchInput;
    private final List<Deck> decks = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deck, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        recyclerView = root.findViewById(R.id.decksRecyclerView);
        emptyState   = root.findViewById(R.id.emptyStateContainer);
        tabLayout    = root.findViewById(R.id.tabLayout);
        searchInput  = root.findViewById(R.id.searchInput);
        FloatingActionButton fab = root.findViewById(R.id.createNewDeckFab);

        // Load decks from Firebase instead of mock data
        loadDecksFromFirebase();

        adapter = new DeckAdapter(decks, new DeckAdapter.OnDeckClickListener() {
            @Override
            public void onDeckClick(Deck deck) {
                Intent intent = new Intent(getContext(), FlashcardActivity.class);
                intent.putExtra("deck_id", deck.getId());
                startActivity(intent);
            }

            @Override
            public void onEditDeck(Deck deck) {
                CreateDeckDialogFragment dialog = new CreateDeckDialogFragment(deck, updatedDeck -> {
                    updateDeckInFirebase(updatedDeck);
                });
                dialog.show(getParentFragmentManager(), "edit_deck");
            }

            @Override
            public void onDeleteDeck(Deck deck) {
                deleteDeckFromFirebase(deck);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        showOrHideEmpty(decks.size());

        fab.setOnClickListener(v -> {
            new CreateDeckDialogFragment(new CreateDeckDialogFragment.CreateListener() {
                @Override
                public void onDeckCreated(Deck newDeck) {
                    saveDeckToFirebase(newDeck);
                }
            }).show(getParentFragmentManager(), "create_deck");
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab)   { applyFilters(); }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab){ applyFilters(); }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c)     { applyFilters(); }
            @Override public void afterTextChanged(Editable e)                            { }
        });
    }

    private void loadDecksFromFirebase() {
        db.collection("decks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    decks.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Deck deck = doc.toObject(Deck.class);
                            // Ensure the deck has an ID from the document
                            if (deck.getId() == null || deck.getId().isEmpty()) {
                                // If no ID in the object, use the document ID
                                deck = new Deck(doc.getId(), deck.getTitle(), deck.getCreatedAt(),
                                        deck.isFavorite(), deck.getCardCount());
                            }
                            decks.add(deck);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing deck: " + doc.getId(), e);
                        }
                    }
                    adapter.updateList(decks);
                    applyFilters();
                    Log.d(TAG, "Loaded " + decks.size() + " decks from Firebase");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading decks from Firebase", e);
                    Toast.makeText(getContext(), "Failed to load decks", Toast.LENGTH_SHORT).show();
                    // Load mock data as fallback
                    decks.addAll(loadMockDecks());
                    adapter.updateList(decks);
                    applyFilters();
                });
    }

    private void saveDeckToFirebase(Deck deck) {
        db.collection("decks")
                .document(deck.getId())
                .set(deck)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Deck saved successfully: " + deck.getTitle());
                    decks.add(deck);
                    adapter.updateList(decks);
                    applyFilters();
                    Toast.makeText(getContext(), "Deck created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving deck to Firebase", e);
                    Toast.makeText(getContext(), "Failed to create deck", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDeckInFirebase(Deck deck) {
        db.collection("decks")
                .document(deck.getId())
                .set(deck)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Deck updated successfully: " + deck.getTitle());
                    // Update local list
                    int index = -1;
                    for (int i = 0; i < decks.size(); i++) {
                        if (decks.get(i).getId().equals(deck.getId())) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        decks.set(index, deck);
                        adapter.updateList(decks);
                        applyFilters();
                    }
                    Toast.makeText(getContext(), "Deck updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating deck in Firebase", e);
                    Toast.makeText(getContext(), "Failed to update deck", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteDeckFromFirebase(Deck deck) {
        // First delete all flashcards in the deck
        db.collection("decks").document(deck.getId()).collection("flashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Delete all flashcards
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }

                    // Then delete the deck itself
                    db.collection("decks").document(deck.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Deck deleted successfully: " + deck.getTitle());
                                decks.remove(deck);
                                adapter.updateList(decks);
                                applyFilters();
                                Toast.makeText(getContext(), "Deleted: " + deck.getTitle(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error deleting deck from Firebase", e);
                                Toast.makeText(getContext(), "Failed to delete deck", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting flashcards", e);
                    Toast.makeText(getContext(), "Failed to delete deck", Toast.LENGTH_SHORT).show();
                });
    }

    private void applyFilters() {
        String query = searchInput.getText() != null
                ? searchInput.getText().toString()
                : "";
        int tabIndex = tabLayout.getSelectedTabPosition();
        adapter.filter(query, tabIndex);
        showOrHideEmpty(adapter.getItemCount());
    }

    private void showOrHideEmpty(int count) {
        if (count == 0) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Keep as fallback for offline mode or initial testing
    private List<Deck> loadMockDecks() {
        List<Deck> list = new ArrayList<>();
        long DAY = 24L * 3600 * 1000;
        long now = System.currentTimeMillis();
        list.add(new Deck("1", "Biology Terms", now - 2*DAY, false, 12));
        list.add(new Deck("2", "Java Syntax",   now - 8*DAY, true,   8));
        list.add(new Deck("3", "Spanish Verbs", now - 1*DAY, false, 20));
        return list;
    }
}