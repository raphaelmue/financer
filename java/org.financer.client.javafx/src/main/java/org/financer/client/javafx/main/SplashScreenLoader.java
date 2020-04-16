package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.financer.client.connection.ServerRequest;
import org.financer.client.format.I18N;
import org.financer.client.javafx.local.LocalStorageImpl;

public class SplashScreenLoader extends Preloader {
    private JFXProgressBar progressBar;
    private Label infoMessage;
    private boolean noLoadingProgress = true;
    private Stage primaryStage = null;

    private Parent getContent() {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: -fx-color-white; -fx-padding: 30px; -fx-background-radius: 20px");

        Label heading = new Label("F I N A N C E R");
        heading.setAlignment(Pos.CENTER);
        heading.setTextAlignment(TextAlignment.CENTER);
        heading.setStyle("-fx-text-fill: -fx-color-primary; -fx-font-size: 22px; -fx-padding: 15px 50px; -fx-font-weight: 700; -fx-font-family: 'Roboto Medium'");

        Image image = new Image(getClass().getResourceAsStream("/images/icons/financer-icon-clipart.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(120);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);

        infoMessage = new Label();
        infoMessage.setStyle("-fx-text-fill: -fx-color-dark-gray; -fx-font-size: 14px; -fx-padding: 15px 20px");

        progressBar = new JFXProgressBar();
        progressBar.setProgress(-1.0);
        progressBar.setPrefWidth(500);

        HBox hBox = new HBox(imageView, heading);
        hBox.setPrefWidth(500);
        hBox.setAlignment(Pos.CENTER);

        borderPane.setCenter(hBox);
        borderPane.setBottom(new VBox(infoMessage, progressBar));
        return borderPane;
    }

    public void start(Stage primaryStage) {
        I18N.setLocalStorage(LocalStorageImpl.getInstance());
        ServerRequest.setLocalStorage(LocalStorageImpl.getInstance());

        Scene scene = new Scene(getContent(), 500, 300);
        scene.getStylesheets().add(getClass().getResource("style/colors.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("style/main.style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(FinancerApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));

        this.primaryStage = primaryStage;

        if (LocalStorageImpl.getInstance().readObject("user") == null) {
            this.primaryStage.hide();
        } else {
            this.primaryStage.show();
        }
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        if (pn.getProgress() != 1.0 || !noLoadingProgress) {
            progressBar.setProgress(pn.getProgress() / 2);
            if (pn.getProgress() > 0) {
                noLoadingProgress = false;
            }
        } else {
            this.primaryStage.hide();
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        //ignore, hide after application signals it is ready
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification preloaderNotification) {
        if (preloaderNotification instanceof ProgressNotification) {
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
            double progress = ((ProgressNotification) preloaderNotification).getProgress();
            progressBar.setProgress(progress);

            if (progress < 0.1) {
                infoMessage.setText(I18N.get("loadingCategories"));
            } else if (progress < 0.4) {
                infoMessage.setText(I18N.get("loadingTransactions"));
            } else {
                infoMessage.setText(I18N.get("loadingFixedTransactions"));
            }

        } else if (preloaderNotification instanceof StateChangeNotification) {
            primaryStage.hide();
        }
    }

}
