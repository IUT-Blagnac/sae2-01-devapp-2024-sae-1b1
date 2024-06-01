package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ComptesManagementViewController;
import application.view.PrelevementsManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

public class PrelevementsManagement {

    private Stage cmStage;
    private PrelevementsManagementViewController pmViewController;
    private DailyBankState dailyBankState;
    private Client clientDesComptes;
    private CompteCourant compteEdite;

    public PrelevementsManagement (Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant cpt) {
        this.clientDesComptes = client;
        this.compteEdite = cpt;
        this.dailyBankState = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(PrelevementsManagementViewController.class.getResource("prelevementsmanager.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.cmStage = new Stage();
            this.cmStage.initModality(Modality.WINDOW_MODAL);
            this.cmStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.cmStage);
            this.cmStage.setScene(scene);
            this.cmStage.setTitle("Gestion des prélèvements automatiques");
            this.cmStage.setResizable(false);

            this.pmViewController = loader.getController();
            this.pmViewController.initContext(this.cmStage, this, _dbstate, client, cpt);

            this.cmStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doPrelevementManagementDialog() {
        this.pmViewController.displayDialog(compteEdite);
    }
}
