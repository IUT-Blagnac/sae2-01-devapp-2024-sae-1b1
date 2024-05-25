package application.control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneViewController;
import application.view.ComptesManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.LogToDatabase;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class ComptesManagement {

	private Stage cmStage;
	private ComptesManagementViewController cmViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;
	private CompteEditorPaneViewController cepViewController;

	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {
		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementViewController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.cmStage = new Stage();
			this.cmStage.initModality(Modality.WINDOW_MODAL);
			this.cmStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.cmStage);
			this.cmStage.setScene(scene);
			this.cmStage.setTitle("Gestion des comptes");
			this.cmStage.setResizable(false);

			this.cmViewController = loader.getController();
			this.cmViewController.initContext(this.cmStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doComptesManagementDialog() {
		this.cmViewController.displayDialog();
	}

	public void gererOperationsDUnCompte(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.cmStage, this.dailyBankState,
				this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	public CompteCourant creerNouveauCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.cmStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		if (compte != null) {
			Connection con = null;
			Statement s = null;
			ResultSet result = null;
			try {
				//Initialisation de l'id (id de la bd) du nouveau compte à -1 pour gérer les erreurs 
				int newIdNumCompte = -1;

				// Connection à la base de données
				con = LogToDatabase.getConnexion();
				con.setAutoCommit(false); // Gestion manuelle des transactions

				// Creation du statement pour exectuter les requetes
				s = con.createStatement();

				// Récuperation d'un numero de compte qui peut être attribué à un nouveau compte
				result = s.executeQuery("SELECT (MAX(idnumcompte) + 1) AS ID_NOUV_COMPTE FROM COMPTECOURANT");

				// Vérifier que le ResultSet contient des données et placer le curseur sur la donnée presente 
				if (result.next()) {
					newIdNumCompte = result.getInt("ID_NOUV_COMPTE");
				} else {
					AlertUtilities.showAlert(cmStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue\nLe compte n'a pas été ajouté",
					 "Contactez l'administrateur de la base de données\nErreur : Données dans COMPTECOURANT inexistantes", AlertType.ERROR);
				}

				// Construction de la requete d'ajout du compte sur la bd
				if (newIdNumCompte != -1) {
					String query = "INSERT INTO COMPTECOURANT (IDNUMCOMPTE, DEBITAUTORISE, SOLDE, IDNUMCLI, ESTCLOTURE) ";
					query += "VALUES (" + newIdNumCompte + "," + (-compte.debitAutorise) + "," + compte.solde + ","
							+ compte.idNumCli + ", 'N')";

					// Ajout du compte sur la bd
					if (s.executeUpdate(query) > 0) {
						con.commit();
						AlertUtilities.showAlert(cmStage, "Ajout du compte", "Le compte a bien été ajouté", "", AlertType.INFORMATION);
					} else {
						AlertUtilities.showAlert(cmStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue\nLe compte n'a pas été ajouté",
					 "Contactez l'administrateur de la base de données\nErreur : l'execution de la requete d'insertion à échoué", AlertType.ERROR);
						con.rollback();
					}
				}

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.cmStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			} catch (SQLException se) {
				AlertUtilities.showAlert(cmStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue\nLe compte n'a pas été ajouté",
					 "Contactez l'administrateur de la base de données\nErreur : " + se.toString(), AlertType.ERROR);
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException se2) {
						AlertUtilities.showAlert(cmStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue\nLe compte n'a pas été ajouté",
					 	"Contactez l'administrateur de la base de données\nErreur : Impossibilité de rollback suite à une exception SQL\n" + se2.toString(), AlertType.ERROR);
					}
				}
			} finally {
				// Fermer les ressources
				try {
					if (result != null) result.close();
					if (s != null) s.close();
					if (con != null) con.close();
				} catch (SQLException se) {
					AlertUtilities.showAlert(cmStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue\nLe compte à été ajouté",
					 	"Contactez l'administrateur de la base de données\nErreur : Exception lors de la fermeture des ressources bd après utilisation\n" 
						+ se.toString(), AlertType.ERROR);
				}
			}
		}
		return compte;
	}

	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.cmStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}
}

