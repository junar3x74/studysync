package com.example.studysync.fragments;

import android.app.Dialog;
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
import com.example.studysync.models.Flashcard;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

public class CreateFlashcardDialogFragment extends DialogFragment {

    public interface CreateFlashcardListener {
        void onFlashcardCreated(Flashcard flashcard);
    }

    private final String deckId;
    private final CreateFlashcardListener listener;

    public CreateFlashcardDialogFragment(String deckId, CreateFlashcardListener listener) {
        this.deckId = deckId;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_flashcard, container, false);

        TextInputEditText frontInput = view.findViewById(R.id.frontInput);
        TextInputEditText backInput = view.findViewById(R.id.backInput);
        Button createButton = view.findViewById(R.id.createFlashcardButton);

        createButton.setOnClickListener(v -> {
            String front = frontInput.getText().toString().trim();
            String back = backInput.getText().toString().trim();

            if (TextUtils.isEmpty(front) || TextUtils.isEmpty(back)) {
                Toast.makeText(getContext(), "Please fill both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = UUID.randomUUID().toString();
            Flashcard newFlashcard = new Flashcard(id, deckId, front, back);
            listener.onFlashcardCreated(newFlashcard);
            dismiss();
        });

        return view;
    }
}
