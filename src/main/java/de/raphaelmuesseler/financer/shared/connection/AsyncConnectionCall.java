package de.raphaelmuesseler.financer.shared.connection;

import com.mysql.cj.exceptions.CJCommunicationsException;
import javafx.scene.control.Alert;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface AsyncConnectionCall extends AsyncCall<ConnectionResult> {
    @Override
    void onSuccess(ConnectionResult result);

    @Override
    default void onFailure(Exception exception) {}

    default void onBefore() {};
    default void onAfter() {};
}
