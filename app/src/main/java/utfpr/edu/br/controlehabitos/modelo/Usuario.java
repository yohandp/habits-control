package utfpr.edu.br.controlehabitos.modelo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

import utfpr.edu.br.controlehabitos.utils.UtilsDate;

@Entity(tableName = "usuario",
        foreignKeys = @ForeignKey(entity = TipoRecipiente.class,
                parentColumns = "id",
                childColumns  = "idUltRec"))
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nome;
    private Date dataNasc;
    private float altura;
    private float peso;
    private float imc;
    private int freqEx;
    private float litrosRestantes;
    private Date dataCad, dataAg;

    @ColumnInfo(index = true)
    private int idUltRec;

    public Usuario(String nome, float altura, float peso, float imc, int freqEx, Date dataNasc){
        setNome(nome);
        setAltura(altura);
        setPeso(peso);
        setImc(imc);
        setFreqEx(freqEx);
        setDataNasc(dataNasc);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public float getImc() {
        return imc;
    }

    public void setImc(float imc) {
        this.imc = imc;
    }

    public int getFreqEx() {
        return freqEx;
    }

    public void setFreqEx(int freqEx) {
        this.freqEx = freqEx;
    }

    public int getIdUltRec() {
        return idUltRec;
    }

    public void setIdUltRec(int idUltRec) {
        this.idUltRec = idUltRec;
    }

    public float getLitrosRestantes() {
        return litrosRestantes;
    }

    public void setLitrosRestantes(float litrosRestantes) {
        this.litrosRestantes = litrosRestantes;
    }

    public Date getDataCad() {
        return dataCad;
    }

    public void setDataCad(Date dataCad) {
        this.dataCad = dataCad;
    }

    public Date getDataAg() {
        return dataAg;
    }

    public void setDataAg(Date dataAg) {
        this.dataAg = dataAg;
    }

    public Date getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(Date dataNasc) {
        this.dataNasc = dataNasc;
    }

    @Override
    public String toString(){
        return UtilsDate.formatDate(getDataCad()) + "\n" + getNome() + "\t - \t" + UtilsDate.totalAnos(getDataNasc());
    }


}