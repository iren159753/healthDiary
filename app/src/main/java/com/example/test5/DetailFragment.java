package com.example.test5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.test5.data.DatabaseDescription.Contact;

import org.w3c.dom.Text;

import java.io.Serializable;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    // Методы обратного вызова, реализованные MainActivity
    public interface DetailFragmentListener {
        void onContactDeleted(); // Вызывается при удалении записи

        // Передает URI редактируемого контакта DetailFragmentListener
        void onEditContact(Uri contactUri);
    }

    private static final int CONTACT_LOADER = 0; // Идентифицирует Loader

    private DetailFragmentListener listener; // MainActivity
    private Uri contactUri; // Uri выбранной записи

    private TextView dateTextView; // Дата
    private TextView weightTextView; // Вес
    private TextView heightTextView; // Рост
    private TextView pressureTextView; // Давление
    private TextView pulseTextView; // Пульс
    private TextView sugarTextView; // Сахар
    private TextView cholesterolTextView; // Холестерин
    private TextView dlc;
    private TextView dlcInfo;
    private TextView checkInfo;

    private TextView answer; //ИМТ(Индекс Массы Тела)
    private TextView answer_pressure;// Давление
    private TextView answer_pulse; // Пульс
    private TextView answer_sugar;//Сахар
    private TextView answer_cholesterol;//Холестерин


    // Назначение DetailFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    // Удаление DetailFragmentListener при отсоединении фрагмента
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

        // Получение объекта Bundle с аргументами и извлечение URI
        Bundle arguments = getArguments();

        if (arguments != null)
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);

        // Заполнение макета DetailFragment
        View view =
                inflater.inflate(R.layout.fragment_details, container, false);

        // Получение компонентов EditTexts
        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        weightTextView = (TextView) view.findViewById(R.id.weightTextView);
        heightTextView = (TextView) view.findViewById(R.id.heightTextView);
        pressureTextView = (TextView) view.findViewById(R.id.pressureTextView);
        pulseTextView = (TextView) view.findViewById(R.id.pulseTextView);
        sugarTextView = (TextView) view.findViewById(R.id.sugarTextView);
        cholesterolTextView = (TextView) view.findViewById(R.id.cholesterollTextView);
        dlc = (TextView) view.findViewById(R.id.dlc);
        dlcInfo = (TextView) view.findViewById(R.id.labelInfo);
        checkInfo = (TextView) view.findViewById(R.id.check_list);

        answer = (TextView) view.findViewById(R.id.answer);
        answer_pressure=(TextView) view.findViewById(R.id.answer_pressure);
        answer_pulse=(TextView) view.findViewById(R.id.answer_pulse);
        answer_sugar=(TextView) view.findViewById(R.id.answer_sugar);
        answer_cholesterol=(TextView) view.findViewById(R.id.answer_cholesterol);

        // Загрузка контакта
        LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        return view;
    }

    // Отображение команд меню фрагмента
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // Обработка выбора команд меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                listener.onEditContact(contactUri); // Передача Uri слушателю
                return true;
            case R.id.action_delete:
                deleteContact(); // метод описан ниже
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ConfirmDialogFragment extends DialogFragment {

        public static ConfirmDialogFragment newInstance(Uri uri, Context context) {
            ConfirmDialogFragment frag = new ConfirmDialogFragment();
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.CONTACT_URI,uri);
            args.putSerializable("listener",(Serializable)context);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Uri uri=getArguments().getParcelable(MainActivity.CONTACT_URI);
            final DetailFragmentListener listener =
                    (DetailFragmentListener)getArguments().getSerializable("listener");
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.confirm_title)
                    .setMessage(R.string.confirm_message)
                    .setPositiveButton(R.string.button_delete,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int button) {
                                    // объект ContentResolver используется
                                    // для вызова delete в AddressBookContentProvider
                                    getActivity().getContentResolver().delete(
                                            uri, null, null);
                                    listener.onContactDeleted(); // Оповещение слушателя
                                }
                            }
                    )
                    .setNegativeButton(R.string.button_cancel,null)
                    .create();
        }
    }

    // DialogFragment для подтверждения удаления записи
    private DialogFragment confirmDelete;

    // Удаление записи
    private void deleteContact() {
        // FragmentManager используется для отображения confirmDelete
        confirmDelete=ConfirmDialogFragment.newInstance(contactUri, (Context)listener);
        confirmDelete.show(getParentFragmentManager(), "confirm delete");
    }

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        CursorLoader cursorLoader;

        switch (id) {
            case CONTACT_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        contactUri, // Uri отображаемой записи
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    // Вызывается LoaderManager при завершении загрузки
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Если запись существует в базе данных, вывести её информацию
        if (data != null && data.moveToFirst()) {
            // Получение индекса столбца для каждого элемента данных
            int dateIndex = data.getColumnIndex(Contact.COLUMN_DATE);
            int weightIndex = data.getColumnIndex(Contact.COLUMN_WEIGHT);
            int heightIndex = data.getColumnIndex(Contact.COLUMN_HEIGHT);
            int pressureIndex = data.getColumnIndex(Contact.COLUMN_PRESSURE);
            int pulseIndex = data.getColumnIndex(Contact.COLUMN_PULSE);
            int sugarIndex = data.getColumnIndex(Contact.COLUMN_SUGAR);
            int cholesterolIndex = data.getColumnIndex(Contact.COLUMN_CHOLESTEROL);
            int dlcIndex = data.getColumnIndex(Contact.COLUMN_DLC);
            int checkIndex = data.getColumnIndex(Contact.COLUMN_CHECK);

            // Заполнение TextView полученными данными
            dateTextView.setText(data.getString(dateIndex));
            weightTextView.setText(data.getString(weightIndex));
            heightTextView.setText(data.getString(heightIndex));
            pressureTextView.setText(data.getString(pressureIndex));
            pulseTextView.setText(data.getString(pulseIndex));
            sugarTextView.setText(data.getString(sugarIndex));
            cholesterolTextView.setText(data.getString(cholesterolIndex));
            dlc.setText(data.getString(dlcIndex));
            checkInfo.setText(data.getString(checkIndex));

            String answer2 ="";
            float answer1 = 0;

            //Подсчет ИМТ

            if(weightTextView.getText().toString().matches("") || heightTextView.getText().toString().matches(""))
            answer.setText("Не достаточно данных");
            else
            {
                float weight = Float.parseFloat(weightTextView.getText().toString());
                float height = Float.parseFloat(heightTextView.getText().toString()) / 100;
                answer1 = weight / (height * height);
                if(answer1>18 && answer1<25) answer2="Норма";
                else if(answer1<18) answer2="Дефицит\n массы тела";
                else answer2 ="Избыточная\n масса тела";
                answer.setText(Float.toString(answer1)+" ("+answer2+")");
            }

            //Показатели давления
            if(pressureTextView.getText().toString().matches("")) answer_pressure.setText("-");
            else
            {
                String systolic = "", diastolic = "";
                String pressure = pressureTextView.getText().toString();
                int index = pressure.indexOf("/");
                for(int i = 0; i < index; i++)
                {
                    systolic += pressure.charAt(i);
                }
                for(int i = index+1; i < pressure.length(); i++)
                {
                    diastolic +=pressure.charAt(i);
                }
                if( systolic.matches("120") && diastolic.matches("80")) answer_pressure.setText("Норма");
                else answer_pressure.setText("Не норма");

            }


            //Показатели пульса
            if(pulseTextView.getText().toString().matches("")) answer_pulse.setText("-");
            else {
                float pulse = Float.parseFloat(pulseTextView.getText().toString());
                if (pulse >= 60 && pulse <= 80) answer_pulse.setText("Норма");
                else answer_pulse.setText("Не норма");
            }

            //Показатели сахара
            if(sugarTextView.getText().toString().matches("")) answer_sugar.setText("-");
            else {
                float sugar = Float.parseFloat(sugarTextView.getText().toString());
                if (sugar >= 4.1 && sugar <= 5.9) answer_sugar.setText("Норма");
                else answer_sugar.setText("Не норма");
            }

            //Показатели холестерина
            if(cholesterolTextView.getText().toString().matches("")) answer_cholesterol.setText("-");
            else{
                float cholesterol = Float.parseFloat(cholesterolTextView.getText().toString());
                if (cholesterol >= 3.0 && cholesterol <= 6.0) answer_cholesterol.setText("Норма");
                else answer_cholesterol.setText("Не норма");
            }

            // Доп инфо
            if(dlc.getText().toString().matches(""))
            {
                dlcInfo.setVisibility(View.INVISIBLE);
                dlc.setVisibility(View.INVISIBLE);
            }

        }
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
} // окончание класса
