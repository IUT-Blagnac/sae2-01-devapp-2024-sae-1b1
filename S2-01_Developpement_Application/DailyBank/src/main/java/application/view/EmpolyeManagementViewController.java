package application.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import application.DailyBankState;
import application.control.EmployeManagement;
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
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmpolyeManagementViewController {
    private DailyBankState dailyBankState;

    private Stage containingStage;
   
    private ObservableList<Employe> oListEmploye;
    private EmployeManagement cmDialogController;
    //private ObservableList<Object> oListEmployer;
    private EmployeManagement cEmpolyerManagement;
    private Stage stage;
    private ObservableList<Employe> ListEmploye;
    
    
    public void initContext(Stage _containingStage, EmployeManagement cEmpManag, DailyBankState _dbstate){
		this.cmDialogController=cEmpManag;
		this.containingStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
  
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
	private ListView<Employe> lvEmploye;
	@FXML
	private Button btnDesactEmployeButton;
	@FXML
	private Button btnModifEmployeButton;
	@FXML
	private Button btnComptesEmployeButton;

	@FXML
	private void doCancel() {
		this.containingStage.close();
	}

	@FXML
	private void doRechercher() throws DatabaseConnexionException, ApplicationException {
		int idEmploye;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				idEmploye = -1;
			} else {
				idEmploye = Integer.parseInt(nc);
				if (idEmploye < 0) {
					this.txtNum.setText("");
					idEmploye = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			idEmploye = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (idEmploye != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des EMPLOYER en BD. cf. AccessClient > getClients(.)
		// idEmploye != -1 => recherche sur idEmploye
		// idEmploye != -1 et debutNom non vide => recherche nom/prenom
		// idEmploye != -1 et debutNom vide => recherche tous les clients
		ArrayList<Employe> listeEmploye;
		listeEmploye = this.cmDialogController.getlisteEmployes(idEmploye, debutNom, debutPrenom);

	    this.oListEmploye.clear();
		this.oListEmploye.addAll(listeEmploye);
		this.validateComponentState();
	}


@FXML
	private void doModifierEmploye() throws DatabaseConnexionException, ApplicationException {

		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe empMod = this.oListEmploye.get(selectedIndice);
			Employe result = this.cmDialogController.modifierEmploye(empMod);
			if (result != null) {
				this.oListEmploye.set(selectedIndice, result);
			}
		}
	}


	@FXML
	private void doDesactiverEmploye() {
	}

	@FXML
	private void doNouveauEmploye() throws DatabaseConnexionException, ApplicationException {
		Employe employe;
		employe = this.cmDialogController.nouveauEmploye();
		if (employe != null) {
			this.oListEmploye.add(employe);
		}
	}

	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnDesactEmployeButton.setDisable(true);
		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifEmployeButton.setDisable(false);
			this.btnComptesEmployeButton.setDisable(false);
		} else {
			this.btnModifEmployeButton.setDisable(true);
			this.btnComptesEmployeButton.setDisable(true);
		}
	}


 

	private void configure(){
			this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
			this.oListEmploye= FXCollections.observableArrayList();
			this.lvEmploye.setItems(this.oListEmploye);
			this.lvEmploye.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			this.lvEmploye.getFocusModel().focus(-1);
			this.lvEmploye.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
			this.validateComponentState();

	}
    
}

