package zad1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClientUI{


    public void ui(Stage stage) throws Exception {

        stage.setTitle("klient-serwer s24149");

        Client client = new Client();

        TextField inputWord = new TextField("");
        TextField language = new TextField("");

        inputWord.setPromptText("Słowo");
        language.setPromptText("EN, DE, ES, FR");

        Button updateButton = new Button("Aktualizuj");

        Label translationLabel = new Label("Tłumaczeniem słowa: ____ w języku: ____ jest: ____");

        updateButton.setOnAction(event -> {

            if(inputWord.getText().isEmpty() || language.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.NONE, "Wszystkie pola muszą być wypełnione", ButtonType.OK);
                alert.showAndWait();
            }else {
                String translatedWord = client.clientLogic(inputWord.getText(), language.getText());

                if (translatedWord.equals("null")) {

                    Alert alert1 = new Alert(Alert.AlertType.NONE, "Brak słowa w słowniku", ButtonType.OK);
                    alert1.showAndWait();

                } else {

                    translationLabel.setText("Tłumaczeniem słowa: " + inputWord.getText().toUpperCase() + " w języku: " + language.getText().toUpperCase() + " jest: " + translatedWord.toUpperCase());

                    System.out.println("inputWord: " + inputWord);
                    System.out.println("language: " + language);
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(inputWord, 0, 0);
        gridPane.add(language, 0, 1);
        gridPane.add(updateButton, 1, 0, 1, 2);
        gridPane.add(translationLabel, 0, 4);

        Scene scene = new Scene(gridPane, 600, 300);

        stage.setScene(scene);

        stage.show();
    }

}
