package com.amirmohammed.seniorstepsfirebase;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNoteFragment extends DialogFragment {
    private EditText editTextTitle, editTextContent;
    private Button buttonAdd;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTitle = view.findViewById(R.id.add_note_et_title);
        editTextContent = view.findViewById(R.id.add_note_et_content);
        buttonAdd = view.findViewById(R.id.add_note_btn);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromUi();

            }
        });
    }

    private void getDataFromUi() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all data", Toast.LENGTH_SHORT).show();
            return;
        }

        addNoteToCloud(title, content);
    }

    private void addNoteToCloud(String title, String content) {
        String noteId = String.valueOf(System.currentTimeMillis());

        Note note = new Note(noteId, title, content);
        // Notes > UID > more than notes
        firestore.collection("notes")
                .document(auth.getCurrentUser().getUid())
                    .collection("myNotes")
                .document(noteId)
                .set(note)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Note Added", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                });
    }
}
