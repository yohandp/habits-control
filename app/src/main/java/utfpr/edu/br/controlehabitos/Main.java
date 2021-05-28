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

import utfpr.edu.br.controlehabitos.modelo.Usuario;
import utfpr.edu.br.controlehabitos.persistencia.UsuarioDatabase;
import utfpr.edu.br.controlehabitos.utils.UtilsGUI;

public class Main extends AppCompatActivity {

    public static final int ConstDados = 1;
    private int posicaoSelecionada = -1;
    private ActionMode actionMode;
    private View viewSelecionada;

    ListView listViewPessoas;
    private ArrayList<Usuario> pessoas;
    private ArrayAdapter<Usuario> listaAdapter;
    private Usuario usuario;
    public static boolean reloadNedeed = false;

    SharedPref sharedpref;
    private Switch myswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);

        if (sharedpref.loadNightModeState() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myswitch = (Switch) findViewById(R.id.mySwitch);

        if (sharedpref.loadNightModeState() == true) {
            myswitch.setChecked(true);
        }

        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedpref.setNightModeState(true);
                } else {
                    sharedpref.setNightModeState(false);
                }
                restartApp();
            }
        });

        listViewPessoas = findViewById(R.id.listView);

        listViewPessoas.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {

                        posicaoSelecionada = position;
                        usuario = (Usuario) parent.getItemAtPosition(posicaoSelecionada);
                        ActAgua.actAgua(Main.this, usuario);
                    }
                });

        listViewPessoas.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewPessoas.setOnItemLongClickListener(
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
                        usuario = (Usuario) parent.getItemAtPosition(posicaoSelecionada);
                        view.setBackgroundColor(Color.LTGRAY);
                        viewSelecionada = view;
                        listViewPessoas.setEnabled(false);
                        actionMode = startSupportActionMode(mActionModeCallback);
                        return true;
                    }
                });
        popularLista();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.reloadNedeed)
            this.popularLista();

        this.reloadNedeed = false;
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), Main.class);
        startActivity(i);
        finish();
    }

    private void popularLista() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                UsuarioDatabase database = UsuarioDatabase.getDatabase(Main.this);
                List<Usuario> lista = database.usuarioDAO().queryAll();
                Main.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(Main.this, android.R.layout.simple_list_item_1, lista);
                        listViewPessoas.setAdapter(listaAdapter);
                        listaAdapter.notifyDataSetChanged();
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
            switch (item.getItemId()) {
                case R.id.menuItemAlterar:
                    alterar();
                    mode.finish();
                    return true;

                case R.id.menuItemExcluir:
                    excluirPessoa(usuario);
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (viewSelecionada != null) {
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode = null;
            viewSelecionada = null;

            listViewPessoas.setEnabled(true);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemSobre:
                startActivity(new Intent(Main.this, Sobre.class));
                break;
            case R.id.menuItemCriar:
                Cadastro.criarUsuario(this, 1);
                break;
            case R.id.editRec:
                listRec();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            popularLista();
        }
    }

    private void excluirPessoa(final Usuario usuario) {
        String mensagem = getString(R.string.deleteMsgUsr) + " " + usuario.getNome() + "?";

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        UsuarioDatabase database = UsuarioDatabase.getDatabase(Main.this);
                                        database.usuarioDAO().delete(usuario);
                                    }
                                });
                                listaAdapter.remove(usuario);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    private void alterar(){
        Cadastro.alterarUsuario(this, 2, usuario);
    }

    private void listRec(){
        Intent i = new Intent(getApplicationContext(), ListRec.class);
        startActivity(i);
    }

}
