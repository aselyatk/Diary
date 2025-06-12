package com.example.diary.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diary.R;
import com.example.diary.data.DiaryEntry;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private List<DiaryEntry> entries;
    private OnItemClickListener listener;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(DiaryEntry entry);
        void onItemLongClick(DiaryEntry entry);
    }
    public void setListener(OnItemClickListener l) { this.listener = l; }

    public void setEntries(List<DiaryEntry> list) {
        entries = list;
        notifyDataSetChanged();
    }
    /** Возвращает текущий список заметок в адаптере */
    public List<DiaryEntry> getEntries() {
        return entries;
    }

    @NonNull
    @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View vew = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_diary_entry, p, false);
        return new ViewHolder(vew);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        DiaryEntry e = entries.get(pos);
        h.textTitle.setText(e.getTitle());
        h.textDate.setText(sdf.format(e.getTimestamp()));
    }
    @Override public int getItemCount() { return entries == null ? 0 : entries.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDate;
        ViewHolder(View item) {
            super(item);
            textTitle = item.findViewById(R.id.textTitle);
            textDate  = item.findViewById(R.id.textDate);
            item.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(entries.get(getAdapterPosition()));
            });
            item.setOnLongClickListener(v -> {
                if (listener != null) listener.onItemLongClick(entries.get(getAdapterPosition()));
                return true;
            });
        }
    }
}