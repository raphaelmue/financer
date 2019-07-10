package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXProgressBar;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreenLoader extends Preloader {
    private JFXProgressBar progressBar;
    private Label infoMessage;
    private boolean noLoadingProgress = true;
    private Stage primaryStage;

    private Parent getContent() {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #575757");

        Label heading = new Label("F I N A N C E R");
        heading.setAlignment(Pos.CENTER);
        heading.setTextAlignment(TextAlignment.CENTER);
        heading.setStyle("-fx-text-fill: #fafafa; -fx-font-size: 18px; -fx-padding: 15px 50px");

        Image image = new Image(getClass().getResourceAsStream("/images/icons/financer-icon-square-inverse.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(120);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);

        infoMessage = new Label();
        infoMessage.setStyle("-fx-text-fill: #fafafa; -fx-font-size: 14px; -fx-padding: 15px 20px");

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
        ServerRequestHandler.setLocalStorage(LocalStorageImpl.getInstance());

        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(getContent(), 500, 300);
        scene.getStylesheets().add(getClass().getResource("style/main.style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(FinancerApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));
        primaryStage.show();

        this.primaryStage = primaryStage;
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        if (pn.getProgress() != 1.0 || !noLoadingProgress) {
            progressBar.setProgress(pn.getProgress() / 2);
            if (pn.getProgress() > 0) {
                noLoadingProgress = false;
            }
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
