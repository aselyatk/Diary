package com.example.diary.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.diary.R;
import com.example.diary.data.DiaryEntry;
import com.example.diary.databinding.ActivityAddEditBinding;

public class AddEditDiaryActivity extends AppCompatActivity {
    private ActivityAddEditBinding b;
    private DiaryViewModel vm;
    private int entryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DiaryTheme);
        super.onCreate(savedInstanceState);
        b = ActivityAddEditBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(DiaryViewModel.class);

        if (getIntent().hasExtra("id")) {
            entryId = getIntent().getIntExtra("id", -1);
            vm.getById(entryId).observe(this, entry -> {
                if (entry != null) {
                    b.editTitle.setText(entry.getTitle());
                    b.editContent.setText(entry.getContent());
                }
            });
        }

        b.btnSave.setOnClickListener(v -> {
            String title = b.editTitle.getText().toString().trim();
            String content = b.editContent.getText().toString().trim();
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, R.string.msg_fill, Toast.LENGTH_SHORT).show();
                return;
            }
            long now = System.currentTimeMillis();
            DiaryEntry entry = new DiaryEntry(title, content, "", now);
            if (entryId > 0) {
                entry.setId(entryId);
                vm.update(entry);
            } else {
                vm.insert(entry);
            }
            finish();
        });
    }
}
