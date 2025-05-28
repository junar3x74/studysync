package com.example.studysync.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.studysync.Managers.DeckManager;
import com.example.studysync.R;
import com.example.studysync.models.Deck;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ShareFragment extends Fragment {

    private DeckManager deckManager;
    private MaterialButton selectDeckToShareButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        deckManager = new DeckManager(requireContext());

        selectDeckToShareButton = view.findViewById(R.id.selectDeckToShareButton);

        // Share Deck: Show a dialog to select from your decks, then copy or share the deck code
        selectDeckToShareButton.setOnClickListener(v -> {
            List<Deck> myDecks = deckManager.getAllDecks(getCurrentUserId());
            if (myDecks.isEmpty()) {
                Toast.makeText(getContext(), "No decks to share!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Build an array of deck titles for the dialog
            String[] deckTitles = new String[myDecks.size()];
            for (int i = 0; i < myDecks.size(); i++) {
                deckTitles[i] = myDecks.get(i).getTitle();
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Select a Deck to Share")
                    .setItems(deckTitles, (dialog, which) -> {
                        Deck selectedDeck = myDecks.get(which);
                        String deckCode = selectedDeck.getId();
                        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("Deck Code", deckCode));
                        Toast.makeText(getContext(), "Deck code copied: " + deckCode, Toast.LENGTH_SHORT).show();
                        // Optionally: Launch a share intent or QR code here
                    })
                    .show();
        });

        // You can add similar logic for QR code scanning if you integrate a QR library

        return view;
    }

    private String getCurrentUserId() {
        return requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("current_user_id", null);
    }
}