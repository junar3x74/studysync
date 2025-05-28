package com.example.studysync.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysync.R;
import com.example.studysync.adapters.DeckAdapter;
import com.example.studysync.models.Deck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recentDecksList;
    private LinearLayout emptyStateContainer;
    private DeckAdapter adapter;
    private List<Deck> allDecks = new ArrayList<>();
    private TextView totalDecksCount, totalCardsCount;

    private ListenerRegistration decksListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        recentDecksList = root.findViewById(R.id.recentDecksList);
        emptyStateContainer = root.findViewById(R.id.emptyStateContainer);
        totalDecksCount = root.findViewById(R.id.totalDecksCount);
        totalCardsCount = root.findViewById(R.id.totalCardsCount);

        adapter = new DeckAdapter(new ArrayList<>(), new DeckAdapter.OnDeckClickListener() {
            @Override
            public void onDeckClick(Deck deck) {
                // Optional: handle deck click (open, view, etc)
            }

            @Override
            public void onEditDeck(Deck deck) {
                // Optional: handle deck edit
            }

            @Override
            public void onDeleteDeck(Deck deck) {
                // Optional: handle deck delete
            }
        });

        recentDecksList.setLayoutManager(new LinearLayoutManager(getContext()));
        recentDecksList.setAdapter(adapter);

        listenToDecksRealtime();
    }

    private void listenToDecksRealtime() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userId = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        decksListener = db.collection("decks")
                .whereEqualTo("user_id", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable com.google.firebase.firestore.QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle error (e.g., log, show error UI)
                            return;
                        }
                        allDecks.clear();
                        if (snapshots != null) {
                            for (QueryDocumentSnapshot doc : snapshots) {
                                Deck deck = doc.toObject(Deck.class);
                                allDecks.add(deck);
                            }
                        }
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        // Update counters
        totalDecksCount.setText(String.valueOf(allDecks.size()));
        int cardSum = 0;
        for (Deck d : allDecks) cardSum += d.getCardCount();
        totalCardsCount.setText(String.valueOf(cardSum));

        // Show recent decks (last 7 days)
        List<Deck> recentDecks = getRecentDecks(allDecks);
        adapter.updateList(recentDecks);
        updateEmptyState(recentDecks.size());
    }

    private void updateEmptyState(int count) {
        if (count == 0) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            recentDecksList.setVisibility(View.GONE);
        } else {
            emptyStateContainer.setVisibility(View.GONE);
            recentDecksList.setVisibility(View.VISIBLE);
        }
    }

    // Show decks created in the last 7 days
    private List<Deck> getRecentDecks(List<Deck> decks) {
        List<Deck> recent = new ArrayList<>();
        long now = System.currentTimeMillis();
        long weekMs = 7L * 24 * 60 * 60 * 1000;
        for (Deck d : decks) {
            if (now - d.getCreatedAt() < weekMs) {
                recent.add(d);
            }
        }
        return recent;
    }

    @Override
    public void onDestroyView() {
        if (decksListener != null) {
            decksListener.remove();
        }
        super.onDestroyView();
    }
}