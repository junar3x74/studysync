package com.example.studysync.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.studysync.Managers.DeckManager;
import com.example.studysync.models.Deck;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckFragment extends Fragment {
    private static final String TAG = "DeckFragment";

    private DeckAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private TabLayout tabLayout;
    private TextInputEditText searchInput;
    private final List<Deck> decks = new ArrayList<>();
    private DeckManager deckManager;

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

        deckManager = new DeckManager(requireContext());

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        recyclerView = root.findViewById(R.id.decksRecyclerView);
        emptyState   = root.findViewById(R.id.emptyStateContainer);
        tabLayout    = root.findViewById(R.id.tabLayout);
        searchInput  = root.findViewById(R.id.searchInput);
        FloatingActionButton fab = root.findViewById(R.id.createNewDeckFab);

        // Always load decks from local DB first
        loadDecksFromLocal();

        // Try to sync to Firebase and from Firebase (if online)
        if (isNetworkAvailable()) {
            syncUnsyncedDecksToFirebase();
            syncDecksFromFirebase();
        }

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
                    saveDeckLocallyAndSync(updatedDeck);
                });
                dialog.show(getParentFragmentManager(), "edit_deck");
            }

            @Override
            public void onDeleteDeck(Deck deck) {
                deleteDeckLocallyAndSync(deck);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        showOrHideEmpty(decks.size());

        fab.setOnClickListener(v -> {
            new CreateDeckDialogFragment(new CreateDeckDialogFragment.CreateListener() {
                @Override
                public void onDeckCreated(Deck newDeck) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        newDeck.setUser_id(user.getUid());
                        if (newDeck.getId() == null || newDeck.getId().isEmpty()) {
                            newDeck.setId(UUID.randomUUID().toString());
                        }
                        newDeck.setSync_status(0);
                        saveDeckLocallyAndSync(newDeck);
                        Toast.makeText(getContext(), "Deck saved locally!", Toast.LENGTH_SHORT).show();
                    }
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

    private void loadDecksFromLocal() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userId = user.getUid();
        decks.clear();
        decks.addAll(deckManager.getAllDecks(userId));
        if (adapter != null) adapter.updateList(decks);
        applyFilters();
    }

    private void saveDeckLocallyAndSync(Deck deck) {
        deckManager.insertOrUpdateDeck(deck, 0); // 0: not synced
        loadDecksFromLocal();
        if (isNetworkAvailable()) syncUnsyncedDecksToFirebase();
    }

    private void deleteDeckLocallyAndSync(Deck deck) {
        // You need to implement a delete method in DeckManager for this
        // deckManager.deleteDeck(deck.getId());
        // For now, mark as deleted (implement as you see fit)
        decks.remove(deck);
        loadDecksFromLocal();
        if (isNetworkAvailable()) syncUnsyncedDecksToFirebase();
    }

    private void syncUnsyncedDecksToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userId = user.getUid();
        List<Deck> unsynced = deckManager.getUnsyncedDecks();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (Deck deck : unsynced) {
            db.collection("decks").document(deck.getId()).set(deck)
                    .addOnSuccessListener(aVoid -> deckManager.updateDeckSyncStatus(deck.getId(), 1));
        }
    }

    private void syncDecksFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("decks").whereEqualTo("user_id", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Deck deck = doc.toObject(Deck.class);
                        deck.setSync_status(1); // mark as synced
                        deckManager.insertOrUpdateDeck(deck, 1);
                    }
                    loadDecksFromLocal();
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void applyFilters() {
        String query = searchInput.getText() != null
                ? searchInput.getText().toString()
                : "";
        int tabIndex = tabLayout.getSelectedTabPosition();
        if (adapter != null) {
            adapter.filter(query, tabIndex);
            showOrHideEmpty(adapter.getItemCount());
        }
    }

    private void showOrHideEmpty(int count) {
        if (emptyState == null || recyclerView == null) return;
        if (count == 0) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}