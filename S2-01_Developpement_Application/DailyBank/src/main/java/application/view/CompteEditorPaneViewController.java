package application.view;

import java.util.Locale;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

/**
 * Contrôleur de vue pour l'édition et la gestion des comptes courants dans l'application DailyBank.
 * Permet de créer, modifier ou supprimer des comptes courants associés à des clients.
 *
 * @version 1.0
 * 
 * @see application.DailyBankState
 * @see application.tools.AlertUtilities
 * @see application.tools.ConstantesIHM
 * @see application.tools.EditionMode
 * @see model.data.Client
 * @see model.data.CompteCourant
 */
public class CompteEditorPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique où est la scène contenant le fichier XML contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private EditionMode editionMode;
    private Client clientDuCompte;
    private CompteCourant compteEdite;
    private CompteCourant compteResultat;

    /**
     * Initialise le contexte du contrôleur.
     *
     * @param _containingStage La fenêtre contenant le contrôleur
     * @param _dbstate         L'état courant de l'application DailyBank
     * 
     * @see application.DailyBankState
     */
    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.txtDecAutorise.focusedProperty().addListener((t, o, n) -> this.focusDecouvert(t, o, n));
        this.txtSolde.focusedProperty().addListener((t, o, n) -> this.focusSolde(t, o, n));
        this.ON.selectedToggleProperty().addListener((t, o, n) -> this.focusIsOpen(t, o, n));
    }

    /**
     * Affiche la fenêtre d'édition de compte courant en mode attente.
     * 
     * @param client Le client associé au compte
     * @param cpte   Le compte courant à éditer
     * @param mode   Le mode d'édition (création, modification, suppression)
     * @return Le compte courant édité ou null en cas d'annulation ou d'erreur
     * 
     * @see #doCancel()
     * @see #doAjouter()
     * @see application.tools.AlertUtilities#showAlert(Stage, String, String, String, AlertType)
     */
    public CompteCourant displayDialog(Client client, CompteCourant cpte, EditionMode mode) {

        this.clientDuCompte = client;
        this.editionMode = mode;

        if (cpte == null) {
            this.compteEdite = new CompteCourant(0, 200, 0, "N", this.clientDuCompte.idNumCli);
        } else {
            this.compteEdite = new CompteCourant(cpte);
        }

        this.compteResultat = null;
        this.txtIdclient.setDisable(true);
        this.txtIdAgence.setDisable(true);
        this.txtIdNumCompte.setDisable(true);
        this.btnCloture.setDisable(true);
        this.btnOuvert.setDisable(true);
        switch (mode) {
            case CREATION:
                this.txtDecAutorise.setDisable(false);
                this.txtSolde.setDisable(false);
                this.lblMessage.setText("Informations sur le nouveau compte");
                this.lblSolde.setText("Solde (premier dépôt)");
                this.btnOk.setText("Ajouter");
                this.btnCancel.setText("Annuler");
                break;
            case MODIFICATION:
                this.txtDecAutorise.setDisable(false);
                this.txtSolde.setDisable(true);
                this.lblMessage.setText("Informations sur le compte à modifier");
                this.lblSolde.setText("Solde");
                this.btnCloture.setDisable(false);
                this.btnOuvert.setDisable(false);
                this.btnOk.setText("Modifier");
                this.btnCancel.setText("Annuler");
                break;
            case SUPPRESSION:
                AlertUtilities.showAlert(this.containingStage, "Non implémenté", "Suppression de compte n'est pas implémenté",
                        null, AlertType.ERROR);
                return null;
        }

        // Paramétrages spécifiques pour les chefs d'agences
        if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
            // rien pour l'instant
        }

        // initialisation du contenu des champs
        this.txtIdclient.setText("" + this.compteEdite.idNumCli);
        this.txtIdNumCompte.setText("" + this.compteEdite.idNumCompte);
        this.txtIdAgence.setText("" + this.dailyBankState.getEmployeActuel().idAg);
        this.txtDecAutorise.setText("" + Math.abs(this.compteEdite.debitAutorise));
        this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde));

        this.compteResultat = null;

        this.containingStage.showAndWait();
        return this.compteResultat;
    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    private Object focusDecouvert(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
            boolean newPropertyValue) {
        if (oldPropertyValue) {
            try {
                int val;
                val = Integer.parseInt(this.txtDecAutorise.getText().trim());
                if (val < 0) {
                    throw new NumberFormatException();
                }
                this.compteEdite.debitAutorise = val;
            } catch (NumberFormatException nfe) {
                this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
            }
        }
        return null;
    }

    private Object focusSolde(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
            boolean newPropertyValue) {
        if (oldPropertyValue) {
            try {
                double val;
                val = Double.parseDouble(this.txtSolde.getText().trim());
                if (val < 0) {
                    throw new NumberFormatException();
                }
                this.compteEdite.solde = val;
            } catch (NumberFormatException nfe) {
                this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde));
            }
        }
        this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde));
        return null;
    }

    private Object focusIsOpen(ObservableValue<? extends Toggle> tValue, Toggle oldToggle, Toggle newToggle) {
        if (newToggle != null) {

            RadioButton selectedButton = (RadioButton) newToggle;
            System.out.println(selectedButton.getText());
            this.compteEdite.estCloture = selectedButton.getText().equals("Ouvert") ? "N" : "O";
            System.out.println(this.compteEdite.estCloture);
        }

        return null;

    }

    // Attributs de la scene + actions
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblSolde;
    @FXML
    private TextField txtIdclient;
    @FXML
    private TextField txtIdAgence;
    @FXML
    private TextField txtIdNumCompte;
    @FXML
    private TextField txtDecAutorise;
    @FXML
    private TextField txtSolde;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private RadioButton btnOuvert;
    @FXML
    private RadioButton btnCloture;
    @FXML
    private ToggleGroup ON;

    /**
     * Action exécutée lors du clic sur le bouton "Annuler".
     * Annule l'édition ou la création du compte et ferme la fenêtre.
     * 
     * @see #closeWindow(WindowEvent)
     */
    @FXML
    private void doCancel() {
        this.compteResultat = null;
        this.containingStage.close();
    }

    /**
     * Action exécutée lors du clic sur le bouton "Ajouter" ou "Modifier".
     * Valide la saisie et enregistre les modifications du compte.
     * 
     * @see #displayDialog(Client, CompteCourant, EditionMode)
     */
    @FXML
    private void doAjouter() {
        switch (this.editionMode) {
            case CREATION:
                if (this.isSaisieValide()) {
                    this.compteResultat = this.compteEdite;
                    this.containingStage.close();
                }
                break;
            case MODIFICATION:
                if (this.isSaisieValide()) {
                    this.compteResultat = this.compteEdite;
                    this.containingStage.close();
                }
                break;
            case SUPPRESSION:
                this.compteResultat = this.compteEdite;
                this.containingStage.close();
                break;
        }

    }

    /**
     * Vérifie si la saisie actuelle dans les champs de texte est valide.
     *
     * @return true si la saisie est valide, sinon false
     */
    private boolean isSaisieValide() {
        // TESTS pour savoir si la saisie est valide
        return true;
    }

    // GETTERS

    /**
     * Récupère l'identifiant du compte édité.
     *
     * @return L'identifiant du compte
     */
    public int getIdNumCompte() {
        return this.compteEdite.idNumCompte;
    }

    /**
     * Récupère le débit autorisé du compte édité.
     *
     * @return Le débit autorisé du compte
     */
    public int getDecAutorise() {
        return this.compteEdite.debitAutorise;
    }

    /**
     * Récupère le solde du compte édité.
     *
     * @return Le solde du compte
     */
    public double getSolde() {
        return this.compteEdite.solde;
    }
}

