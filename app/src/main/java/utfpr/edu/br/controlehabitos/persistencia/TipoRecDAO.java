package utfpr.edu.br.controlehabitos.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import utfpr.edu.br.controlehabitos.modelo.TipoRecipiente;

@Dao
public interface TipoRecDAO {
    @Insert
    long insert(TipoRecipiente tipo);

    @Delete
    void delete(TipoRecipiente tipo);

    @Update
    void update(TipoRecipiente tipo);

    @Query("SELECT * FROM tipoRecipiente WHERE id = :id")
    TipoRecipiente queryForId(long id);

    @Query("SELECT * FROM tipoRecipiente ORDER BY id DESC")
    List<TipoRecipiente> queryAll();

    @Query("SELECT id FROM tipoRecipiente ORDER BY id LIMIT 1")
    long firstEl();
}