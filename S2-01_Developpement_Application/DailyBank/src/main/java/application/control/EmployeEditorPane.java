package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneViewController;
import application.view.EmployeEditorViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.data.CompteCourant;
import model.data.Employe;

/**
 * La classe EmployeEditorPane permet de gérer l'édition et la visualisation d'un employé
 * au sein de l'application DailyBank.
 * Elle permet d'afficher une interface utilisateur pour la gestion des employés, y compris
 * l'édition et l'ajout d'un nouvel employé.
 * Elle utilise EmployeEditorViewController pour le contrôle de la vue et DailyBankState pour
 * maintenir l'état de l'application.
 *
 * @see DailyBankState
 * @see EmployeEditorViewController
 */
public class EmployeEditorPane {

    private Stage cepStage;
    private EmployeEditorViewController cepViewController;
    private DailyBankState dailyBankState;

    /**
     * Constructeur de la classe EmployeEditorPane.
     *
     * @param _parentStage le stage parent pour la fenêtre de gestion d'employé
     * @param _dbstate l'état quotidien de la banque
     */
    public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(CompteEditorPaneViewController.class.getResource("employeeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.cepStage = new Stage();
            this.cepStage.initModality(Modality.WINDOW_MODAL);
            this.cepStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.cepStage);
            this.cepStage.setScene(scene);
            this.cepStage.setTitle("Gestion d'un employer");
            this.cepStage.setResizable(false);

            this.cepViewController = loader.getController();
            this.cepViewController.initContext(this.cepStage, this.dailyBankState);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la fenêtre de gestion d'employé pour l'édition ou l'ajout d'un employé.
     *
     * @param emp l'employé à éditer ou à ajouter
     * @param em le mode d'édition (EditionMode.EDIT pour éditer, EditionMode.ADD pour ajouter)
     * @return l'employé modifié ou ajouté
     * @see EmployeEditorViewController#displayDialog(Employe, EditionMode)
     */
    public Employe doEmployeEditorDialog(Employe emp, EditionMode em) {
        return this.cepViewController.displayDialog(emp, em);
    }
}
