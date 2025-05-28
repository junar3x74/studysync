package com.example.studysync.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysync.LoginActivity;
import com.example.studysync.R;
import com.example.studysync.adapters.DeckAdapter;
import com.example.studysync.Managers.DeckManager;
import com.example.studysync.models.Deck;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private RecyclerView recentDecksList;
    private LinearLayout emptyStateContainer;
    private DeckAdapter adapter;
    private List<Deck> allDecks = new ArrayList<>();
    private TextView totalDecksCount, totalCardsCount, lastSyncTimeTextView;
    private ListenerRegistration decksListener;
    private MaterialButton syncNowButton;
    private DeckManager deckManager;

    private static final String PREFS_NAME = "user_prefs";
    private static final String LAST_SYNC_KEY = "last_sync_time";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        setHasOptionsMenu(true);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        }

        recentDecksList = root.findViewById(R.id.recentDecksList);
        emptyStateContainer = root.findViewById(R.id.emptyStateContainer);
        totalDecksCount = root.findViewById(R.id.totalDecksCount);
        totalCardsCount = root.findViewById(R.id.totalCardsCount);
        lastSyncTimeTextView = root.findViewById(R.id.lastSyncTime);
        syncNowButton = root.findViewById(R.id.syncNowButton);

        deckManager = new DeckManager(requireContext());

        adapter = new DeckAdapter(new ArrayList<>(), new DeckAdapter.OnDeckClickListener() {
            @Override
            public void onDeckClick(Deck deck) { }
            @Override
            public void onEditDeck(Deck deck) { }
            @Override
            public void onDeleteDeck(Deck deck) { }
        });

        recentDecksList.setLayoutManager(new LinearLayoutManager(getContext()));
        recentDecksList.setAdapter(adapter);

        loadLastSyncTime();
        setupSyncNowButton();

        listenToDecksRealtime();
    }

    private void setupSyncNowButton() {
        syncNowButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                syncAllDataToCloud();
            } else {
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncAllDataToCloud() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString("current_user_id", null);
        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Deck> unsynced = deckManager.getUnsyncedDecks();
        if (unsynced.isEmpty()) {
            Toast.makeText(getContext(), "No new changes to sync!", Toast.LENGTH_SHORT).show();
            updateLastSyncTime(); // Still update sync time for user feedback
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int[] completed = {0}; // To track when all decks are synced
        for (Deck deck : unsynced) {
            db.collection("decks").document(deck.getId()).set(deck)
                    .addOnSuccessListener(aVoid -> {
                        deckManager.updateDeckSyncStatus(deck.getId(), 1);
                        completed[0]++;
                        if (completed[0] == unsynced.size()) {
                            updateLastSyncTime();
                            Toast.makeText(getContext(), "Sync complete!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Sync failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        if (unsynced.isEmpty()) updateLastSyncTime();
    }

    private void updateLastSyncTime() {
        long now = System.currentTimeMillis();
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(LAST_SYNC_KEY, now)
                .apply();
        displayLastSyncTime(now);
    }

    private void loadLastSyncTime() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastSync = prefs.getLong(LAST_SYNC_KEY, 0);
        displayLastSyncTime(lastSync);
    }

    private void displayLastSyncTime(long millis) {
        if (millis == 0) {
            lastSyncTimeTextView.setText("--:--");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        lastSyncTimeTextView.setText(sdf.format(new Date(millis)));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        if (e != null) return;
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
        totalDecksCount.setText(String.valueOf(allDecks.size()));
        int cardSum = 0;
        for (Deck d : allDecks) cardSum += d.getCardCount();
        totalCardsCount.setText(String.valueOf(cardSum));
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

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}