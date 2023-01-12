package com.example.test5;

import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test5.data.DatabaseDescription.Contact;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class ContactsAdapter  extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    // Интерфейс реализуется ContactsFragment для обработки
    // прикосновения к элементу в списке RecyclerView
    public interface ContactClickListener {
        void onClick(Uri contactUri);
    }

    // Вложенный субкласс RecyclerView.ViewHolder используется
    // для реализации паттерна View–Holder в контексте RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        private long rowID;



        // Настройка объекта ViewHolder элемента RecyclerView
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);

            // Присоединение слушателя к itemView
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        // Выполняется при щелчке на контакте в ViewHolder
                        @Override
                        public void onClick(View view) {
                            clickListener.onClick(Contact.buildContactUri(rowID));
                        }
                    }
            );
        }

        // Идентификатор записи базы данных для контакта в ViewHolder
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }
    }

    // Переменные экземпляров ContactsAdapter
    private Cursor cursor = null;
    private final ContactClickListener clickListener;

    // Конструктор
    public ContactsAdapter(ContactClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Подготовка нового элемента списка и его объекта ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Заполнение макета android.R.layout.simple_list_item_1
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view); // ViewHolder текущего элемента
    }

    // Назначает текст элемента списка
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        holder.setRowID(cursor.getLong(cursor.getColumnIndex(Contact._ID)));
        holder.textView.setText(cursor.getString(cursor.getColumnIndex(
          Contact.COLUMN_DATE)));

    }

    // Возвращает количество элементов, предоставляемых адаптером
    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // Текущий объект Cursor адаптера заменяется новым
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
