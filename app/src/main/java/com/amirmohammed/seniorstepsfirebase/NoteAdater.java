package com.amirmohammed.seniorstepsfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdater extends RecyclerView.Adapter<NoteAdater.NoteHolder> {
    private List<Note> notes;

    public NoteAdater(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.note_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = notes.get(position);

        holder.textViewTitle.setText(note.getTitle());

        holder.textViewContent.setText(note.getContent());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    class NoteHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewContent;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.note_item_tv_title);
            textViewContent = itemView.findViewById(R.id.note_item_tv_content);
        }
    }
}
