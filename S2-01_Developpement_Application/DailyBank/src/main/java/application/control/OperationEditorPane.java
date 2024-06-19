package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.StageManagement;
import application.view.OperationEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * La classe OperationEditorPane gère l'édition et l'enregistrement des opérations bancaires
 * dans l'application DailyBank.
 * Elle utilise OperationEditorPaneViewController pour le contrôle de la vue et DailyBankState pour
 * maintenir l'état de l'application.
 *
 * @see DailyBankState
 * @see OperationEditorPaneViewController
 */
public class OperationEditorPane {

    private Stage oepStage;
    private OperationEditorPaneViewController oepViewController;

    /**
     * Constructeur de la classe OperationEditorPane.
     *
     * @param _parentStage le stage parent pour la fenêtre d'édition des opérations
     * @param _dbstate l'état quotidien de la banque
     */
    public OperationEditorPane(Stage _parentStage, DailyBankState _dbstate) {

        try {
            FXMLLoader loader = new FXMLLoader(OperationEditorPaneViewController.class.getResource("operationeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 500 + 20, 250 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.oepStage = new Stage();
            this.oepStage.initModality(Modality.WINDOW_MODAL);
            this.oepStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.oepStage);
            this.oepStage.setScene(scene);
            this.oepStage.setTitle("Enregistrement d'une opération");
            this.oepStage.setResizable(false);

            this.oepViewController = loader.getController();
            this.oepViewController.initContext(this.oepStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la fenêtre d'édition des opérations pour enregistrer une nouvelle opération.
     *
     * @param cpte le compte courant pour lequel l'opération est enregistrée
     * @param cm la catégorie d'opération à enregistrer
     * @return l'opération enregistrée
     * @see OperationEditorPaneViewController#displayDialog(CompteCourant, CategorieOperation)
     */
    public Operation doOperationEditorDialog(CompteCourant cpte, CategorieOperation cm) {
        return this.oepViewController.displayDialog(cpte, cm);
    }
}
