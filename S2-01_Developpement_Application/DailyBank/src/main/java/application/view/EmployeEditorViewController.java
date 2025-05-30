package application.view;

import java.util.regex.Pattern;

import application.DailyBankApp;
import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

/**
 * Contrôleur JavaFX pour la vue EmployeEditorView, responsable de l'édition des informations d'un employé.
 * Cette classe gère l'interaction utilisateur pour la création, modification ou suppression des données d'un employé.
 *
 * @see application.DailyBankState
 * @see application.tools.EditionMode
 * @see application.control.ExceptionDialog
 * @see application.tools.AlertUtilities
 * @see application.tools.ConstantesIHM
 * @see model.data.Employe
 */
public class EmployeEditorViewController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage containingStage;

	// Données de la fenêtre

	private Employe employeEdite;
	private EditionMode editionMode;
	private Employe employeResultat;


    /**
     * Initialise le contexte du contrôleur de vue EmployeEditorViewController.
     *
     * @param _containingStage Stage qui contient le fichier XML contrôlé par EmployeEditorViewController
     * @param dailyBankState2 Etat courant de l'application
     */
	public void initContext(Stage _containingStage, DailyBankState dailyBankState2) {
		this.containingStage = _containingStage;
		this.dailyBankState = dailyBankState2;
		this.configure();
	}

	
	private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	 /**
     * Affiche la fenêtre d'édition d'un employé en fonction du mode spécifié (CRÉATION, MODIFICATION, SUPPRESSION).
     * Cette méthode permet à l'utilisateur de saisir ou de modifier les informations d'un employé.
     *
     * @param employe Employé à éditer (null si création d'un nouvel employé)
     * @param mode    Mode d'édition (CRÉATION, MODIFICATION, SUPPRESSION)
     * @return Employé résultat après édition, null si aucune action n'est réalisée
     */
	public Employe displayDialog(Employe employe, EditionMode mode) {

		this.editionMode = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "guichetier", "", "", this.dailyBankState.getEmployeActuel().idAg);
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeResultat = null;
		switch (mode) {
		case CREATION:
			this.txtIdempl.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLog.setDisable(false);
			this.txtMdp.setDisable(false);
			this.txtAg.setDisable(false);
			this.lblMessage.setText("Informations sur le nouveau employe");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
		this.txtIdempl.setDisable(true);
		this.txtNom.setDisable(false);
		this.txtPrenom.setDisable(false);
		this.txtLog.setDisable(false);
		this.txtMdp.setDisable(false);
		this.txtAg.setDisable(false);
		this.lblMessage.setText("Informations sur le nouveau employe");
		this.butOk.setText("Ajouter");
		this.butCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Employer:
			// la suppression d'un employer n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE",
					null);
			ExceptionDialog ed = new ExceptionDialog(this.containingStage, this.dailyBankState, ae);
			ed.doExceptionDialog();

			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}
		// initialisation du contenu des champs

		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtIdempl.setText(""+this.employeEdite.idEmploye);
		this.txtAg.setText(""+this.employeEdite.idAg);
		this.txtMdp.setText(this.employeEdite.motPasse);
		this.txtLog.setText(this.employeEdite.login);

		this.employeResultat= null;

		this.containingStage.showAndWait();
		return this.employeResultat;
	}

	/**
     * Gestion de la fermeture de la fenêtre par l'utilisateur.
     *
     * @param e Evénement associé à la fermeture de la fenêtre
     * @return null toujours (inutilisé)
     */
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdempl;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtAg;
	@FXML
	private TextField txtLog;
	@FXML
	private TextField txtMdp;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	 /**
     * Action exécutée lors du clic sur le bouton Annuler.
     * Réinitialise les champs et ferme la fenêtre d'édition.
     */
	@FXML
	private void doCancel() {
		this.employeResultat = null;
		this.containingStage.close();
	}

	/**
     * Action exécutée lors du clic sur le bouton Ajouter.
     * Valide la saisie et effectue l'ajout, la modification ou la suppression de l'employé.
     */
	@FXML
	private void doAjouter() {
		switch (this.editionMode) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResultat = this.employeEdite;
				this.containingStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResultat = this.employeEdite;
				this.containingStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResultat = this.employeEdite;
			this.containingStage.close();
			break;
		}

	}

	/**
     * Vérifie la validité de la saisie des données de l'employé.
     *
     * @return true si la saisie est valide, false sinon
     */
	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLog.getText().trim();
		this.employeEdite.motPasse = this.txtMdp.getText().trim();
	

		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.containingStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.containingStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}

		

		
		if (this.employeEdite.motPasse.isEmpty()&& (this.employeEdite.motPasse.length() < 50)) {
			AlertUtilities.showAlert(this.containingStage, "Erreur de saisie", null, "Le mot de passe n'est pas valable",
					AlertType.WARNING);
			this.txtMdp.requestFocus();
			return false;
		}
		
		if (this.employeEdite.login.isEmpty()&& (this.employeEdite.login.length() > 2)) {
			AlertUtilities.showAlert(this.containingStage, "Erreur de saisie", null, "Le login n'est pas valable",
					AlertType.WARNING);
			this.txtMdp.requestFocus();
			return false;
		}

		return true;
	}

   
}

