package de.raphaelmuesseler.financer.client.javafx.dialogs;

import de.raphaelmuesseler.financer.client.format.FormatterImpl;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.exceptions.FinancerException;
import de.raphaelmuesseler.financer.shared.exceptions.NotAuthorizedException;

import java.net.ConnectException;
import java.net.UnknownHostException;

public class FinancerExceptionDialog extends FinancerAlert {
    public FinancerExceptionDialog(String header, Exception exception) {
        super(AlertType.ERROR);

        this.setHeaderText(header);

        String message = "Something went wrong. Please try again later.";
        try {
            throw exception;
        } catch (NotAuthorizedException e) {
            message = I18N.get("errNotAuthorized");
        } catch (UnknownHostException e) {
            message = "The database is not available at the moment. Please try again later";
        } catch (ConnectException connectException) {
            message = "Server is currently not available. Please try again later.";
        } catch (FinancerException financerException) {
            message = new FormatterImpl(LocalStorageImpl.getInstance()).formatExceptionMessage(financerException);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.setContentText(message);
        }
    }
}
