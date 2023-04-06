package com.br.guilhermematthew.nowly.commons.common.utility.string;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtility {

    public static final DecimalFormat decimalFormat = new DecimalFormat("###,###.##");

    public static String cpuQuality(final double percentage) {
        if (percentage <= 60.0D) return "§a" + percentage;
        if (percentage > 60.0D && percentage < 90.0D) return "§e" + percentage;
        return "§c" + percentage;
    }

    public static String ramQuality(double percentage) {
        if (percentage <= 60.0D) return "§a" + percentage;
        if (percentage > 60.0D && percentage < 90.0D) return "§e" + percentage;
        return "§c" + percentage;
    }

    public static String toMillis(double d) {
        String string = String.valueOf(d);
        StringBuilder sb = new StringBuilder();
        boolean stop = false;

        for (char c : string.toCharArray()) {
            if (stop)
                return sb.append(c).toString();
            if (c == '.')
                stop = true;
            sb.append(c);
        }
        return sb.toString();
    }

    public static String reformuleMegaBytes(Long megaBytes) {
        if (megaBytes <= 999) return megaBytes + " MB";

        long mb = megaBytes;
        long gigas = (megaBytes / 1000);

        mb = (mb - (gigas * 1000));

        if (mb != 0) {
            String mbFormatted = String.valueOf(mb).substring(0, 1);
            return gigas + "." + mbFormatted + " GB";
        } else {
            return gigas + " GB";
        }
    }

    public static String formatStringToArrayWithoutSpace(final List<String> array) {
        return formatArrayToString(array, false);
    }

    public static String formatArrayToString(List<String> array, boolean lowerCase) {
        if (array.size() == 1) return array.get(0);

        final StringBuilder toReturn = new StringBuilder();

        for (int i = 0; i < array.size(); i++) {
            String string = array.get(i);

            if (lowerCase) {
                toReturn.append(string.toLowerCase()).append(array.size() - i > 1 ? ", " : "");
            } else {
                toReturn.append(string).append(array.size() - i > 1 ? ", " : "");
            }

            string = null;
        }

        return toReturn.toString();
    }

    public static String formatArrayToStringWithoutSpace(List<String> array, boolean lowerCase) {
        if (array.size() == 1) return array.get(0);


        final StringBuilder toReturn = new StringBuilder();

        for (int i = 0; i < array.size(); i++) {
            String string = array.get(i);

            if (lowerCase) {
                toReturn.append(string.toLowerCase()).append(array.size() - i > 1 ? "," : "");
            } else {
                toReturn.append(string).append(array.size() - i > 1 ? "," : "");
            }
            string = null;
        }

        return toReturn.toString();
    }

    public static boolean isInteger(final String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static String createArgs(int index, String[] args) {
        return createArgs(index, args, "", false);
    }

    public static String createArgs(int index, String[] args, String defaultArgs, boolean color) {
        StringBuilder sb = new StringBuilder();

        for (int i = index; i < args.length; i++)
            sb.append(args[i]).append((i + 1 >= args.length ? "" : " "));

        if (sb.length() == 0)
            sb.append(defaultArgs);

        return color ? sb.toString().replace("&", "§") : sb.toString();
    }

    public static String replace(String message, String[] old, String[] now) {
        String replaced = message;
        for (int i = 0; i < old.length; i++)
            replaced = replaced.replace(old[i], now[i]);
        return replaced;
    }

    public static String formatValue(int valor) {
        return decimalFormat.format(valor);
    }

    public static List<String> formatStringToArrayWithoutSpace(String formatted) {
        List<String> lista = new ArrayList<>();

        if (formatted.equals("") || (formatted.equals(" ") || (formatted.isEmpty()))) {
            return lista;
        }

        if (formatted.contains(" ")) {
            formatted = formatted.replaceAll(" ", "");
        }

        if (formatted.contains(",")) {
            lista.addAll(Arrays.asList(formatted.split(",")));
        } else {
            lista.add(formatted);
        }
        return lista;
    }

    public static List<String> formatStringToArray(String formatted) {
        List<String> lista = new ArrayList<>();
        if (formatted.equals("") || (formatted.equals(" ") || (formatted.isEmpty()))) {
            return lista;
        }
        if (formatted.contains(",")) {
            lista.addAll(Arrays.asList(formatted.split(",")));
        } else {
            lista.add(formatted);
        }
        return lista;
    }

    public static Object convertValue(String value, String classExpected) {
        if (classExpected.equalsIgnoreCase("String"))
            return value;
        else if (classExpected.equalsIgnoreCase("Integer"))
            return Integer.valueOf(value);
        else if (classExpected.equalsIgnoreCase("Long"))
            return Long.valueOf(value);
        else if (classExpected.equalsIgnoreCase("Boolean"))
            return Boolean.valueOf(value);
        else if (classExpected.equalsIgnoreCase("List")) {
            return "?";
        }
        return value;
    }
}