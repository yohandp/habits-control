package utfpr.edu.br.controlehabitos.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import utfpr.edu.br.controlehabitos.modelo.Usuario;

@Dao
public interface UsuarioDAO {
    @Insert
    long insert(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Update
    void update(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE id = :id")
    Usuario queryForId(long id);

    @Query("SELECT * FROM usuario ORDER BY nome ASC")
    List<Usuario> queryAll();
}
