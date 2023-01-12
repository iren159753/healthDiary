package com.example.test5;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity
        implements ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener,
        Serializable{

    // Ключ для сохранения Uri контакта в переданном объекте Bundle
    public static final String CONTACT_URI = "contact_uri";
    private ContactsFragment contactsFragment; // Вывод списка записей

    // Отображает ContactsFragment при первой загрузке MainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        // Если макет содержит fragmentContainer, используется макет для
        // телефона; отобразить ContactsFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {
            // Создание ContactsFragment
            contactsFragment = new ContactsFragment();

            // Добавление фрагмента в FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, contactsFragment);
            transaction.commit(); // Вывод ContactsFragment
        } else {
            contactsFragment =
                    (ContactsFragment) getSupportFragmentManager().
                            findFragmentById(R.id.contactsFragment);
        }
    }

    // Отображение DetailFragment для выбранной записи
    @Override
    public void onContactSelected(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayContact(contactUri, R.id.fragmentContainer);
        else { // Планшет
            // Извлечение с вершины стека возврата
            getSupportFragmentManager().popBackStack();

            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    // Отображение AddEditFragment для добавления нового контакта
    @Override
    public void onAddContact() {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // Планшет
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // Отображение информации о записи
    private void displayContact(Uri contactUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // Передача URI контакта в аргументе DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(CONTACT_URI, contactUri);
        detailFragment.setArguments(arguments);

        // Использование FragmentTransaction для отображения
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению DetailFragment
    }

    // Отображение фрагмента для добавления или изменения записи
    private void displayAddEditFragment(int viewID, Uri contactUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // При изменении передается аргумент contactUri
        if (contactUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CONTACT_URI, contactUri);
            addEditFragment.setArguments(arguments);
        }

        // Использование FragmentTransaction для отображения AddEditFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению AddEditFragment
    }

    // Возвращение к списку записей при удалении текущей записи
    @Override
    public void onContactDeleted() {
        // Удаление с вершины стека
        getSupportFragmentManager().popBackStack();
        contactsFragment.updateContactList(); // Обновление записей
    }

    // Отображение AddEditFragment для изменения существующей записи
    @Override
    public void onEditContact(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
        else // Планшет
            displayAddEditFragment(R.id.rightPaneContainer, contactUri);
    }

    // Обновление GUI после сохранения новой или существующей записи
    @Override
    public void onAddEditCompleted(Uri contactUri) {
        // Удаление вершины стека возврата
        getSupportFragmentManager().popBackStack();
        contactsFragment.updateContactList(); // Обновление записей

        if (findViewById(R.id.fragmentContainer) == null) { // Планшет
            // Удаление с вершины стека возврата
            getSupportFragmentManager().popBackStack();

            // На планшете выводится добавленная или измененная запись
            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

}