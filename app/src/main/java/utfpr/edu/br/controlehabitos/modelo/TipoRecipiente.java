package utfpr.edu.br.controlehabitos.modelo;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tipoRecipiente",
        indices = @Index(value = {"recipiente"}, unique = true))
public class TipoRecipiente {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String recipiente;

    @NonNull
    private int ml;

    //remover essa tabela, unir com usuario, criar tipo recipiente e consumo atual

    public TipoRecipiente(String recipiente, int ml){
        setRecipiente(recipiente);
        setMl(ml);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getRecipiente() {
        return recipiente;
    }

    public void setRecipiente(@NonNull String recipiente) {
        this.recipiente = recipiente;
    }

    public int getMl() {
        return ml;
    }

    public void setMl(int ml) {
        this.ml = ml;
    }

    @Override
    public String toString(){
        return getRecipiente() + " has " + getMl() + "ml";
    }
}