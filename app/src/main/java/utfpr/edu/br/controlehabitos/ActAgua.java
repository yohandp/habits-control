package utfpr.edu.br.controlehabitos;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import utfpr.edu.br.controlehabitos.modelo.TipoRecipiente;
import utfpr.edu.br.controlehabitos.modelo.Usuario;
import utfpr.edu.br.controlehabitos.persistencia.UsuarioDatabase;
import utfpr.edu.br.controlehabitos.utils.UtilsDate;

public class ActAgua extends AppCompatActivity {

    public static final String ID = "0";
    private TipoRecipiente tipoRecipiente;
    private List<TipoRecipiente> listaRec;
    private Usuario usuario;
    static private TextView daily, remaining, editDate, alturaUsr, pesoUsr, imcUsr, lastDrinkUsr;
    private UsuarioDatabase database;
    private Spinner spinner;
    TimePickerDialog timepicker;
    int id;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.agua_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.update:
                    update();
                    return true;

                case R.id.reset:
                    reset();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agua);
        startSupportActionMode(mActionModeCallback);

        spinner = findViewById(R.id.spinner);
        carregaSpinner();

        Intent data = getIntent();
        Bundle bundle = data.getExtras();

        if (bundle != null) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    id = bundle.getInt(ID);
                    daily = findViewById(R.id.daily);
                    remaining = findViewById(R.id.remaining);
                    editDate = findViewById(R.id.editTextDate);
                    alturaUsr = findViewById(R.id.alturaUsr);
                    pesoUsr = findViewById(R.id.pesoUsr);
                    imcUsr = findViewById(R.id.imcUsr);
                    lastDrinkUsr = findViewById(R.id.lastDrinkUsr);

                    database = UsuarioDatabase.getDatabase(ActAgua.this);
                    tipoRecipiente = database.tipoRecDAO().queryForId(id);
                    usuario = database.usuarioDAO().queryForId(id);

                    ActAgua.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            daily.setText(String.valueOf(usuario.getPeso() * 0.035f));
                            remaining.setText(String.valueOf(usuario.getLitrosRestantes()));
                            alturaUsr.setText(String.valueOf(usuario.getAltura()));
                            pesoUsr.setText(String.valueOf(usuario.getPeso()));
                            DecimalFormat df = new DecimalFormat("0.00");
                            imcUsr.setText(String.valueOf(df.format(usuario.getImc())));
                            if(usuario.getDataAg() != null)
                                lastDrinkUsr.setText(UtilsDate.formatHour(usuario.getDataAg()));

                            Calendar time = Calendar.getInstance();
                            int hours = time.get(Calendar.HOUR_OF_DAY);
                            int minutes = time.get(Calendar.MINUTE);

                            /*
                            if(hours<10) {
                                if (minutes < 10)
                                    editDate.setText("0" + hours + ":0" + minutes);
                                else
                                    editDate.setText("0" + hours + ":" + minutes);
                            }else {
                                if (minutes < 10)
                                    editDate.setText("" + hours + ":0" + minutes);
                                else
                                    editDate.setText(String.valueOf(hours) + ":" + minutes);
                            }
                            */

                            editDate.setText(UtilsDate.formatHour(time.getTime()));

                            int posicao = posicaoTipo(usuario.getIdUltRec());
                            spinner.setSelection(posicao);
                            editDate.setFocusable(false);
                            editDate.setOnClickListener(new View.OnClickListener(){

                                @Override
                                public void onClick(View view){
                                    timepicker = new TimePickerDialog(ActAgua.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            Calendar time2 = Calendar.getInstance();
                                            time2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            time2.set(Calendar.MINUTE, minute);
                                            editDate.setText(UtilsDate.formatHour(time2.getTime()));
                                        }
                                    }, hours, minutes, true);
                                    timepicker.show();
                                }
                            });
                        }
                    });
                }
            });


        }
    }

    private void carregaSpinner(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                UsuarioDatabase database = UsuarioDatabase.getDatabase(ActAgua.this);
                listaRec = database.tipoRecDAO().queryAll();
                ActAgua.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<TipoRecipiente> spinnerAdapter = new ArrayAdapter<>(ActAgua.this, android.R.layout.simple_list_item_1, listaRec);
                        spinner.setAdapter(spinnerAdapter);
                    }
                });
            }
        });
    }

    private int posicaoTipo(int tipoId){
        for (int pos = 0; pos < listaRec.size(); pos++){
            TipoRecipiente t = listaRec.get(pos);
            if (t.getId() == tipoId){
                return pos;
            }
        }
        return -1;
    }

    public static void actAgua(AppCompatActivity activity,  Usuario usuario){
        Intent data = new Intent(activity, ActAgua.class);
        data.putExtra(ID, usuario.getId());
        activity.startActivity(data);
    }

    private void update(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TipoRecipiente tipo = (TipoRecipiente) spinner.getSelectedItem();
                if(tipo.getMl() != 0) {
                    float restante = ((usuario.getLitrosRestantes() * 1000) - tipo.getMl()) / 1000;
                    usuario.setLitrosRestantes(restante);
                    usuario.setIdUltRec(tipo.getId());

                    String data = editDate.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    try {
                        Date date = format.parse(data);
                        usuario.setDataAg(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    database.usuarioDAO().update(usuario);
                }else{
                    finish();
                }
            }
        });
        remaining.setText(String.valueOf(usuario.getLitrosRestantes()));
        setResult(Activity.RESULT_OK);
        Main.reloadNedeed = true;
        finish();
    }

    private void reset(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                usuario.setLitrosRestantes(usuario.getPeso() * 0.035f);
                database.usuarioDAO().update(usuario);
            }
        });
        remaining.setText(String.valueOf(usuario.getPeso() * 0.035f));
        setResult(Activity.RESULT_OK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            carregaSpinner();
        }
    }



}
