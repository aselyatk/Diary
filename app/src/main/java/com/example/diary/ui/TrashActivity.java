package com.example.diary.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.diary.R;
import com.example.diary.data.DiaryEntry;
import com.example.diary.databinding.ActivityTrashBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TrashActivity extends AppCompatActivity {
    private ActivityTrashBinding b;
    private DiaryViewModel vm;
    private DiaryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        b = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Тулбар
        setSupportActionBar(b.topAppBar);
        getSupportActionBar().setTitle(R.string.title_trash);

        // RecyclerView + Adapter
        adapter = new DiaryAdapter();
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerView.setAdapter(adapter);

        // ViewModel
        vm = new ViewModelProvider(this).get(DiaryViewModel.class);
        vm.getTrash().observe(this, list -> adapter.setEntries(list));

        // Long-click: восстановить или удалить навсегда
        adapter.setListener(new DiaryAdapter.OnItemClickListener() {
            @Override public void onItemClick(DiaryEntry e) { }
            @Override public void onItemLongClick(DiaryEntry e) {
                new MaterialAlertDialogBuilder(TrashActivity.this)
                        .setTitle(R.string.dialog_restore_title)
                        .setMessage(R.string.dialog_restore_message)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setNeutralButton(R.string.action_delete_forever,
                                (d,w)-> vm.permanentDelete(e))
                        .setPositiveButton(R.string.action_restore,
                                (d,w)-> vm.restore(e))
                        .show();
            }
        });
    }
}
