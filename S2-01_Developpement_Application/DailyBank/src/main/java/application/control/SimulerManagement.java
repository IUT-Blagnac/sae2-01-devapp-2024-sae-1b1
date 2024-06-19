package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.OperationsManagementViewController;
import application.view.SimulerManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * La classe SimulerManagement gère la fenêtre de simulation d'emprunts et assurances dans l'application DailyBank.
 * Elle utilise SimulerManagementViewController pour la logique de la vue et DailyBankState pour maintenir l'état de l'application.
 * La classe utilise également OperationsManagementViewController pour charger l'interface utilisateur à partir du fichier FXML.
 *
 * @author Clara Figueras
 * @version 1.0
 * @see DailyBankState
 * @see SimulerManagementViewController
 * @see OperationsManagementViewController
 */
public class SimulerManagement {

    private Stage primaryStage;
    private DailyBankState dailyBankState;
    private SimulerManagementViewController smcViewController;

    /**
     * Constructeur de la classe SimulerManagement.
     *
     * @param _parentStage le stage parent pour la fenêtre de simulation
     * @param _dbstate l'état quotidien de la banque
     */
    public SimulerManagement(Stage _parentStage, DailyBankState _dbstate) {

        this.dailyBankState = _dbstate;
        this.primaryStage = _parentStage;

        try {
            FXMLLoader loader = new FXMLLoader(
                    OperationsManagementViewController.class.getResource("simulermanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 550 + 20, 390 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);

            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Simulations d'emprunts/assurances");
            this.primaryStage.setResizable(false);

            this.smcViewController = loader.getController();
            this.smcViewController.initContext(this.primaryStage, this, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la boîte de dialogue de gestion des simulations d'emprunts/assurances.
     *
     * @see SimulerManagementViewController#displayDialog()
     */
    public void doSimulerManagementDialog() {
        this.smcViewController.displayDialog();
    }
}
