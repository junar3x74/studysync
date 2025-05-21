package com.example.studysync.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.studysync.R;
import com.example.studysync.models.Deck;
import com.google.android.material.textfield.TextInputEditText;

public class CreateDeckDialogFragment extends DialogFragment {

    private Deck deckToEdit;
    private CreateListener createListener;
    private EditListener editListener;

    public interface CreateListener {
        void onDeckCreated(Deck newDeck);
    }

    public interface EditListener {
        void onDeckUpdated(Deck updatedDeck);
    }

    // Use one of these constructors
    public CreateDeckDialogFragment(CreateListener listener) {
        this.createListener = listener;
    }

    public CreateDeckDialogFragment(Deck deckToEdit, EditListener listener) {
        this.deckToEdit = deckToEdit;
        this.editListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_deck, container, false);

        TextInputEditText titleInput = view.findViewById(R.id.deckTitleInput);
        Button saveBtn = view.findViewById(R.id.saveDeckButton);

        if (deckToEdit != null) {
            titleInput.setText(deckToEdit.getTitle());
        }

        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (deckToEdit != null) {
                deckToEdit.setTitle(title);
                if (editListener != null) {
                    editListener.onDeckUpdated(deckToEdit);
                }
            } else {
                Deck newDeck = new Deck(
                        String.valueOf(System.currentTimeMillis()),
                        title,
                        System.currentTimeMillis(),
                        false,
                        0
                );
                if (createListener != null) {
                    createListener.onDeckCreated(newDeck);
                }
            }
            dismiss();
        });

        return view;
    }
}
