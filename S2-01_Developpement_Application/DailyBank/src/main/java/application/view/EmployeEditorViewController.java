package application.view;

import java.util.regex.Pattern;

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

public class EmployeEditorViewController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage containingStage;

	// Données de la fenêtre

	private Employe employeEdite;
	private EditionMode editionMode;
	private Employe employeResultat;

	// Manipulation de la fenêtre

	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.containingStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public Employe displayDialog(Employe employe, EditionMode mode) {

		this.editionMode = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dailyBankState.getEmployeActuel().idAg);
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeEdite = null;
		switch (mode) {
		case CREATION:
			this.txtIdempl.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLog.setDisable(false);
			this.txtMdp.setDisable(false);
			
		
			this.lblMessage.setText("Informations sur le nouveau employer");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtIdempl.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtDac.setDisable(false);
			this.txtMdp.setDisable(false);
			this.lblMessage.setText("Informations employer");
			this.butOk.setText("Modifier");
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
		this.txtDac.setText(this.employeEdite.droitsAccess);
		this.txtMdp.setText(this.employeEdite.motPasse);
		this.txtLog.setText(this.employeEdite.login);

		this.employeResultat= null;

		this.containingStage.showAndWait();
		return this.employeResultat;
	}

	// Gestion du stage
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
	private TextField txtDac;
	@FXML
	private TextField txtLog;
	@FXML
	private TextField txtMdp;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@FXML
	private void doCancel() {
		this.employeResultat = null;
		this.containingStage.close();
	}

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

	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.droitsAccess = this.txtDac.getText().trim();
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

		String regex = "[A-Za-z0-9-]{8}";
		if (!Pattern.matches(regex, this.employeEdite.motPasse) || this.employeEdite.motPasse.length() > 10) {
			AlertUtilities.showAlert(this.containingStage, "Erreur de saisie", null, "Le mot de passe n'est pas valable",
					AlertType.WARNING);
			this.txtMdp.requestFocus();
			return false;
		}
		regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		if (!Pattern.matches(regex, this.employeEdite.login) || this.employeEdite.login.length() > 20) {
			AlertUtilities.showAlert(this.containingStage, "Erreur de saisie", null, "Le login n'est pas valable",
					AlertType.WARNING);
			this.txtMdp.requestFocus();
			return false;
		}

		return true;
	}
}