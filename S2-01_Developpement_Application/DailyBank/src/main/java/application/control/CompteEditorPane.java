package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

/**
 * Classe de gestion de l'édition d'un compte.
 * <p>
 *     Cette classe permet de gérer l'édition d'un compte à travers une interface graphique dédiée.
 *     Elle initialise et affiche la fenêtre de gestion d'édition de compte en fonction du mode d'édition spécifié.
 * </p>
 *
 * <p>
 *     L'initialisation de cette classe crée une nouvelle fenêtre modale pour l'édition du compte,
 *     basée sur les informations du client et du compte courant fournis.
 * </p>
 *
 * <p>
 *     Cette classe utilise une vue chargée depuis un fichier FXML pour représenter l'interface graphique d'édition.
 * </p>
 *
 * <p>
 *     Les méthodes publiques incluent la construction et l'affichage de la fenêtre modale d'édition,
 *     ainsi que la récupération du compte édité après la fermeture de la fenêtre.
 * </p>
 *
 * <p>
 *     Cette classe fait partie de l'application DailyBank et nécessite une configuration initiale de l'état
 *     de l'application via l'objet DailyBankState.
 * </p>
 */
public class CompteEditorPane {

    private Stage cepStage;
    private CompteEditorPaneViewController cepViewController;

    /**
     * Constructeur de la classe CompteEditorPane.
     *
     * @param _parentStage La fenêtre parente à partir de laquelle cette fenêtre est créée.
     * @param _dbstate L'état actuel de l'application bancaire.
     */
    public CompteEditorPane(Stage _parentStage, DailyBankState _dbstate) {

        try {
            FXMLLoader loader = new FXMLLoader(CompteEditorPaneViewController.class.getResource("compteeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.cepStage = new Stage();
            this.cepStage.initModality(Modality.WINDOW_MODAL);
            this.cepStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.cepStage);
            this.cepStage.setScene(scene);
            this.cepStage.setTitle("Gestion d'un compte");
            this.cepStage.setResizable(false);

            this.cepViewController = loader.getController();
            this.cepViewController.initContext(this.cepStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la boîte de dialogue d'édition de compte.
     * <p>
     *     Cette méthode appelle le contrôleur de la vue pour afficher la boîte de dialogue d'édition de compte
     *     pour le client spécifié, le compte courant et le mode d'édition.
     * </p>
     *
     * @param client Le client dont le compte est édité.
     * @param cpte Le compte courant à éditer.
     * @param em Le mode d'édition spécifié (ajout, modification, suppression).
     * @return Le compte courant édité après la fermeture de la boîte de dialogue.
     */
    public CompteCourant doCompteEditorDialog(Client client, CompteCourant cpte, EditionMode em) {
        return this.cepViewController.displayDialog(client, cpte, em);
    }
}
