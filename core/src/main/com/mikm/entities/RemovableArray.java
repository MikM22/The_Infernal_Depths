package com.mikm.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mikm.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class RemovableArray<T extends InanimateEntity> extends ArrayList<T> {
    private ArrayList<T> toAdd = new ArrayList<>();
    private ArrayList<T> toRemove = new ArrayList<>();
    private boolean executeMethodAfterRender;
    private ArrayList<Method> queuedActionsToDoAfterRender = new ArrayList<>();

    public RemovableArray() {

    }

    public RemovableArray(ArrayList<T> list) {
        this.addAll(list);
    }

    public void draw(Batch batch) {
        for (T inanimateEntity : this) {
            inanimateEntity.draw(batch);
        }
    }

    public void render(Batch batch) {
        for (T inanimateEntity : this) {
            inanimateEntity.render(batch);
        }
        if (toAdd.size() != 0) {
            super.addAll(toAdd);
            this.sort(Comparator.comparing(InanimateEntity::getZOrder));
        }
        if (toRemove.size() != 0) {
            super.removeAll(toRemove);
        }
        if (toAdd.size() != 0) {
            toAdd.clear();
        }
        if (toRemove.size() != 0) {
            toRemove.clear();
        }
        if (executeMethodAfterRender) {
            executeMethodAfterRender = false;
            for (Method queuedAction : queuedActionsToDoAfterRender) {
                queuedAction.invoke();
            }
            queuedActionsToDoAfterRender.clear();
        }
    }

    public void addInstantly(T t) {
        super.add(t);
        this.sort(Comparator.comparing(InanimateEntity::getZOrder));
    }

    @Override
    public void clear() {
        super.clear();
        toAdd.clear();
        toRemove.clear();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean b = super.addAll(c);
        this.sort(Comparator.comparing(InanimateEntity::getZOrder));
        return b;
    }

    public void removeInstantly(T t) {
        super.remove(t);
    }

    @Override
    public boolean add(T t) {
        return toAdd.add(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        return toRemove.add((T)o);
    }

    public void doAfterRender(Method method) {
        queuedActionsToDoAfterRender.add(method);
        executeMethodAfterRender = true;
    }
}
