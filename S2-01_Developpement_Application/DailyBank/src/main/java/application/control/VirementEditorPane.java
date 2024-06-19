package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.OperationEditorPaneViewController;
import application.view.VirementEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;

/**
 * La classe VirementEditorPane gère la fenêtre d'édition des opérations de virement dans l'application DailyBank.
 * Elle utilise VirementEditorPaneViewController pour la logique de la vue et DailyBankState pour maintenir l'état de l'application.
 *
 * @author Titouan DELAPLAGNE
 * @version 1.0
 * @see DailyBankState
 * @see VirementEditorPaneViewController
 */
public class VirementEditorPane {

    private DailyBankState dailyBankState;
    private Stage vepStage;
    private VirementEditorPaneViewController vepViewController;

    /**
     * Constructeur de la classe VirementEditorPane.
     *
     * @param _parentStage le stage parent pour la fenêtre d'édition des opérations de virement
     * @param _dbstate l'état quotidien de la banque
     */
    public VirementEditorPane(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;

        try {
            FXMLLoader loader = new FXMLLoader(
                    VirementEditorPaneViewController.class.getResource("virementeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 500 + 20, 250 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.vepStage = new Stage();
            this.vepStage.initModality(Modality.WINDOW_MODAL);
            this.vepStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.vepStage);
            this.vepStage.setScene(scene);
            this.vepStage.setTitle("Enregistrement d'une opération de virement");
            this.vepStage.setResizable(false);

            this.vepViewController = loader.getController();
            this.vepViewController.initContext(this.vepStage, this, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la boîte de dialogue d'édition des opérations de virement pour le compte courant spécifié.
     *
     * @param cpte le compte courant pour lequel l'opération de virement est éditée
     * @return Le montant du virement effectué, ou -1 si l'utilisateur a annulé l'opération
     *
     * @see VirementEditorPaneViewController#displayDialog(CompteCourant)
     */
    public double doOperationEditorDialog(CompteCourant cpte) {
        return this.vepViewController.displayDialog(cpte);
    }

    /**
     * Récupère le destinataire choisi pour le virement.
     *
     * @return Le compte courant destinataire sélectionné pour le virement
     *
     * @see VirementEditorPaneViewController#getDestinataire()
     */
    public CompteCourant getDestinataire() {
        return this.vepViewController.getDestinataire();
    }

    /**
     * Ouvre une boîte de dialogue pour choisir le destinataire du virement.
     *
     * @return Le compte courant destinataire choisi pour le virement
     *
     * @see DestPickerPane#chooseDestDialog()
     */
    public CompteCourant chooseDest() {
        DestPickerPane dpp = new DestPickerPane(this.vepStage, this.dailyBankState);
        return dpp.chooseDestDialog();
    }
}
