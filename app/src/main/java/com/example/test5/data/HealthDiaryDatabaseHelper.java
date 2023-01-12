// Субкласс SQLiteOpenHelper, определяющий базу данных приложения
package com.example.test5.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.test5.data.DatabaseDescription.Contact;

public class HealthDiaryDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthDiary7.db";
    private static final int DATABASE_VERSION = 1;

    // Конструктор
    public HealthDiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблицы contacts при создании базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Команда SQL для создания таблицы contacts
        final String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + Contact.TABLE_NAME + "(" +
                        Contact._ID + " integer primary key, " +
                        Contact.COLUMN_DATE + " TEXT, " +
                        Contact.COLUMN_WEIGHT + " TEXT, " +
                        Contact.COLUMN_HEIGHT + " TEXT, " +
                        Contact.COLUMN_PRESSURE + " TEXT, " +
                        Contact.COLUMN_PULSE + " TEXT, " +
                        Contact.COLUMN_SUGAR + " TEXT, " +
                        Contact.COLUMN_CHOLESTEROL + " TEXT, " +
                        Contact.COLUMN_DLC + " TEXT, " +
                        Contact.COLUMN_CHECK + " TEXT);";
        db.execSQL(CREATE_CONTACTS_TABLE); // Создание таблицы contacts
    }

    // Обычно определяет способ обновления при изменении схемы базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
    }
}
