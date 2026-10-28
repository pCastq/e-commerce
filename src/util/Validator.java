package util;

import java.util.regex.Pattern;

/**
 * Regole di validazione dei campi. Sono le STESSE del JavaScript (registrazione.js):
 * il controllo nel browser serve all'utente, ma si può aggirare, quindi il server
 * deve rifare sempre i controlli.
 */
public final class Validator {

    private Validator() { }

    // Lettere (anche accentate), spazio, apostrofo, trattino; 2-50 caratteri.
    private static final Pattern NOME = Pattern.compile("^[\\p{L}' -]{2,50}$");

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Almeno: 1 minuscola, 1 maiuscola, 1 cifra, 1 speciale tra . , _ - ! ? @ ; lunghezza 8-100.
    private static final Pattern PASSWORD =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.,_\\-!?@]).{8,100}$");

    public static boolean isNome(String s)     { return s != null && NOME.matcher(s).matches(); }
    public static boolean isEmail(String s)    { return s != null && s.length() <= 255 && EMAIL.matcher(s).matches(); }
    public static boolean isPassword(String s) { return s != null && PASSWORD.matcher(s).matches(); }
}
