package de.theonlymarv.computermonitor.Interfaces;

/**
 * Created by Marvin on 15.08.2016.
 */
public interface ChooseDialogEvents<T> {
    void OnChoose(T t);

    void OnEmptyChooseList();
}
