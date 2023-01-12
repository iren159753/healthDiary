// Класс описывает имя таблицы и имена столбцов базы данных, а также
// содержит другую информацию, необходимую для ContentProvider
package com.example.test5.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    // Имя ContentProvider: обычно совпадает с именем пакета
    public static final String AUTHORITY =
            "com.example.test5.data";

    // Базовый URI для взаимодействия с ContentProvider
    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    // Вложенный класс, определяющий содержимое таблицы contacts
    public static final class Contact implements BaseColumns {
        public static final String TABLE_NAME = "contacts"; // Имя таблицы
        // Объект Uri для таблицы contacts
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // Имена столбцов таблицы
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_PULSE = "pulse";
        public static final String COLUMN_SUGAR = "sugar";
        public static final String COLUMN_CHOLESTEROL = "cholesterol";
        public static final String COLUMN_DLC = "dlcInfo";
        public static  final String COLUMN_CHECK = "checkList";

        // Создание Uri для конкретного контакта
        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
