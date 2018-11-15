package de.raphaelmuesseler.financer.util.collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class CollectionUtil {
    public static <T> ObservableList<T> castObjectListToObservable(List<Object> list) {
        ObservableList<T> result = FXCollections.observableArrayList();
        for (Object item : list) {
            result.add((T) item);
        }
        return result;
    }


    public static <T> ObservableList<T> castListToObserableList(List<T> list) {
        ObservableList<T> result = FXCollections.observableArrayList();
        result.addAll(list);
        return result;
    }
}
