package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmpolyeManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagement{

	private Stage cmStage;
	private DailyBankState dailyBankState;
	private EmpolyeManagementViewController cmViewController;

	public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
		
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmpolyeManagementViewController.class.getResource("employemanagement.fxml"));
			BorderPane root = loader.load();
			if(root==null){
				System.out.println("probleme");
			}

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.cmStage = new Stage();
			this.cmStage.initModality(Modality.WINDOW_MODAL);
			this.cmStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.cmStage);
			this.cmStage.setScene(scene);
			this.cmStage.setTitle("Gestion des employer");
			this.cmStage.setResizable(false);

			this.cmViewController = loader.getController();
			this.cmViewController.initContext(this.cmStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doEmployeManagementDialog() {
		this.cmViewController.displayDialog();
	}

	
	public Employe modifierEmploye(Employe em) throws DatabaseConnexionException, ApplicationException {
		EmployeEditorPane emp = new EmployeEditorPane(cmStage, dailyBankState);
		Employe result = emp.doEmployeEditorDialog(em, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				Access_BD_Employe ac = new Access_BD_Employe();
				ac.updateEmploye(result);
			} catch(DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				result = null;
				this.cmStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}

	public boolean supprimerEmploye(Employe emp){
		try {
			Access_BD_Employe ac = new Access_BD_Employe();
			ac.deleteEmploye(emp);
			return true;
		} catch (DatabaseConnexionException e){
			ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			emp = null;
			return false;
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			emp = null;
			return false;

		}

	}

	public Employe nouveauEmploye() throws DatabaseConnexionException, ApplicationException {
		Employe employe;
		EmployeEditorPane emp = new EmployeEditorPane(this.cmStage, this.dailyBankState);
		employe = emp.doEmployeEditorDialog( null, EditionMode.CREATION);
		if (employe != null) {
			Access_BD_Employe ac = new Access_BD_Employe();

			ac.insertEmploye(employe);
		}
		return employe;
	
	
}
public ArrayList<Employe> getlisteEmployes(int _idEmploye, String _debutNom, String _debutPrenom) throws DatabaseConnexionException, ApplicationException {
	ArrayList<Employe> listeEmp = new ArrayList<>();
	Access_BD_Employe ac = new Access_BD_Employe();
	listeEmp = ac.getEmploye(this.dailyBankState.getEmployeActuel().idAg, _idEmploye, _debutNom, _debutPrenom);
	return listeEmp;
}
}

