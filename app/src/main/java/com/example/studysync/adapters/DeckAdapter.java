package com.example.studysync.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysync.R;
import com.example.studysync.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckVH> {

    public interface OnDeckClickListener {
        void onDeckClick(Deck deck);
        void onEditDeck(Deck deck);
        void onDeleteDeck(Deck deck);
    }

    private final List<Deck> originalList;
    private List<Deck> filteredList;
    private final OnDeckClickListener listener;

    public DeckAdapter(List<Deck> decks, OnDeckClickListener listener) {
        this.originalList = new ArrayList<>(decks);
        this.filteredList = new ArrayList<>(decks);
        this.listener     = listener;
    }

    @NonNull
    @Override
    public DeckVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deck, parent, false);
        return new DeckVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckVH holder, int position) {
        Deck deck = filteredList.get(position);

        holder.title.setText(deck.getTitle());

        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                deck.getCreatedAt(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.subtitle.setText(deck.getCardCount() + " cards • " + ago);

        int favRes = deck.isFavorite()
                ? R.drawable.ic_favorite_filled
                : R.drawable.ic_favorite;
        holder.favIcon.setImageResource(favRes);

        holder.itemView.setOnClickListener(v -> listener.onDeckClick(deck));

        holder.favIcon.setOnClickListener(v -> {
            deck.setFavorite(!deck.isFavorite());
            notifyItemChanged(position);
            // TODO: persist favorite state in your DB
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(holder.itemView.getContext(), v);
            popup.inflate(R.menu.deck_item_menu);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    listener.onEditDeck(deck);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    listener.onDeleteDeck(deck);
                    return true;
                }
                return false;
            });
            popup.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void updateList(List<Deck> newList) {
        originalList.clear();
        originalList.addAll(newList);
        filter("", 0);
    }

    public void filter(String query, int tabPosition) {
        List<Deck> temp = new ArrayList<>();
        long weekMs = 7L * 24 * 60 * 60 * 1000;
        long now    = System.currentTimeMillis();

        for (Deck d : originalList) {
            boolean matchesSearch = d.getTitle().toLowerCase()
                    .contains(query.toLowerCase());
            boolean matchesTab;
            switch (tabPosition) {
                case 1:
                    matchesTab = now - d.getCreatedAt() < weekMs;
                    break;
                case 2:
                    matchesTab = d.isFavorite();
                    break;
                default:
                    matchesTab = true;
            }
            if (matchesSearch && matchesTab) {
                temp.add(d);
            }
        }
        filteredList = temp;
        notifyDataSetChanged();
    }

    static class DeckVH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView favIcon;

        DeckVH(@NonNull View itemView) {
            super(itemView);
            title    = itemView.findViewById(R.id.textDeckTitle);
            subtitle = itemView.findViewById(R.id.textDeckSubtitle);
            favIcon  = itemView.findViewById(R.id.imageFavorite);
        }
    }
}
