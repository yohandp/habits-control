package utfpr.edu.br.controlehabitos.persistencia;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

import utfpr.edu.br.controlehabitos.R;
import utfpr.edu.br.controlehabitos.modelo.TipoRecipiente;
import utfpr.edu.br.controlehabitos.modelo.Usuario;

@Database(entities = {Usuario.class, TipoRecipiente.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class UsuarioDatabase extends RoomDatabase {

    public abstract UsuarioDAO usuarioDAO();
    public abstract TipoRecDAO tipoRecDAO();

    private static UsuarioDatabase instance;

    public static UsuarioDatabase getDatabase(final Context context){
        if(instance == null){
            synchronized (UsuarioDatabase.class) {
                if (instance == null) {
                    RoomDatabase.Builder builder =  Room.databaseBuilder(context,
                            UsuarioDatabase.class,
                            "usuarios.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    carregaRecs(context);
                                }
                            });
                        }
                    });

                    instance = (UsuarioDatabase) builder.build();
                }
            }
        }
        return instance;
    }

    private static void carregaRecs(final Context context){

        String[] descricoes = context.getResources().getStringArray(R.array.recipientes);
        int i = 0;
        for (String descricao : descricoes) {
            TipoRecipiente tipoRecipiente = new TipoRecipiente(descricao, i*2);
            i+=100;
            instance.tipoRecDAO().insert(tipoRecipiente);
        }
    }

}

