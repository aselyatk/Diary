package com.example.diary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.R;
import com.example.diary.data.DiaryEntry;
import com.example.diary.databinding.ActivityMainBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DiaryViewModel vm;
    private DiaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);

        // Настройка RecyclerView и ViewModel
        adapter = new DiaryAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        vm = new ViewModelProvider(this).get(DiaryViewModel.class);
        vm.getAllEntries().observe(this, entries -> adapter.setEntries(entries));

        // Spinner для фильтра по категориям
        Spinner filter = binding.spinnerFilter;
        List<String> cats = Arrays.asList(getResources().getStringArray(R.array.categories));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, cats
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(spinnerAdapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String cat = cats.get(pos);
                if ("Все".equals(cat)) {
                    vm.getAllEntries().observe(MainActivity.this, list -> adapter.setEntries(list));
                } else {
                    vm.filterByCategory(cat).observe(MainActivity.this, list -> adapter.setEntries(list));
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Swipe-to-delete с откатом
        ItemTouchHelper.SimpleCallback swipe = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                DiaryEntry deleted = adapter.getEntries().get(pos);
                vm.softDelete(deleted);
                Snackbar.make(binding.getRoot(),
                                getString(R.string.msg_deleted),
                                Snackbar.LENGTH_LONG)
                        .setAction(R.string.action_undo, v -> vm.insert(deleted))
                        .show();
            }
        };
        new ItemTouchHelper(swipe).attachToRecyclerView(binding.recyclerView);

        // Клики по заметкам
        adapter.setListener(new DiaryAdapter.OnItemClickListener() {
            @Override public void onItemClick(DiaryEntry e) {
                Intent i = new Intent(MainActivity.this, AddEditDiaryActivity.class);
                i.putExtra("id", e.getId());
                startActivity(i);
            }
            @Override public void onItemLongClick(DiaryEntry e) {
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, (d, w) -> vm.softDelete(e))
                        .show();
            }
        });

        // FAB для создания новой заметки
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditDiaryActivity.class))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.menu_main, menu);

        SearchView sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        sv.setQueryHint(getString(R.string.action_search));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; }
            @Override public boolean onQueryTextChange(String q) {
                vm.search(q).observe(MainActivity.this, list -> adapter.setEntries(list));
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter_date) {
            // Используем MaterialDatePicker
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.action_filter_date))
                    .setCalendarConstraints(
                            new CalendarConstraints.Builder()
                                    .setValidator(DateValidatorPointBackward.now())
                                    .build()
                    )
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date(selection));
                vm.filterByDate(dateStr)
                        .observe(MainActivity.this, list -> adapter.setEntries(list));
            });
            picker.show(getSupportFragmentManager(), "DATE_PICKER");
            return true;
        }
        else if (id == R.id.action_trash) {
            startActivity(new Intent(this, TrashActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
