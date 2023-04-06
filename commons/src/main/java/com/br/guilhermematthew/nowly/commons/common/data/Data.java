package com.br.guilhermematthew.nowly.commons.common.data;

import java.util.List;

public class Data {

    private Object data;

    public Data(final Object data) {
        this.data = data;
    }

    public void add() {
        add(1);
    }

    public void add(int quantia) {
        setValue(getInt() + quantia);
    }

    public void remove() {
        remove(1);
    }

    public void remove(int quantia) {
        int atual = getInt();

        if (atual - quantia < 0) {
            setValue(0);
            return;
        }

        setValue(atual - quantia);
    }

    public void setValue(Object data) {
        this.data = data;
    }

    public Object getObject() {
        return data;
    }

    public String getString() {
        return (String) data;
    }

    public Integer getInt() {
        return (Integer) data;
    }

    public Long getLong() {
        return (Long) data;
    }

    public Boolean getBoolean() {
        return (Boolean) data;
    }

    @SuppressWarnings("unchecked")
    public List<String> getList() {
        return (List<String>) data;
    }

    @Override
    public String toString() {
        if (data != null)
            return data.toString();
        return "null";
    }
}