package com.amirmohammed.seniorstepsfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityA";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    RecyclerView recyclerView;
    NoteAdater noteAdater;
    List<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            return;
        }
        else {
            getNotesFromCloud();

        }

        recyclerView = findViewById(R.id.main_rv);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));

        noteAdater = new NoteAdater(noteList);

        recyclerView.setAdapter(noteAdater);

    }

    private void getNotesFromCloud() {
        firestore.collection("notes")
                .document(auth.getCurrentUser().getUid())
                .collection("myNotes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        noteList.clear();

                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Note note = snapshot.toObject(Note.class);
                            noteList.add(note);
                        }

                        noteAdater.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;

            case R.id.menu_add:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(new AddNoteFragment(), "AddNoteFragment")
                        .commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
