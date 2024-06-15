package application.view;

import java.util.Locale;

import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * Contrôleur JavaFX pour la vue OperationEditorPaneView, responsable de la gestion des opérations bancaires.
 * Cette classe gère l'affichage de la fenêtre d'édition d'opération, la validation des données saisies,
 * et l'interaction avec l'utilisateur pour effectuer des opérations de crédit ou débit sur un compte bancaire.
 * Elle communique avec le modèle de données et l'état courant de l'application.
 * 
 * Cette classe utilise également les outils de l'application comme les catégories d'opération et les constantes IHM.
 * 
 * @author VotreNom
 * @version 1.0
 * @see application.DailyBankState
 * @see application.tools.CategorieOperation
 * @see application.tools.ConstantesIHM
 * @see model.data.CompteCourant
 * @see model.data.Operation
 */
public class OperationEditorPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique où est la scène contenant le fichier XML contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private CategorieOperation categorieOperation;
    private CompteCourant compteEdite;
    private Operation operationResultat;

    // Manipulation de la fenêtre
    /**
     * Initialise le contexte du contrôleur de vue OperationEditorPaneViewController.
     * 
     * @param _containingStage Stage qui contient le fichier XML contrôlé par OperationEditorPaneViewController
     * @param _dbstate         Etat courant de l'application
     */
    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

    /**
     * Affiche la fenêtre d'édition d'opération bancaire en fonction du type (débit ou crédit).
     * 
     * @param cpte Compte courant sur lequel l'opération sera effectuée
     * @param mode Mode de l'opération : débit ou crédit
     * @return L'opération résultante après validation et saisie des données, ou null si l'opération est annulée
     */
    public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
        this.categorieOperation = mode;
        this.compteEdite = cpte;
        String info;
        ObservableList<String> listTypesOpesPossibles;

        switch (mode) {
            case DEBIT:
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                this.btnOk.setText("Effectuer Débit");
                this.btnCancel.setText("Annuler débit");

                listTypesOpesPossibles = FXCollections.observableArrayList();
                listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_DEBIT_GUICHET);

                this.cbTypeOpe.setItems(listTypesOpesPossibles);
                this.cbTypeOpe.getSelectionModel().select(0);
                break;
            case CREDIT:
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                this.btnOk.setText("Effectuer Crédit");
                this.btnCancel.setText("Annuler crédit");

                listTypesOpesPossibles = FXCollections.observableArrayList();
                listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_CREDIT_GUICHET);

                this.cbTypeOpe.setItems(listTypesOpesPossibles);
                this.cbTypeOpe.getSelectionModel().select(0);
                break;
        }

        // Paramétrages spécifiques pour les chefs d'agences
        if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
            // rien pour l'instant
        }

        this.operationResultat = null;
        this.cbTypeOpe.requestFocus();

        this.containingStage.showAndWait();
        return this.operationResultat;
    }

    // Gestion du stage
    /**
     * Méthode de fermeture de la fenêtre.
     * 
     * @param e Evénement associé à la fermeture de la fenêtre
     * @return null toujours (inutilisé)
     */
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scène + actions

    @FXML
    private Label lblMessage;
    @FXML
    private Label lblMontant;
    @FXML
    private ComboBox<String> cbTypeOpe;
    @FXML
    private TextField txtMontant;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;

    @FXML
    private void doCancel() {
        this.operationResultat = null;
        this.containingStage.close();
    }

    @FXML
    private void doAjouter() {
        double montant;
        String info;
        String typeOp;

        switch (this.categorieOperation) {
            case DEBIT:
                // règles de validation d'un débit :
                // - le montant doit être un nombre valide
                // - et si l'utilisateur n'est pas chef d'agence,
                // - le débit ne doit pas amener le compte en dessous de son découvert autorisé

                this.txtMontant.getStyleClass().remove("borderred");
                this.lblMontant.getStyleClass().remove("borderred");
                this.lblMessage.getStyleClass().remove("borderred");
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                try {
                    montant = Double.parseDouble(this.txtMontant.getText().trim());
                    if (montant <= 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException nfe) {
                    this.txtMontant.getStyleClass().add("borderred");
                    this.lblMontant.getStyleClass().add("borderred");
                    this.txtMontant.requestFocus();
                    return;
                }
                if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
                    info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
                            + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                            + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                    this.lblMessage.setText(info);
                    this.txtMontant.getStyleClass().add("borderred");
                    this.lblMontant.getStyleClass().add("borderred");
                    this.lblMessage.getStyleClass().add("borderred");
                    this.txtMontant.requestFocus();
                    return;
                }
                typeOp = this.cbTypeOpe.getValue();
                this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
                this.containingStage.close();
                break;
            case CREDIT:
                this.txtMontant.getStyleClass().remove("borderred");
                this.lblMontant.getStyleClass().remove("borderred");
                this.lblMessage.getStyleClass().remove("borderred");
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                try {
                    montant = Double.parseDouble(this.txtMontant.getText().trim());
                    if (montant <= 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException nfe) {
                    this.txtMontant.getStyleClass().add("borderred");
                    this.lblMontant.getStyleClass().add("borderred");
                    this.txtMontant.requestFocus();
                    return;
                }

                typeOp = this.cbTypeOpe.getValue();

                this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCompte, typeOp);
                this.containingStage.close();
                break;
			}
		}
	}
	