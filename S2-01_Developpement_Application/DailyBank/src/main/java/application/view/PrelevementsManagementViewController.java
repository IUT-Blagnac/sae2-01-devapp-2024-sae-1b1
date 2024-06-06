package application.view;

import application.DailyBankState;
import application.control.PrelevementEditorPane;
import application.control.PrelevementsManagement;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

import java.util.Locale;

/**
 * Contrôleur pour la gestion des prélèvements automatiques.
 * <p>
 * Cette classe gère l'interface utilisateur pour afficher, ajouter, modifier et supprimer les prélèvements automatiques d'un compte courant.
 * </p>
 *
 * <p>Les principales fonctionnalités de ce contrôleur incluent l'affichage des informations client et compte, la gestion des événements
 * de bouton pour ajouter, modifier et supprimer des prélèvements, ainsi que le chargement initial et la mise à jour de la liste des prélèvements
 * automatiques à partir de la base de données.</p>
 *
 * @author Yassir BOULOUIHA GNAOUI
 * @see PrelevementEditorPane
 * @see PrelevementsManagement
 * @see AlertUtilities
 */
public class PrelevementsManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;
    // Contrôleur de Dialogue associé à PrelevementsManagement
    private PrelevementsManagement pmDialogController;
    // Fenêtre physique où se trouve la scène contenant le fichier XML contrôlé par cette classe
    private Stage containingStage;
    // Données de la fenêtre
    private Client clientDuCompte;
    private CompteCourant compteConcerne;
    private ObservableList<Prelevement> oListPrelevements;

    /**
     * Initialise le contexte de la fenêtre de gestion des prélèvements automatiques.
     *
     * @param _containingStage La fenêtre parente contenant cette scène.
     * @param _pm Le contrôleur de gestion des prélèvements automatiques.
     * @param _dbstate L'état courant de l'application bancaire.
     * @param client Le client dont les comptes sont gérés.
     * @param compte Le compte courant concerné par les prélèvements.
     */
    public void initContext(Stage _containingStage, PrelevementsManagement _pm, DailyBankState _dbstate, Client client, CompteCourant compte) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.pmDialogController = _pm;
        this.clientDuCompte = client;
        this.compteConcerne = compte;
        this.configure();

        this.displayDialog(compte);
    }

    /**
     * Configure les composants de la fenêtre.
     * <p>
     * Cette méthode initialise les composants de l'interface, configure les écouteurs d'événements et charge la liste des prélèvements.
     * </p>
     */
    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.oListPrelevements = FXCollections.observableArrayList();
        this.lvPrelevements.setItems(this.oListPrelevements);
        this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevements.getFocusModel().focus(-1);
        this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

        this.loadList();
        this.validateComponentState();
    }

    /**
     * Affiche la boîte de dialogue de gestion des prélèvements pour le compte courant donné.
     *
     * @param cpt Le compte courant concerné par les prélèvements.
     */
    public void displayDialog(CompteCourant cpt) {
        String info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli + ")";
        this.lblInfosClient.setText(info);

        info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
                + String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
                + String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
        this.lblInfosCompte.setText(info);
    }

    /**
     * Gestion de la fermeture de la fenêtre.
     * <p>
     * Cette méthode est appelée lors de la fermeture de la fenêtre et empêche la fermeture automatique.
     * </p>
     *
     * @param e L'événement de fermeture de fenêtre.
     * @return Toujours null.
     */
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scène + actions

    @FXML
    private Label lblInfosClient;
    @FXML
    private Label lblInfosCompte;
    @FXML
    private ListView<Prelevement> lvPrelevements;
    @FXML
    private Button btnSuprPrelev;
    @FXML
    private Button btnModifPrelev;
    @FXML
    private Button btnNouvPrelev;

    /**
     * Annule l'action en cours et ferme la fenêtre de gestion des prélèvements.
     */
    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    /**
     * Gère l'ajout d'un nouveau prélèvement.
     * @author Yassir BOULOUIHA GNAOUI
     */
    @FXML
    private void doNouveauPrelev() {

        if (this.compteConcerne.estCloture.equals("N")){
            PrelevementEditorPane pEditorPane = new PrelevementEditorPane(this.containingStage, this.dailyBankState, this,
                    this.compteConcerne);
            pEditorPane.doPrelevementEditorPaneDialog(EditionMode.CREATION,null);
        }else {
            AlertUtilities.showAlert(this.containingStage, "Action interdite",
                    "Vous ne pouvez pas établir un prélèvement automatiqué sur un compte clôturé",
                    "", Alert.AlertType.WARNING);
        }

    }

    /**
     * Gère la modification d'un prélèvement existant.
     */
    @FXML
    private void doModifierPrelev() {
        int selectedIndice=this.lvPrelevements.getSelectionModel().getSelectedIndex();

        if (this.compteConcerne.estCloture.equals("N")){
            PrelevementEditorPane pEditorPane = new PrelevementEditorPane(this.containingStage, this.dailyBankState, this,
                    this.compteConcerne);
            pEditorPane.doPrelevementEditorPaneDialog(EditionMode.MODIFICATION, this.lvPrelevements.getItems().get(selectedIndice));
        }else {
            AlertUtilities.showAlert(this.containingStage, "Action interdite",
                    "Vous ne pouvez pas modifier un prélèvement automatiqué sur un compte clôturé",
                    "", Alert.AlertType.WARNING);
        }

    }

    /**
     * Gère la suppression d'un prélèvement existant
     *
     * @author Titouan DELAPLAGNE
     */
    @FXML
    private void doSuprimmerPrelev() {
        int selectedIndice=this.lvPrelevements.getSelectionModel().getSelectedIndex();

        Access_BD_Prelevement aBd_Prelevement= new Access_BD_Prelevement();
        
        if(AlertUtilities.confirmYesCancel(this.containingStage, "Supprimer?", "Voulez vous vraiment supprimer ce prélevement", null, AlertType.CONFIRMATION)){
            try {
                aBd_Prelevement.deletePrelevement(this.lvPrelevements.getItems().get(selectedIndice));
            } catch (DataAccessException | DatabaseConnexionException e) {
                AlertUtilities.showAlert(this.containingStage,"Erreur de BD","Echec d'accès à la BD",e.getMessage(),AlertType.ERROR);
            }
            this.loadList();
        }
    }

    /**
     * Valide l'état des composants de l'interface en fonction de la sélection actuelle.
     */
    private void validateComponentState() {
        this.btnNouvPrelev.setDisable(false);
        int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifPrelev.setDisable(false);
            this.btnSuprPrelev.setDisable(false);
        } else {
            this.btnModifPrelev.setDisable(true);
            this.btnSuprPrelev.setDisable(true);
        }
    }

    /**
     * Charge la liste des prélèvements automatiques pour le compte courant concerné.
     * <p>
     * Cette méthode interroge la base de données pour récupérer les prélèvements et les affiche dans la liste.
     * </p>
     */
    public void loadList() {
        try {
            Access_BD_Prelevement access = new Access_BD_Prelevement();
            this.oListPrelevements.clear();
            this.oListPrelevements.addAll(access.getPrelevements(this.compteConcerne));
            this.lvPrelevements.setItems(this.oListPrelevements);
        } catch (DataAccessException e) {
            AlertUtilities.showAlert(containingStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue",
                    "Contactez l'administrateur de la base de données\nErreur : " + e.getMessage(), Alert.AlertType.ERROR);
            this.containingStage.close();
        } catch (DatabaseConnexionException e) {
            AlertUtilities.showAlert(containingStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue",
                    "Contactez l'administrateur de la base de données\nErreur : " + e.getMessage(), Alert.AlertType.ERROR);
            this.containingStage.close();
        }
    }
}
