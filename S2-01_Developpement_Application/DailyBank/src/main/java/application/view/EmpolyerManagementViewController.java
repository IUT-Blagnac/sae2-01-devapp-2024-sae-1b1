package application.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import application.DailyBankState;
import application.control.EmployerManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import model.data.Employe;

public class EmpolyerManagementViewController {
    private DailyBankState dailyBankState;
    private EmployerManagement cEmpolyerManagement;
    private Stage containingStage;
   
    private ObservableList<Employe> oListEmployer;
    private EmployerManagement cmDialogController;
    //private ObservableList<Object> oListEmployer;
    
    
    public void initContext(Stage _containingStage, EmployerManagement cEmpManag, DailyBankState _dbstate){
		this.cmDialogController=cEmpManag;
		this.containingStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
    }

	private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.oListEmployer= FXCollections.observableArrayList();
		this.lvEmployer.setItems(this.oListEmployer);
		this.lvEmployer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmployer.getFocusModel().focus(-1);
		this.lvEmployer.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	public void displayDialog() {
		this.containingStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene

	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Employe> lvEmployer;
	@FXML
	private Button btnDesactEmployerButton;
	@FXML
	private Button btnModifEmployerButton;
	@FXML
	private Button btnComptesEmployerButton;

	@FXML
	private void doCancel() {
		this.containingStage.close();
	}

	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des EMPLOYER en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Employe> listeEmployer;
		listeEmployer = this.cmDialogController.getlisteComptes(numCompte, debutNom, debutPrenom);

	    this.oListEmployer.clear();
		this.oListEmployer.addAll(listeEmployer);
		this.validateComponentState();
	}

	@FXML
	private void doComptesEmployer() {
		int selectedIndice = this.lvEmployer.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe employe = this.oListEmployer;
			this.cmDialogController.gererCompte(employe);
		}
	}

	@FXML
	private void doModifierEmployer() {

		int selectedIndice = this.lvEmployer.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe empMod = this.oListEmployer.get();
			Employe result = this.cmDialogController.modifierEmployer(empMod);
			if (result != null) {
				this.oListEmployer.set(selectedIndice, result);
			}
		}
	}

	@FXML
	private void doDesactiverEmployer() {
	}

	@FXML
	private void doNouveauEmployer() {
		Employe employer;
		employer = this.cmDialogController.nouveauEmploye();
		if (employer != null) {
			this.oListEmployer.add(employer);
		}
	}

	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnDesactEmployerButton.setDisable(true);
		int selectedIndice = this.lvEmployer.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifEmployerButton.setDisable(false);
			this.btnComptesEmployerButton.setDisable(false);
		} else {
			this.btnModifEmployerButton.setDisable(true);
			this.btnComptesEmployerButton.setDisable(true);
		}
	}
}
    

