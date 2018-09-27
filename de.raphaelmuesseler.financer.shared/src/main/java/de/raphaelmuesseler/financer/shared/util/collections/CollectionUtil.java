package de.raphaelmuesseler.financer.shared.util.collections;

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
}
