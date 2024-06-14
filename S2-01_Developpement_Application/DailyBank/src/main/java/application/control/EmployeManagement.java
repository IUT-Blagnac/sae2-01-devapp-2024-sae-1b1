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

/**
 * La classe EmployeManagement gère la gestion des employés au sein de l'application DailyBank.
 * Elle permet d'afficher une interface utilisateur pour la gestion des employés, y compris
 * l'ajout, la modification et la suppression d'employés.
 * Elle utilise EmpolyeManagementViewController pour le contrôle de la vue et DailyBankState pour
 * maintenir l'état de l'application.
 *
 * @see DailyBankState
 * @see EmpolyeManagementViewController
 */
public class EmployeManagement {

    private Stage cmStage;
    private DailyBankState dailyBankState;
    private EmpolyeManagementViewController cmViewController;

    /**
     * Constructeur de la classe EmployeManagement.
     *
     * @param _parentStage le stage parent pour la fenêtre de gestion d'employé
     * @param _dbstate l'état quotidien de la banque
     */
    public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {

        this.dailyBankState = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(EmpolyeManagementViewController.class.getResource("employemanagement.fxml"));
            BorderPane root = loader.load();

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

    /**
     * Affiche la fenêtre de gestion d'employé pour effectuer des opérations de gestion.
     *
     * @see EmpolyeManagementViewController#displayDialog()
     */
    public void doEmployeManagementDialog() {
        this.cmViewController.displayDialog();
    }

    /**
     * Modifie un employé existant dans la base de données.
     *
     * @param em l'employé à modifier
     * @return l'employé modifié
     * @throws DatabaseConnexionException si une erreur de connexion à la base de données survient
     * @throws ApplicationException si une erreur d'application survient
     * @see EmployeEditorPane
     * @see Access_BD_Employe#updateEmploye(Employe)
     */
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

    /**
     * Supprime un employé de la base de données.
     *
     * @param emp l'employé à supprimer
     * @return true si la suppression a réussi, sinon false
     * @see Access_BD_Employe#deleteEmploye(Employe)
     */
    public boolean supprimerEmploye(Employe emp) {
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

    /**
     * Crée un nouvel employé dans la base de données.
     *
     * @return l'employé créé
     * @throws DatabaseConnexionException si une erreur de connexion à la base de données survient
     * @throws ApplicationException si une erreur d'application survient
     * @see EmployeEditorPane
     * @see Access_BD_Employe#insertEmploye(Employe)
     */
    public Employe nouveauEmploye() throws DatabaseConnexionException, ApplicationException {
        Employe employe;
        EmployeEditorPane emp = new EmployeEditorPane(this.cmStage, this.dailyBankState);
        employe = emp.doEmployeEditorDialog(null, EditionMode.CREATION);
        if (employe != null) {
            Access_BD_Employe ac = new Access_BD_Employe();
            ac.insertEmploye(employe);
        }
        return employe;
    }

    /**
     * Récupère une liste d'employés en fonction des critères spécifiés.
     *
     * @param _idEmploye l'identifiant de l'employé à rechercher (0 pour ignorer)
     * @param _debutNom le début du nom de l'employé à rechercher
     * @param _debutPrenom le début du prénom de l'employé à rechercher
     * @return une liste d'employés correspondant aux critères de recherche
     * @throws DatabaseConnexionException si une erreur de connexion à la base de données survient
     * @throws ApplicationException si une erreur d'application survient
     * @see Access_BD_Employe#getEmploye(int, int, String, String)
     */
    public ArrayList<Employe> getlisteEmployes(int _idEmploye, String _debutNom, String _debutPrenom) throws DatabaseConnexionException, ApplicationException {
        ArrayList<Employe> listeEmp = new ArrayList<>();
        Access_BD_Employe ac = new Access_BD_Employe();
        listeEmp = ac.getEmploye(this.dailyBankState.getEmployeActuel().idAg, _idEmploye, _debutNom, _debutPrenom);
        return listeEmp;
    }
}
