package com.example.test5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.example.test5.data.DatabaseDescription.Contact;

import org.w3c.dom.Text;

import java.lang.invoke.CallSite;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Определяет метод обратного вызова, реализованный MainActivity
    public interface AddEditFragmentListener {
        // Вызывается при сохранении записи
        void onAddEditCompleted(Uri contactUri);
    }

    // Константа для идентификации Loader
    private static final int CONTACT_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri contactUri; // Uri выбранной записи
    private boolean addingNewContact = true; // Добавление (true) или изменение


    // Компоненты EditText для информации записи
    private TextInputLayout dateTextInputLayout;
    private TextInputLayout weightTextInputLayout;
    private TextInputLayout heightTextInputLayout;
    private TextInputLayout pressureTextInputLayout;
    private TextInputLayout pulseTextInputLayout;
    private TextInputLayout sugarTextInputLayout;
    private TextInputLayout cholesterolTextInputLayout;
    private FloatingActionButton saveContactFAB;
    private TextView dlc_info;
    private CheckBox dlc_btn;
    private TextView check_list;

    private CoordinatorLayout coordinatorLayout; // Для SnackBar

    // Назначение AddEditFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }
    // Удаление AddEditFragmentListener при отсоединении фрагмента
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Вызывается при создании представлений фрагмента
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // У фрагмента есть команды меню

        // Заполнение GUI и получение ссылок на компоненты EditText
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        dateTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.dateTextInputLayout);
      //  dateTextInputLayout.getEditText().addTextChangedListener(
     //           nameChangedListener);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        dateTextInputLayout.getEditText().setText(currentDateTimeString);
        weightTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.weightTextInputLayout);
        heightTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.heightTextInputLayout);
        pressureTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.pressureTextInputLayout);
        pulseTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.pulseTextInputLayout);
        sugarTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.sugarTextInputLayout);
        cholesterolTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.cholesterolTextInputLayout);

        check_list = (TextView) view.findViewById(R.id.check_list);
        check_list.setText("Рано просыпаться:\n\n"+
                "Пить утром стакан воды:\n\n"+
                "Есть овощи и фрукты:\n\n"+
                "Короткая тренировка:\n\n"+
                "Как минимум 10000 шагов:\n\n"+
                "Медитация:\n\n"+
                "Пить 5-6 стаканов воды:\n\n"+
                "Следить за осанкой:\n\n"+
                "Чистить зубы перед сном:\n\n"+
                "Сон 7-8 часов:"
                );

        dlc_btn = (CheckBox) view.findViewById(R.id.dlc_button);
        dlc_info = (TextView) view.findViewById(R.id.dlc_information);
        dlc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dlc_btn.isChecked()){
                    dlc_info.setVisibility(View.VISIBLE);
                }
                else dlc_info.setVisibility(View.INVISIBLE);
            }
        });

        // Назначение слушателя событий FloatingActionButton
        saveContactFAB = (FloatingActionButton) view.findViewById(
                R.id.saveFloatingActionButton);
        saveContactFAB.setOnClickListener(saveContactButtonClicked);
        updateSaveButtonFAB(); // метод описан ниже

        // Используется для отображения SnackBar с короткими сообщениями
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null при создании записи

        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        // При изменении существующей записи создать Loader
        if (contactUri != null)
            LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);

        return view;
    }

    // Обнаруживает изменения в тексте поля EditTex, связанного
    // с nameTextInputLayout, для отображения или скрытия saveButtonFAB
    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        // Вызывается при изменении текста в nameTextInputLayout
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveButtonFAB(); // метод описан ниже
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // Кнопка saveButtonFAB видна, если имя не пусто
    private void updateSaveButtonFAB() {
        String input =
                dateTextInputLayout.getEditText().getText().toString();

        // Если для контакта указано имя, показать FloatingActionButton
        if (input.trim().length() != 0)
            saveContactFAB.show();
        else
            saveContactFAB.hide();
    }

    // Реагирует на событие, генерируемое при сохранении записи
    private final View.OnClickListener saveContactButtonClicked =
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    // Скрыть виртуальную клавиатуру
                    ((InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(), 0);
                    saveContact(); // Сохранение контакта в базе данных

                }

            };

    // Сохранение информации записи в базе данных
    private void saveContact() {
        // Создание объекта ContentValues с парами "ключ—значение"
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contact.COLUMN_DATE,
                dateTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_WEIGHT,
                weightTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_HEIGHT,
                heightTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_PRESSURE,
                pressureTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_PULSE,
                pulseTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_SUGAR,
                sugarTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_CHOLESTEROL,
                cholesterolTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_DLC, dlc_info.getText().toString());
        contentValues.put(Contact.COLUMN_CHECK, check_list.getText().toString() );


        if (addingNewContact) {
            // Использовать объект ContentResolver активности для вызова
            // insert для объекта AddressBookContentProvider
            Uri newContactUri = getActivity().getContentResolver().insert(
                    Contact.CONTENT_URI, contentValues);

            if (newContactUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.entry_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newContactUri);

            } else {
                Snackbar.make(coordinatorLayout,
                        R.string.entry_not_added, Snackbar.LENGTH_LONG).show();

            }

        } else {
            // Использовать объект ContentResolver активности для вызова
            // update для объекта AddressBookContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                    contactUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(contactUri);
                Snackbar.make(coordinatorLayout,
                        R.string.entry_updated, Snackbar.LENGTH_LONG).show();

            } else {
                Snackbar.make(coordinatorLayout,
                        R.string.entry_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        switch (id) {
            case CONTACT_LOADER:
                return new CursorLoader(getActivity(),
                        contactUri, // Uri отображаемого контакта
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
            default:
                return null;
        }
    }

    // Вызывается LoaderManager при завершении загрузки
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Если контакт существует в базе данных, вывести его информацию
        if (data != null && data.moveToFirst()) {
            // Получение индекса столбца для каждого элемента данных
            int dateIndex = data.getColumnIndex(Contact.COLUMN_DATE);
            int placeIndex = data.getColumnIndex(Contact.COLUMN_WEIGHT);
            int headerIndex = data.getColumnIndex(Contact.COLUMN_HEIGHT);
            int subjectIndex = data.getColumnIndex(Contact.COLUMN_PRESSURE);
            int descriptionIndex = data.getColumnIndex(Contact.COLUMN_PULSE);
            int emotionIndex = data.getColumnIndex(Contact.COLUMN_SUGAR);
            int ageIndex = data.getColumnIndex(Contact.COLUMN_CHOLESTEROL);
            int dlcIndex = data.getColumnIndex(Contact.COLUMN_DLC);
            int checkIndex = data.getColumnIndex(Contact.COLUMN_CHECK);

            // Заполнение компонентов EditText полученными данными
            dateTextInputLayout.getEditText().setText(
                    data.getString(dateIndex));
            weightTextInputLayout.getEditText().setText(
                    data.getString(placeIndex));
            heightTextInputLayout.getEditText().setText(
                    data.getString(headerIndex));
            pressureTextInputLayout.getEditText().setText(
                    data.getString(subjectIndex));
            pulseTextInputLayout.getEditText().setText(
                    data.getString(descriptionIndex));
            sugarTextInputLayout.getEditText().setText(
                    data.getString(emotionIndex));
            cholesterolTextInputLayout.getEditText().setText(
                    data.getString(ageIndex));
            dlc_info.setText(data.getString(dlcIndex));
            check_list.setText(data.getString(checkIndex));

            updateSaveButtonFAB();
        }
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
} // окончание класса
