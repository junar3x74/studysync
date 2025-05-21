package com.example.studysync.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.studysync.R;
import com.example.studysync.adapters.DeckAdapter;
import com.example.studysync.models.Deck;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DeckFragment extends Fragment {
    private DeckAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private TabLayout tabLayout;
    private TextInputEditText searchInput;
    private final List<Deck> decks = new ArrayList<>();

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

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        recyclerView = root.findViewById(R.id.decksRecyclerView);
        emptyState   = root.findViewById(R.id.emptyStateContainer);
        tabLayout    = root.findViewById(R.id.tabLayout);
        searchInput  = root.findViewById(R.id.searchInput);
        FloatingActionButton fab = root.findViewById(R.id.createNewDeckFab);

        decks.addAll(loadMockDecks());

        adapter = new DeckAdapter(decks, new DeckAdapter.OnDeckClickListener() {
            @Override
            public void onDeckClick(Deck deck) {

                Toast.makeText(getContext(), "Clicked: " + deck.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditDeck(Deck deck) {
                CreateDeckDialogFragment dialog = new CreateDeckDialogFragment(deck, updatedDeck -> {
                    int index = decks.indexOf(deck);
                    if (index != -1) {
                        decks.set(index, updatedDeck);
                        adapter.updateList(decks);
                        applyFilters();
                    }
                });
                dialog.show(getParentFragmentManager(), "edit_deck");
            }

            @Override
            public void onDeleteDeck(Deck deck) {
                decks.remove(deck);
                adapter.updateList(decks);
                applyFilters();
                Toast.makeText(getContext(), "Deleted: " + deck.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        showOrHideEmpty(decks.size());

        fab.setOnClickListener(v -> {
            new CreateDeckDialogFragment(newDeck -> {
                decks.add(newDeck);
                adapter.updateList(decks);
                applyFilters();
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
