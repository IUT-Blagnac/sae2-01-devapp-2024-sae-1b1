package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.PrelevementsManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

/**
 * Classe de gestion des prélèvements automatiques.
 * <p>
 * Cette classe permet d'initialiser et de gérer la fenêtre de gestion des prélèvements automatiques.
 * </p>
 *
 * @author Yassir BOULOUIHA GNAOUI
 */
public class PrelevementsManagement {

    private Stage cmStage;
    private PrelevementsManagementViewController pmViewController;
    private DailyBankState dailyBankState;
    private Client clientDesComptes;
    private CompteCourant compteEdite;

    /**
     * Constructeur de la classe PrelevementsManagement.
     * <p>
     * Initialise le contrôleur de gestion des prélèvements automatiques et charge l'interface graphique associée.
     * </p>
     *
     * @param _parentStage La fenêtre parente à partir de laquelle cette fenêtre est créée.
     * @param _dbstate L'état actuel de l'application bancaire.
     * @param client Le client dont les comptes sont gérés.
     * @param cpt Le compte courant à éditer.
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant cpt) {
        this.clientDesComptes = client;
        this.compteEdite = cpt;
        this.dailyBankState = _dbstate;
        try {
            // Chargement du fichier FXML pour la vue des prélèvements
            FXMLLoader loader = new FXMLLoader(PrelevementsManagementViewController.class.getResource("prelevementsmanager.fxml"));
            BorderPane root = loader.load();

            // Création de la scène avec la vue chargée
            Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            // Initialisation de la nouvelle fenêtre (Stage) pour les prélèvements
            this.cmStage = new Stage();
            this.cmStage.initModality(Modality.WINDOW_MODAL);  // La fenêtre est modale
            this.cmStage.initOwner(_parentStage);  // Définition de la fenêtre parente
            StageManagement.manageCenteringStage(_parentStage, this.cmStage);  // Gestion du centrage de la fenêtre
            this.cmStage.setScene(scene);  // Définition de la scène pour la fenêtre
            this.cmStage.setTitle("Gestion des prélèvements automatiques");
            this.cmStage.setResizable(false);

            // Récupération du contrôleur associé à la vue
            this.pmViewController = loader.getController();
            this.pmViewController.initContext(this.cmStage, this, _dbstate, client, cpt);  // Initialisation du contexte du contrôleur

            // Affichage de la fenêtre et attente de sa fermeture
            this.cmStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la boîte de dialogue de gestion des prélèvements.
     * <p>
     * Cette méthode appelle le contrôleur de la vue pour afficher la boîte de dialogue de gestion des prélèvements pour le compte courant édité.
     * </p>
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    public void doPrelevementManagementDialog() {
        this.pmViewController.displayDialog(compteEdite);
    }
}
