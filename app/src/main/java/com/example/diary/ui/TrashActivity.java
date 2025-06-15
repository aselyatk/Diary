package com.example.diary.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // 1) Навешиваем тулбар
        setSupportActionBar(b.topAppBar);
        if (getSupportActionBar() != null) {
            // включаем стрелку «назад»
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // заголовок уже задан в XML через app:title
        }

        // 2) RecyclerView + Adapter
        adapter = new DiaryAdapter();
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerView.setAdapter(adapter);

        // 3) ViewModel — получаем список «мягко» удалённых записей
        vm = new ViewModelProvider(this).get(DiaryViewModel.class);
        vm.getTrash().observe(this, list -> adapter.setEntries(list));

        // 4) Долгое нажатие — диалог «восстановить» / «удалить навсегда»
        adapter.setListener(new DiaryAdapter.OnItemClickListener() {
            @Override public void onItemClick(DiaryEntry e) { /* клики в корзине не обрабатываем */ }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 5) Обработка стрелки «назад» в тулбаре
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
