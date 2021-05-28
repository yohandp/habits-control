package utfpr.edu.br.controlehabitos;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import utfpr.edu.br.controlehabitos.modelo.TipoRecipiente;
import utfpr.edu.br.controlehabitos.persistencia.TipoRecDAO;
import utfpr.edu.br.controlehabitos.persistencia.UsuarioDatabase;

public class CriaRec extends AppCompatActivity {

    public static final String MODO = "MODO", ID = "0";
    public static final int    NOVO    = 1, ALTERAR = 2;
    static private EditText nome, ml;
    private int modo;
    private TipoRecipiente tipoRecipiente;
    private TipoRecDAO tipoRecDAO;

    public static void criarRec(AppCompatActivity activity, int requestCode){
        Intent data = new Intent(activity, CriaRec.class);
        data.putExtra(MODO, NOVO);
        activity.startActivityForResult(data, requestCode);
    }

    public static void alterarRec(AppCompatActivity activity, int requestCode,  TipoRecipiente tipoRecipiente){
        Intent data = new Intent(activity, CriaRec.class);
        data.putExtra(MODO, ALTERAR);
        data.putExtra(ID, tipoRecipiente.getId());

        activity.startActivityForResult(data, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        nome = findViewById(R.id.nome);
        ml = findViewById(R.id.ml);

        Intent data = getIntent();
        Bundle bundle = data.getExtras();

        if (bundle != null){
            modo = bundle.getInt(MODO, NOVO);
            if (modo == ALTERAR){
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = bundle.getInt(ID);
                        UsuarioDatabase database = UsuarioDatabase.getDatabase(CriaRec.this);
                        tipoRecipiente = database.tipoRecDAO().queryForId(id);
                        nome.setText(tipoRecipiente.getRecipiente());
                        ml.setText(String.valueOf(tipoRecipiente.getMl()));
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

    public void salvar(){
        String checkNome = nome.getText().toString(), checkMl = ml.getText().toString();

        if(checkNome == null || checkNome.trim().isEmpty()){
            Toast.makeText(this,
                    getString(R.string.preencha_nome),
                    Toast.LENGTH_SHORT).show();
            nome.requestFocus();
            return;
        }else if(checkMl == null || checkMl.trim().isEmpty()){
            Toast.makeText(this,
                    getString(R.string.preencha_ml),
                    Toast.LENGTH_SHORT).show();
            ml.requestFocus();
            return;
        }else{
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    UsuarioDatabase database = UsuarioDatabase.getDatabase(CriaRec.this);
                    if(modo == NOVO){
                        tipoRecipiente = new TipoRecipiente(checkNome, Integer.parseInt(checkMl));

                        database.tipoRecDAO().insert(tipoRecipiente);
                    }else{
                        tipoRecipiente.setRecipiente(checkNome);
                        tipoRecipiente.setMl(Integer.parseInt(checkMl));

                        database.tipoRecDAO().update(tipoRecipiente);
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
            }
        }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }


}
