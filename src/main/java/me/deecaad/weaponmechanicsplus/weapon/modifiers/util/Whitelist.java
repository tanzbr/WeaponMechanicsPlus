package me.deecaad.weaponmechanicsplus.weapon.modifiers.util;

import java.util.Collection;

public class Whitelist<E> {

    private boolean isWhitelist;
    private Collection<E> list;

    public Whitelist(boolean isWhitelist, Collection<E> list) {
        this.isWhitelist = isWhitelist;
        this.list = list;
    }

    public boolean isWhitelist() {
        return isWhitelist;
    }

    public void setWhitelist(boolean whitelist) {
        isWhitelist = whitelist;
    }

    public Collection<E> getList() {
        return list;
    }

    public void setList(Collection<E> list) {
        this.list = list;
    }

    public boolean isWhitelisted(E object) {
        return isWhitelist == list.contains(object);
    }
}
