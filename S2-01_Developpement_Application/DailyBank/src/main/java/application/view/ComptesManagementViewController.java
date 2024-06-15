package application.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.PrelevementsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import application.tools.AlertUtilities;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

/**
 * Contrôleur de vue pour la gestion des comptes d'un client.
 * Cette classe gère l'interface utilisateur et les interactions associées avec les comptes courants d'un client.
 * Elle permet d'afficher les comptes, de modifier, supprimer et créer de nouveaux comptes.
 * Elle gère également l'affichage et la gestion des opérations et prélèvements liés à chaque compte.
 *
 * @see application.view
 */
public class ComptesManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à ComptesManagementController
    private ComptesManagement cmDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private Client clientDesComptes;
    private ObservableList<CompteCourant> oListCompteCourant;

    /**
     * Initialise le contexte du contrôleur avec la fenêtre principale et les données nécessaires.
     *
     * @param _containingStage La fenêtre principale contenant la scène gérée par ce contrôleur
     * @param _cm              Le contrôleur des dialogues pour la gestion des comptes
     * @param _dbstate         L'état courant de l'application bancaire
     * @param client           Le client dont les comptes sont gérés par cette fenêtre
     */
    public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
        this.cmDialogController = _cm;
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.clientDesComptes = client;
        this.configure();
    }

    private void configure() {
        String info;

        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.oListCompteCourant = FXCollections.observableArrayList();
        this.lvComptes.setItems(this.oListCompteCourant);
        this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvComptes.getFocusModel().focus(-1);
        this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

        info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : " + this.clientDesComptes.idNumCli + ")";
        this.lblInfosClient.setText(info);

        this.loadList();
        this.validateComponentState();
    }

    /**
     * Affiche la fenêtre de gestion des comptes et attend que l'utilisateur la ferme.
     */
    public void displayDialog() {
        this.containingStage.showAndWait();
    }

    // Gestion du stage

    /**
     * Gère l'événement de fermeture de la fenêtre en annulant toute opération en cours.
     *
     * @param e L'événement de fermeture de la fenêtre
     * @return null
     */
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene + actions

    @FXML
    private Label lblInfosClient;
    @FXML
    private ListView<CompteCourant> lvComptes;
    @FXML
    private Button btnVoirOpes;
    @FXML
    private Button btnModifierCompte;
    @FXML
    private Button btnSupprCompte;
    @FXML
    private Button btnVoirPrelev;

    /**
     * Gère l'événement de clic sur le bouton d'annulation.
     * Ferme simplement la fenêtre de gestion des comptes.
     */
    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    /**
     * Gère l'événement de clic sur le bouton pour voir les opérations du compte sélectionné.
     * Récupère le compte courant sélectionné et ouvre la boîte de dialogue pour gérer les opérations du compte.
     */
    @FXML
    private void doVoirOperations() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
            this.cmDialogController.gererOperationsDUnCompte(cpt);
        }
        this.loadList();
        this.validateComponentState();
    }

    /**
     * Gère l'événement de clic sur le bouton pour voir les prélèvements du compte sélectionné.
     * Récupère le compte courant sélectionné et ouvre la boîte de dialogue pour gérer les prélèvements du compte.
     */
    @FXML
    private void doVoirPrelevements() {
        // Récupération de l'indice du compte sélectionné dans la ListView
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();

        // Vérifie si un compte est effectivement sélectionné
        if (selectedIndice >= 0) {
            // Récupération du compte courant à l'indice sélectionné
            CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);

            // Création d'une instance de PrelevementsManagement pour gérer les prélèvements du compte sélectionné
            PrelevementsManagement p = new PrelevementsManagement(this.containingStage, this.dailyBankState,
                    this.clientDesComptes, cpt);

            // Ouverture de la boîte de dialogue de gestion des prélèvements
            p.doPrelevementManagementDialog();
        }
    }

    /**
     * Gère l'événement de clic sur le bouton pour modifier le compte sélectionné.
     * Récupère le compte courant sélectionné et ouvre la boîte de dialogue pour modifier ses détails.
     * Affiche une alerte d'erreur si aucun compte n'est sélectionné.
     */
    @FXML
    private void doModifierCompte() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
            this.cmDialogController.editerCompte(cpt);
        } else {
            // Alerte théoriquement inatteignable
            AlertUtilities.showAlert(this.containingStage, "Erreur de sélection", "Aucun compte ne semble sélectionné!",
                    "Vous devez sélectionner un compte à supprimer!!", AlertType.ERROR);
        }
    }

    /**
     * Gère l'événement de clic sur le bouton pour supprimer le compte sélectionné.
     * Récupère le compte courant sélectionné et demande confirmation à l'utilisateur avant de le supprimer.
     * Affiche une alerte d'erreur si aucun compte est sélectionné.
     */
    @FXML
    private void doSupprimerCompte() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            if (AlertUtilities.confirmYesCancel(containingStage, "Suppression de compte", "Le compte ne pourra pas être retrouvé",
                    "Voulez-vous réellement le supprimer?", AlertType.CONFIRMATION)) {
                CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
                this.cmDialogController.supprimerCompte(cpt);
            }
        } else {
            // Alerte théoriquement inatteignable
            AlertUtilities.showAlert(this.containingStage, "Erreur de sélection", "Aucun compte ne semble sélectionné!",
                    "Vous devez sélectionner un compte à supprimer!!", AlertType.ERROR);
        }

    }

    /**
     * Gère l'événement de clic sur le bouton pour créer un nouveau compte pour le client actuel.
     * Ouvre la boîte de dialogue pour créer un nouveau compte.
     */
    @FXML
    private void doNouveauCompte() {
        this.cmDialogController.creerNouveauCompte();
    }

    private void loadList() {
        ArrayList<CompteCourant> listeCpt;
        listeCpt = this.cmDialogController.getComptesDunClient();
        this.oListCompteCourant.clear();
        this.oListCompteCourant.addAll(listeCpt);
    }

    private void validateComponentState() {

        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifierCompte.setDisable(false);
            this.btnSupprCompte.setDisable(false);
            this.btnVoirOpes.setDisable(false);
            this.btnVoirPrelev.setDisable(false);
        } else {
            this.btnModifierCompte.setDisable(true);
            this.btnSupprCompte.setDisable(true);
            this.btnVoirOpes.setDisable(true);
            this.btnVoirPrelev.setDisable(true);
        }
    }

    /**
     * Met à jour la liste des comptes affichée pour refléter les changements.
     */
    public void reloadList() {
        this.loadList();
    }

}
