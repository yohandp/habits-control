package utfpr.edu.br.controlehabitos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import utfpr.edu.br.controlehabitos.modelo.TipoRecipiente;
import utfpr.edu.br.controlehabitos.modelo.Usuario;
import utfpr.edu.br.controlehabitos.persistencia.UsuarioDatabase;
import utfpr.edu.br.controlehabitos.utils.UtilsDate;

public class Cadastro extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String MODO = "MODO", NOME = "NOME", ALTURA = "ALTURA", PESO = "PESO", IMC = "IMC", FREQ = "FREQ", ID = "0";
    public static final int    NOVO    = 1, ALTERAR = 2;
    private TipoRecipiente tipoRecipiente;
    static private EditText nome, altura, peso, imc;
    static private TextView dataNasc;
    private Calendar calDataNasc;
    private int modo;
    private Usuario usuario;
    private RadioGroup rb;
    private CheckBox cb;
    private boolean imcCalculado = false;


    public static void criarUsuario(AppCompatActivity activity, int requestCode){
        Intent data = new Intent(activity, Cadastro.class);
        data.putExtra(MODO, NOVO);
        activity.startActivityForResult(data, requestCode);
    }

    public static void alterarUsuario(AppCompatActivity activity, int requestCode,  Usuario usuario){

        Intent data = new Intent(activity, Cadastro.class);
        data.putExtra(MODO, ALTERAR);
        data.putExtra(ID, usuario.getId());
        data.putExtra(NOME, usuario.getNome());
        data.putExtra(IMC, usuario.getImc());
        data.putExtra(ALTURA, usuario.getAltura());
        data.putExtra(PESO, usuario.getPeso());
        data.putExtra(FREQ, usuario.getFreqEx());

        activity.startActivityForResult(data, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent data = getIntent();
        Bundle bundle = data.getExtras();

        if (bundle != null){
            modo = bundle.getInt(MODO, NOVO);

            calDataNasc = Calendar.getInstance();

            nome = findViewById(R.id.nome);
            altura = findViewById(R.id.altura);
            peso = findViewById(R.id.peso);
            imc = findViewById(R.id.imc);
            rb = findViewById(R.id.rbG);
            cb = findViewById(R.id.checkBox2);
            dataNasc = findViewById(R.id.editDataNasc);
            dataNasc.setFocusable(false);
            dataNasc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog picker = new DatePickerDialog(Cadastro.this,
                            Cadastro.this,
                            calDataNasc.get(Calendar.YEAR),
                            calDataNasc.get(Calendar.MONTH),
                            calDataNasc.get(Calendar.DAY_OF_MONTH));
                    picker.show();
                }
            });

            dataNasc.setText(UtilsDate.formatDate(calDataNasc.getTime()));

            if (modo == ALTERAR){
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = bundle.getInt(ID);

                        UsuarioDatabase database = UsuarioDatabase.getDatabase(Cadastro.this);
                        usuario = database.usuarioDAO().queryForId(id);
                        Cadastro.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nome.setText(usuario.getNome());
                                altura.setText(String.valueOf(usuario.getAltura()));
                                peso.setText(String.valueOf(usuario.getPeso()));
                                imc.setText(String.valueOf(usuario.getImc()));
                                calDataNasc.setTime(usuario.getDataNasc());
                                String textoData = UtilsDate.formatDate(Cadastro.this,
                                        usuario.getDataNasc());
                                dataNasc.setText(textoData);

                                rb.check(usuario.getFreqEx());
                                imcCalculado = true;
                            }
                        });
                    }
                });
            }
        }
        startSupportActionMode(mActionModeCallback);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            getMenuInflater().inflate(R.menu.cadastro_opcoes, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.menuItemCriar:
                    salvar();
                    break;
                case R.id.menuItemCancelar:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            finish();
        }
    };

    public void imcCalc(View view){
        String checkAltura = altura.getText().toString(),
                checkPeso = peso.getText().toString();
        if(!(checkAltura==null || checkAltura.trim().isEmpty()) && !(checkPeso==null || checkPeso.trim().isEmpty())){
            float pesoF = Float.valueOf((peso.getText().toString()));
            float alturaF = Float.valueOf((altura.getText().toString()));
            float imcF = pesoF/(alturaF*alturaF);
            DecimalFormat dc =  new DecimalFormat("0.00");
            String imcS = Float.toString(pesoF/(alturaF*alturaF));
            imc.setText(imcS);
            if(imcF <18.5f){
                Toast.makeText(this,
                        dc.format(imcF)+ getString(R.string.imc_baixo),
                        Toast.LENGTH_LONG).show();
            }else if(imcF >=18.5f && imcF < 25){
                Toast.makeText(this,
                        dc.format(imcF)+ getString(R.string.imc_ideal),
                        Toast.LENGTH_LONG).show();
            }else if(imcF >=25 && imcF < 30){
                Toast.makeText(this,
                        dc.format(imcF)+ getString(R.string.imc_acimaPeso),
                        Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,
                        dc.format(imcF)+ getString(R.string.imc_acimaPeso2),
                        Toast.LENGTH_LONG).show();
            }
            imcCalculado = true;
        }

    }

    public void salvar(){
        String checkNome = nome.getText().toString(),
                checkAltura = altura.getText().toString(),
                checkPeso = peso.getText().toString(),
                checkIMC= imc.getText().toString();
        if(checkNome == null || checkNome.trim().isEmpty()){
            Toast.makeText(this,
                    getString(R.string.preencha_nome),
                    Toast.LENGTH_SHORT).show();
            nome.requestFocus();
            return;
        }else if(checkAltura == null || checkAltura.trim().isEmpty()){
            Toast.makeText(this,
                    getString(R.string.preencha_altura),
                    Toast.LENGTH_SHORT).show();
            altura.requestFocus();
            return;
        }else if(checkPeso == null || checkPeso.trim().isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.preencha_peso),
                    Toast.LENGTH_SHORT).show();
            peso.requestFocus();
            return;
        }else if(checkIMC == null || checkIMC.trim().isEmpty()){
            Toast.makeText(this,
                    getString(R.string.imc_blank),
                    Toast.LENGTH_SHORT).show();
            imc.requestFocus();
            return;
        }else if(rb.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this,
                    getString(R.string.notific_blank),
                    Toast.LENGTH_SHORT).show();
            rb.requestFocus();
            return;
        }else if(cb.isChecked()==false){
            Toast.makeText(this,
                    getString(R.string.dados_naoVeridicos),
                    Toast.LENGTH_SHORT).show();
            cb.requestFocus();
            return;
        }else{
            DecimalFormat dc =  new DecimalFormat("0.00");
            if(imcCalculado == false){
                Toast.makeText(this,
                        getString(R.string.imc_naoCalculado),
                        Toast.LENGTH_LONG).show();
            }else {
                int opR = rb.getCheckedRadioButtonId();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        UsuarioDatabase database = UsuarioDatabase.getDatabase(Cadastro.this);
                        float litros = 0.035f*Float.parseFloat(checkPeso);

                        if(modo == NOVO){
                            long chave = database.tipoRecDAO().firstEl();

                            usuario = new Usuario(checkNome, Float.parseFloat(checkAltura), Float.parseFloat(checkPeso), Float.parseFloat(checkIMC), opR, calDataNasc.getTime());
                            usuario.setLitrosRestantes(litros);

                            usuario.setIdUltRec((int) chave);
                            usuario.setDataCad(new Date());

                            database.usuarioDAO().insert(usuario);
                        }else{
                            usuario.setNome(checkNome);
                            usuario.setAltura(Float.parseFloat(checkAltura));
                            usuario.setPeso(Float.parseFloat(checkPeso));
                            usuario.setImc(Float.parseFloat(checkIMC));
                            usuario.setFreqEx(opR);
                            usuario.setLitrosRestantes(litros);
                            usuario.setDataNasc(calDataNasc.getTime());

                            database.usuarioDAO().update(usuario);
                        }

                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
            }
        }

    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calDataNasc.set(year, month, dayOfMonth);
        String textoData = UtilsDate.formatDate(this, calDataNasc.getTime());
        dataNasc.setText(textoData);
    }
}