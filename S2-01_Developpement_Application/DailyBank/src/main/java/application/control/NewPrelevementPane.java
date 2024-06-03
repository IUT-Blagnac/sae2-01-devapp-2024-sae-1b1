package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.NewPrelevementPaneViewController;
import application.view.PrelevementsManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;

/**
 * Gère l'affichage et l'initialisation de la fenêtre de gestion des prélèvements automatiques.
 * <p>
 * Cette classe est responsable de la création et de la configuration de la fenêtre modale pour ajouter,
 * modifier ou supprimer un prélèvement automatique. Elle utilise le {@link NewPrelevementPaneViewController}
 * pour contrôler la logique de l'interface utilisateur.
 * </p>
 *
 * @see NewPrelevementPaneViewController
 * @see PrelevementsManagementViewController
 * @see DailyBankState
 * @see CompteCourant
 * @see EditionMode
 * @see DailyBankApp
 *
 * @author Yassir BOULOUIHA GNAOUI
 */
public class NewPrelevementPane {

    private Stage nppStage;
    private NewPrelevementPaneViewController nppViewController;
    private DailyBankState dailyBankState;

    /**
     * Constructeur pour créer une nouvelle instance de la fenêtre de gestion des prélèvements.
     *
     * @param _parentStage La fenêtre parente.
     * @param _dbstate L'état courant de l'application bancaire.
     * @param p Le contrôleur de gestion des prélèvements.
     * @param cpt Le compte courant concerné par les prélèvements.
     *
     * @see NewPrelevementPaneViewController
     * @see PrelevementsManagementViewController
     * @see DailyBankState
     * @see CompteCourant
     * @see EditionMode
     * @see DailyBankApp

     *
     * @see DailyBankApp
     */
    public NewPrelevementPane(Stage _parentStage, DailyBankState _dbstate, PrelevementsManagementViewController p, CompteCourant cpt) {
        this.dailyBankState = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(NewPrelevementPaneViewController.class.getResource("newprelevementpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.nppStage = new Stage();
            this.nppStage.initModality(Modality.WINDOW_MODAL);
            this.nppStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.nppStage);
            this.nppStage.setScene(scene);
            this.nppStage.setTitle("Gestion d'un prélèvement");
            this.nppStage.setResizable(false);

            this.nppViewController = loader.getController();
            this.nppViewController.initContext(this.nppStage, this.dailyBankState, p, cpt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la boîte de dialogue pour la gestion des prélèvements en fonction du mode d'édition.
     *
     * @param em Le mode d'édition (création, modification, suppression).
     *
     * @see NewPrelevementPaneViewController#displayDialog(EditionMode)
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html#showAndWait--">Stage.showAndWait()</a>
     */
    public void doNewPrelevementDialog(EditionMode em) {
        this.nppViewController.displayDialog(em);
    }
}
