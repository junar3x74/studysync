package com.example.studysync.adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysync.R;
import com.example.studysync.models.Flashcard;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardVH> {
    private List<Flashcard> flashcards;

    public FlashcardAdapter(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    @NonNull
    @Override
    public FlashcardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardVH holder, int position) {
        Flashcard flashcard = flashcards.get(position);
        holder.frontText.setText(flashcard.getFront());
        holder.backText.setText(flashcard.getBack());

        // Ensure starting state is front visible
        holder.front.setVisibility(View.VISIBLE);
        holder.back.setVisibility(View.GONE);
        holder.showingFront = true;

        holder.cardView.setOnClickListener(v -> holder.flipCard(v.getContext()));
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    static class FlashcardVH extends RecyclerView.ViewHolder {
        View cardView;
        LinearLayout front, back;
        TextView frontText, backText;
        boolean showingFront = true;

        FlashcardVH(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            front = itemView.findViewById(R.id.front);
            back = itemView.findViewById(R.id.back);
            frontText = itemView.findViewById(R.id.textFront);
            backText = itemView.findViewById(R.id.textBack);
        }

        void flipCard(Context context) {
            AnimatorSet outAnim, inAnim;
            if (showingFront) {
                outAnim = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_out);
                inAnim  = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_in);
                outAnim.setTarget(front);
                inAnim.setTarget(back);
                outAnim.start();
                inAnim.start();
                front.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            } else {
                outAnim = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_out);
                inAnim  = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_in);
                outAnim.setTarget(back);
                inAnim.setTarget(front);
                outAnim.start();
                inAnim.start();
                back.setVisibility(View.GONE);
                front.setVisibility(View.VISIBLE);
            }
            showingFront = !showingFront;
        }
    }
}