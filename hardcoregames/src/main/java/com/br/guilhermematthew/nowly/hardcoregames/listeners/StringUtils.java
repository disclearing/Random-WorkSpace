package com.br.guilhermematthew.nowly.hardcoregames.listeners;

import java.util.Collection;

public class StringUtils {

    /**
     * Junta uma Array em uma {@code String} utilizando um separador.
     *
     * @param array     A array para juntar.
     * @param index     O ínicio da iteração da array (0 = toda a array).
     * @param separator O separador da junção.
     * @return Resultado da junção.
     */
    public static <T> String join(T[] array, int index, String separator) {
        StringBuilder joined = new StringBuilder();
        for (int slot = index; slot < array.length; slot++) {
            joined.append(array[slot].toString() + (slot + 1 == array.length ? "" : separator));
        }

        return joined.toString();
    }

    /**
     * Junta uma Array em uma {@code String} utilizando um separador.
     *
     * @param array     A array para juntar.
     * @param separator O separador da junção.
     * @return Resultado da junção.
     */
    public static <T> String join(T[] array, String separator) {
        return join(array, 0, separator);
    }

    /**
     * Junta uma Coleção em uma {@code String} utilizando um separador.
     *
     * @param collection A coleção para juntar.
     * @param separator  O separador da junção.
     * @return Resultado da junção.
     */
    public static <T> String join(Collection<T> collection, String separator) {
        return join(collection.toArray(new Object[collection.size()]), separator);
    }


}
