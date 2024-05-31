package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.DestPickerPaneViewController;
import application.view.OperationEditorPaneViewController;
import application.view.VirementEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_Client;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class DestPickerPane {

	private DailyBankState dailyBankState;
	private Stage dppStage;
	private DestPickerPaneViewController dppViewController;

	public DestPickerPane(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState=_dbstate;

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationEditorPaneViewController.class.getResource("destpicker.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 850, 410);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.dppStage = new Stage();
			this.dppStage.initModality(Modality.WINDOW_MODAL);
			this.dppStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.dppStage);
			this.dppStage.setScene(scene);
			this.dppStage.setTitle("Choix du destinataire du virement");
			this.dppStage.setResizable(false);

			this.dppViewController = loader.getController();
			this.dppViewController.initContext(this.dppStage,this, _dbstate );

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CompteCourant chooseDestDialog() {
		return this.dppViewController.displayDialog();
	}

	public ArrayList<CompteCourant> getComptesDunClient(Client cli) {
		ArrayList<CompteCourant> listeCptTemp = new ArrayList<CompteCourant>();
		ArrayList<CompteCourant> listeCptDef=new ArrayList<CompteCourant>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCptTemp = acc.getCompteCourants(cli.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.dppStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.dppStage.close();
			listeCptTemp = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.dppStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCptTemp = new ArrayList<>();
		}
		

		Stage stage = dppStage;
		DailyBankState dbState=this.dailyBankState;
		int idLastClient=-1;

		for(CompteCourant cpt : listeCptTemp){
			if(cpt.idNumCli!=idLastClient){
				idLastClient=cpt.idNumCli;
				listeCptDef.add(new CompteCourant(cpt){
					@Override
					public String toString(){
						String result;
						Client cli;

						try {
							Access_BD_Client acc = new Access_BD_Client();
							cli = acc.getClient(cpt.idNumCli);
						} catch (DatabaseConnexionException e) {
							ExceptionDialog ed = new ExceptionDialog(stage, dbState, e);
							ed.doExceptionDialog();
							stage.close();
							cli = null;
						} catch (ApplicationException ae) {
							ExceptionDialog ed = new ExceptionDialog(stage, dbState, ae);
							ed.doExceptionDialog();
							cli=new Client();
						}

						result="[" + cli.idNumCli + "]  " +  cli.nom.toUpperCase() + " " + cli.prenom + "(" + cli.email + "): \n	";

						return result+super.toString();
					}
				});
			} else
				listeCptDef.add(new CompteCourant(cpt){
					@Override
					public String toString(){
						return "	" + super.toString();
					}
				});
		}

		return listeCptDef;
	}

	public ArrayList<Client> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Client> listeCli = new ArrayList<>();
		try {
			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte == -1 et debutNom non vide => recherche nom/prenom
			// numCompte == -1 et debutNom vide => recherche tous les clients

			Access_BD_Client ac = new Access_BD_Client();
			listeCli = ac.getClients(this.dailyBankState.getEmployeActuel().idAg, _numCompte, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.dppStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.dppStage.close();
			listeCli = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.dppStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCli = new ArrayList<>();
		}
		return listeCli;
	}
}
