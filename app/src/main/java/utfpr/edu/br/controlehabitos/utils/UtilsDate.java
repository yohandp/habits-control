package utfpr.edu.br.controlehabitos.utils;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilsDate {

    public static String formatDate(Context context, Date date){
        SimpleDateFormat dateFormat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM/dd/yyyy");
            dateFormat = new SimpleDateFormat(pattern);
        }else{
            dateFormat = (SimpleDateFormat) DateFormat.getMediumDateFormat(context);
        }
        return dateFormat.format(date);
    }

    public static String formatDate(Date date){
        SimpleDateFormat dateFormat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM/dd/yyyy");
            dateFormat = new SimpleDateFormat(pattern);
        }else{
            return null;
        }
        return dateFormat.format(date);
    }

    public static String formatHour(Date date){
        SimpleDateFormat format;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "HH:mm");
            format = new SimpleDateFormat(pattern);
        }else{
            return null;
        }
        return format.format(date);
    }

    public static int totalAnos(Date date) {
        Calendar dataAnterior = Calendar.getInstance();
        dataAnterior.setTime(date);
        Calendar dataAtual = Calendar.getInstance();
        dataAtual.setTime(new Date());
        return dataAtual.get(Calendar.YEAR) - dataAnterior.get(Calendar.YEAR);
    }

}
