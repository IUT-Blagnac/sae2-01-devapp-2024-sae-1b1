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

public class VirementEditorPaneViewController {

    // Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage containingStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.containingStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;
		String info;

		info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
		this.lblMessage.setText(info);

		this.btnOk.setText("Effectuer Virement");
		this.btnCancel.setText("Annuler Virement");	

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.operationResultat = null;

		this.containingStage.showAndWait();
		return this.operationResultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

    @FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
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

	/*@FXML
	private void doAjouter() {	
		double montant;
		String info;
		String typeOp;

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
	}*/
}
