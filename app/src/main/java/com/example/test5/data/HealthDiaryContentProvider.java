package com.example.test5.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.test5.R;
import com.example.test5.data.DatabaseDescription.Contact;

public class HealthDiaryContentProvider extends ContentProvider {
    // Используется для обращения к базе данных
    private HealthDiaryDatabaseHelper dbHelper;

    // UriMatcher помогает ContentProvider определить выполняемую операцию
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    // Константы, используемые для определения выполняемой операции
    private static final int ONE_CONTACT = 1; // Один контакт
    private static final int CONTACTS = 2; // Таблица контактов

    // Статический блок для настройки UriMatcher объекта ContentProvider
    static {
        // Uri для записи с заданным идентификатором
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Contact.TABLE_NAME + "/#", ONE_CONTACT);

        // Uri для таблицы
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Contact.TABLE_NAME, CONTACTS);
    }

    // Вызывается при создании AddressBookContentProvider
    @Override
    public boolean onCreate() {
        // Создание объекта AddressBookDatabaseHelper
        dbHelper = new HealthDiaryDatabaseHelper(getContext());
        return true; // Объект ContentProvider создан успешно
    }

    // Обязательный метод: здесь не используется, возвращаем null
    @Override
    public String getType(Uri uri) { return null; }

    // Получение информации из базы данных
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        // Создание SQLiteQueryBuilder для запроса к таблице contacts
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contact.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT: // Выбрать запись с заданным идентификатором
                queryBuilder.appendWhere(
                        Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case CONTACTS: // Выбрать все записи
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_query_uri) + uri);
        }

        // Выполнить запрос для получения одной или всех записей
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // Настройка отслеживания изменений в контенте
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Вставка новой записи в базу данных
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newContactUri = null;
        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                // При успехе возвращается идентификатор записи новой записи
                long rowId = dbHelper.getWritableDatabase().insert(
                        Contact.TABLE_NAME, null, values);
                // Если запись была вставлена, создать подходящий Uri;
                // в противном случае выдать исключение
                if (rowId > 0) { // идентификаторы записей начинаются с 1
                    newContactUri = Contact.buildContactUri(rowId);
                    // Оповестить наблюдателей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_insert_uri) + uri);
        }
        return newContactUri;
    }

    // Обновление существующей записи в базе данных
    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int numberOfRowsUpdated; // 1, если обновление успешно; 0 при неудаче
        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                // Получение идентификатора записи из Uri
                String id = uri.getLastPathSegment();
                // Обновление контакта
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Contact.TABLE_NAME, values, Contact._ID + "=" + id,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_update_uri) + uri);
        }
        // Если были внесены изменения, оповестить наблюдателей
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }

    // Удаление существующей записи из базы данных
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;
        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                // Получение из URI идентификатора записи
                String id = uri.getLastPathSegment();
                // Удаление записи
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Contact.TABLE_NAME, Contact._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }
        // Оповестить наблюдателей об изменениях в базе данных
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }
}