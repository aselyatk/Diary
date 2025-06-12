package com.example.diary.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_trash);
        }

        adapter = new DiaryAdapter();
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerView.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(DiaryViewModel.class);
        vm.getTrash().observe(this, list -> adapter.setEntries(list));

        adapter.setListener(new DiaryAdapter.OnItemClickListener() {
            @Override public void onItemClick(DiaryEntry e) { }

            @Override public void onItemLongClick(DiaryEntry e) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(TrashActivity.this)
                        .setTitle(R.string.dialog_restore_title)
                        .setMessage(R.string.dialog_restore_message)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setNeutralButton(R.string.action_delete_forever,
                                (d, w) -> vm.permanentDelete(e))
                        .setPositiveButton(R.string.action_restore,
                                (d, w) -> vm.restore(e))
                        .show();

                // Настроить фон диалога
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(
                            ContextCompat.getDrawable(TrashActivity.this, R.drawable.bg_dialog_dark)
                    );
                }

                // Цвета
                int onSurface = ContextCompat.getColor(TrashActivity.this, R.color.white);
                int errorColor = ContextCompat.getColor(TrashActivity.this, R.color.red_700);

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(onSurface);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setTextColor(errorColor);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(onSurface);

                TextView title   = dialog.findViewById(android.R.id.title);
                TextView message = dialog.findViewById(android.R.id.message);
                if (title   != null) title.setTextColor(onSurface);
                if (message != null) message.setTextColor(onSurface);
            }
        });
    }
}
