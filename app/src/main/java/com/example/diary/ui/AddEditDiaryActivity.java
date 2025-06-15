package com.example.diary.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diary.R;
import com.example.diary.data.DiaryEntry;
import com.example.diary.databinding.ActivityAddEditBinding;

import java.util.Arrays;
import java.util.List;

public class AddEditDiaryActivity extends AppCompatActivity {
    private ActivityAddEditBinding binding;
    private DiaryViewModel vm;
    private int entryId = -1;
    private List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Инициализируем ViewModel
        vm = new ViewModelProvider(this).get(DiaryViewModel.class);

        // 2. Подготовка списка категорий (массив в res/values/arrays.xml должен начинаться с "Все")
        categories = Arrays.asList(getResources().getStringArray(R.array.categories));
        Spinner spinner = binding.spinnerCategory;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // по-умолчанию — "Все"
        spinner.setSelection(0);

        // 3. Если пришёл ID — значит редактируем, подгружаем данные
        if (getIntent().hasExtra("id")) {
            entryId = getIntent().getIntExtra("id", -1);
            vm.getById(entryId).observe(this, entry -> {
                if (entry != null) {
                    binding.editTitle.setText(entry.getTitle());
                    binding.editContent.setText(entry.getContent());
                    int pos = categories.indexOf(entry.getCategory());
                    spinner.setSelection(pos >= 0 ? pos : 0);
                }
            });
        }

        // 4. Нажатие кнопки «Сохранить»
        binding.btnSave.setOnClickListener(v -> {
            String title    = binding.editTitle.getText().toString().trim();
            String content  = binding.editContent.getText().toString().trim();
            String category = categories.get(spinner.getSelectedItemPosition());

            if (title.isEmpty()) {
                binding.editTitle.setError(getString(R.string.error_empty_title));
                return;
            }

            // берем текущее время как метку
            long timestamp = System.currentTimeMillis();
            DiaryEntry entry = new DiaryEntry(title, content, category, timestamp);

            if (entryId >= 0) {
                // обновляем
                entry.setId(entryId);
                vm.update(entry);
            } else {
                // вставляем новую
                vm.insert(entry);
            }
            finish();
        });
    }
}
