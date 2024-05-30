package application.control;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_Client;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.LogToDatabase;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class OperationsManagement {

	private Stage omStage;
	private DailyBankState dailyBankState;
	private OperationsManagementViewController omViewController;
	private Client clientDuCompte;
	private CompteCourant compteConcerne;

	public OperationsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementViewController.class.getResource("operationsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.omStage = new Stage();
			this.omStage.initModality(Modality.WINDOW_MODAL);
			this.omStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.omStage);
			this.omStage.setScene(scene);
			this.omStage.setTitle("Gestion des opérations");
			this.omStage.setResizable(false);

			this.omViewController = loader.getController();
			this.omViewController.initContext(this.omStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doOperationsManagementDialog() {
		this.omViewController.displayDialog();
	}

	public Operation enregistrerDebit() {

		OperationEditorPane oep = new OperationEditorPane(this.omStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.omStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	

/**
 * Enregistre une opération de crédit sur le compte courant concerné.
 * 
 * @return L'opération de crédit enregistrée, ou null si une erreur s'est produite.
 */
public Operation enregistrerCredit() {

    // Crée et affiche une boîte de dialogue pour saisir les détails de l'opération de crédit
    OperationEditorPane oep = new OperationEditorPane(this.omStage, this.dailyBankState);
    Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT);

    if (op != null) {
        Connection con = null;
        Statement s = null;
        ResultSet result = null;
        PreparedStatement pst = null;
        try {
            // Initialisation de l'id (id de la bd) de la nouvelle opération à -1 pour gérer les erreurs 
            int newIdOp = -1;

            // Connection à la base de données
            con = LogToDatabase.getConnexion();
            con.setAutoCommit(false); // Gestion manuelle des transactions

            // Création du statement pour exécuter les requêtes
            s = con.createStatement();

            // Récupération d'un numéro d'opération qui peut être attribué à une nouvelle opération
            result = s.executeQuery("SELECT MAX(idoperation) + 1 AS NOUV_ID_OP FROM Operation");

            // Vérifier que le ResultSet contient des données et placer le curseur sur la donnée présente 
            if (result.next()) {
                newIdOp = result.getInt("NOUV_ID_OP");
            } else {
                AlertUtilities.showAlert(omStage, "Erreur Base de données", 
                    "Une erreur concernant la base de données est survenue\nL'opération n'a pas été enregistrée",
                    "Contactez l'administrateur de la base de données\nErreur : Données dans COMPTECOURANT inexistantes", 
                    AlertType.ERROR);
            }

            // Construction de la requête d'ajout de l'opération sur la bd
            if (newIdOp != -1) {

                String query = "INSERT INTO OPERATION (IDOPERATION, MONTANT, DATEVALEUR, IDNUMCOMPTE, IDTYPEOP) VALUES (?, ?, ?, ?, ?)";
                pst = con.prepareStatement(query);

                // Calculer la date d'aujourd'hui + 2 jours (date effective de l'opération à renseigner sur la bd)
                LocalDate todayPlusTwo = LocalDate.now().plusDays(2);
                Date sqlDate = Date.valueOf(todayPlusTwo);

                pst.setInt(1, newIdOp);
                pst.setDouble(2, op.montant);
                pst.setDate(3, sqlDate); // insertion de la date d'aujourd'hui (+2 jours)
                pst.setInt(4, this.compteConcerne.idNumCompte);
                pst.setString(5, op.idTypeOp);

                // Ajout de l'opération sur la bd
                if (pst.executeUpdate() > 0) {
                    // Mise à jour du solde du compte localement
                    this.compteConcerne.solde += op.montant;

                    // Modification du solde du compte sur la bd 
                    query = "UPDATE COMPTECOURANT SET solde = ? WHERE idnumcompte = ?";

                    pst = con.prepareStatement(query);
                    pst.setDouble(1, this.compteConcerne.solde);
                    pst.setInt(2, this.compteConcerne.idNumCompte);

                    if (pst.executeUpdate() > 0) {
                        con.commit();
                        Access_BD_Client accesCl = new Access_BD_Client();
                        AlertUtilities.showAlert(omStage, "Opération de Crédit", "Le compte a bien été crédité", 
                            "Le compte numéro " + this.compteConcerne.idNumCompte  
                            + " de " + accesCl.getClient(this.compteConcerne.idNumCli).nom + " " 
                            + accesCl.getClient(this.compteConcerne.idNumCli).prenom + " a bien été crédité de " 
                            + op.montant + "€", AlertType.INFORMATION);
                    }

                } else {
                    AlertUtilities.showAlert(omStage, "Erreur Base de données", 
                        "Une erreur concernant la base de données est survenue\nL'opération n'a pas été enregistrée",
                        "Contactez l'administrateur de la base de données\nErreur : l'exécution de la requête d'insertion a échoué", 
                        AlertType.ERROR);
                    con.rollback();
                }
            }

        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.omStage.close();
            op = null;
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, ae);
            ed.doExceptionDialog();
            op = null;
        } catch (SQLException se) {
            AlertUtilities.showAlert(omStage, "Erreur Base de données", 
                "Une erreur concernant la base de données est survenue\nL'opération n'a pas été enregistrée",
                "Contactez l'administrateur de la base de données\nErreur : " + se.toString(), 
                AlertType.ERROR);
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException se2) {
                    AlertUtilities.showAlert(omStage, "Erreur Base de données", 
                        "Une erreur concernant la base de données est survenue\nL'opération n'a pas été enregistrée",
                        "Contactez l'administrateur de la base de données\nErreur : Impossibilité de rollback suite à une exception SQL\n" 
                        + se2.toString(), AlertType.ERROR);
                }
            }
        } finally {
            // Fermer les ressources
            try {
                if (result != null) result.close();
                if (pst != null) pst.close();
                if (s != null) s.close();
                if (con != null) con.close();
            } catch (SQLException se) {
                AlertUtilities.showAlert(omStage, "Erreur Base de données", 
                    "Une erreur concernant la base de données est survenue\nL'opération a été enregistrée",
                    "Contactez l'administrateur de la base de données\nErreur : Exception lors de la fermeture des ressources bd après utilisation\n" 
                    + se.toString(), AlertType.ERROR);
            }
        }
    }
    return op;
}


	public Operation enregistrerVirement() {

		VirementEditorPane vep = new VirementEditorPane(this.omStage, this.dailyBankState);
		PairsOfValue<Operation,Operation> ops = vep.doOperationEditorDialog(this.compteConcerne);
        CompteCourant compteDest=vep.getDestinataire();
        ops=null;
		// if (ops != null) {
			// try {
				// Access_BD_Operation ao = new Access_BD_Operation();
// 
				// ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
// 
			// } catch (DatabaseConnexionException e) {
				// ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, e);
				// ed.doExceptionDialog();
				// this.omStage.close();
				// op = null;
			// } catch (ApplicationException ae) {
				// ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, ae);
				// ed.doExceptionDialog();
				// op = null;
			// }
		// }
		return null;
	}
// 
	public PairsOfValue<CompteCourant, ArrayList<Operation>> operationsEtSoldeDunCompte() {
		ArrayList<Operation> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			Access_BD_Operation ao = new Access_BD_Operation();
			listeOP = ao.getOperations(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.omStage.close();
			listeOP = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.omStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeOP = new ArrayList<>();
		}
		System.out.println(this.compteConcerne.solde);
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
}
