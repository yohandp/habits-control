package utfpr.edu.br.controlehabitos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import java.util.ArrayList;
import java.util.List;

import utfpr.edu.br.controlehabitos.modelo.TipoRecipiente;
import utfpr.edu.br.controlehabitos.persistencia.UsuarioDatabase;
import utfpr.edu.br.controlehabitos.utils.UtilsGUI;

public class ListRec extends AppCompatActivity {
    public static final int ConstDados = 1;
    private int        posicaoSelecionada = -1;
    private ActionMode actionMode;
    private View viewSelecionada;

    ListView listViewTipos;
    private ArrayList<TipoRecipiente> listaTipos;
    private ArrayAdapter<TipoRecipiente> listaAdapter;
    private TipoRecipiente tipoRecipiente;

    SharedPref sharedpref;
    private Switch myswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);

        if(sharedpref.loadNightModeState()==true){
            setTheme(R.style.darktheme);
        }else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);

        myswitch = (Switch)findViewById(R.id.mySwitch);

        if(sharedpref.loadNightModeState()==true){
            myswitch.setChecked(true);
        }

        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedpref.setNightModeState(true);
                }
                else {
                    sharedpref.setNightModeState(false);
                }
                restartApp();

            }
        });

        listViewTipos = findViewById(R.id.listView);

        listViewTipos.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {

                        posicaoSelecionada = position;
                        tipoRecipiente = (TipoRecipiente) parent.getItemAtPosition(posicaoSelecionada);
                        CriaRec.alterarRec(ListRec.this, 2, tipoRecipiente);
                    }
                });

        listViewTipos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewTipos.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view,
                                                   int position,
                                                   long id) {

                        if (actionMode != null) {
                            return false;
                        }

                        posicaoSelecionada = position;
                        tipoRecipiente = (TipoRecipiente) parent.getItemAtPosition(posicaoSelecionada);
                        view.setBackgroundColor(Color.LTGRAY);
                        viewSelecionada = view;
                        listViewTipos.setEnabled(false);
                        actionMode = startSupportActionMode(mActionModeCallback);
                        return true;
                    }
                });
        popularLista();
    }

    public void restartApp () {
        Intent i = new Intent(getApplicationContext(), ListRec.class);
        startActivity(i);
        finish();
    }

    private void popularLista(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                UsuarioDatabase database = UsuarioDatabase.getDatabase(ListRec.this);
                List<TipoRecipiente> lista = database.tipoRecDAO().queryAll();
                ListRec.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(ListRec.this,
                                android.R.layout.simple_list_item_1,
                                lista);
                        listViewTipos.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.principal_item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.menuItemAlterar:
                    alterar();
                    mode.finish();
                    return true;

                case R.id.menuItemExcluir:
                    excluirTipo(tipoRecipiente);
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (viewSelecionada != null){
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode         = null;
            viewSelecionada    = null;

            listViewTipos.setEnabled(true);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rec_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuItemCriar:
                CriaRec.criarRec(this, 1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 1 || requestCode == 2) &&resultCode == Activity.RESULT_OK){
            popularLista();
        }
    }

    private void excluirTipo(final TipoRecipiente tipoRecipiente){
        String mensagem = getString(R.string.deleteMsgUsr) + " " + tipoRecipiente.getRecipiente() + "?";

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        UsuarioDatabase database = UsuarioDatabase.getDatabase(ListRec.this);
                                        database.tipoRecDAO().delete(tipoRecipiente);
                                    }
                                });
                                listaAdapter.remove(tipoRecipiente);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    private void alterar(){
        CriaRec.alterarRec(this, 2, tipoRecipiente);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
