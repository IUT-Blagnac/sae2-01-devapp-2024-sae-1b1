package model.orm;

import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe d'accès aux Prélèvements automatiques en BD Oracle.
 * <p>
 * Cette classe fournit des méthodes pour accéder et manipuler les prélèvements automatiques associés à un compte bancaire.
 * </p>
 *
 * @author Yassir BOULOUIHA GNAOUI
 * @see LogToDatabase
 */
public class Access_BD_Prelevement {

    /**
     * Constructeur par défaut.
     * <p>
     * Initialise une nouvelle instance de Access_BD_Prelevement.
     * </p>
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    public Access_BD_Prelevement() {
    }

    /**
     * Recherche de tous les prélèvements automatiques établis sur un compte.
     * <p>
     * Cette méthode interroge la base de données pour récupérer tous les prélèvements automatiques associés à un compte donné.
     * Si aucun prélèvement n'est trouvé, elle retourne une liste vide.
     * </p>
     *
     * @param cpt Le compte courant dont on cherche les prélèvements.
     * @return Une liste de tous les prélèvements automatiques sur le compte, liste vide si aucun prélèvement n'est trouvé.
     * @throws DataAccessException        Si une erreur d'accès aux données survient (par exemple, si la requête est mal formée).
     * @throws DatabaseConnexionException Si une erreur de connexion à la base de données survient.
     * @throws SQLException               En cas d'erreur SQL lors de l'exécution de la requête.
     * @author Yassir BOULOUIHA GNAOUI
     * @see LogToDatabase
     */
    public ArrayList<Prelevement> getPrelevements(CompteCourant cpt) throws DataAccessException,
            DatabaseConnexionException {
        try {
            ArrayList<Prelevement> alPrelevements = new ArrayList<>();

            // Obtention de la connexion à la base de données
            Connection con = LogToDatabase.getConnexion();

            // Requête SQL pour récupérer les prélèvements automatiques du compte spécifié
            String query = """
                    SELECT idprelev, montant, daterecurrente, beneficiaire
                    FROM PRELEVEMENTAUTOMATIQUE
                    WHERE idnumcompte = ?
                    """;

            PreparedStatement pst = con.prepareStatement(query);

            // Paramétrage de la requête avec l'identifiant du compte
            pst.setInt(1, cpt.idNumCompte);

            ResultSet rs = pst.executeQuery();

            // Parcours des résultats de la requête
            while (rs.next()) {
                // Ajout des prélèvements à l'ArrayList
                alPrelevements.add(
                        new Prelevement(rs.getInt("idprelev"), rs.getDouble("montant"),
                                rs.getInt("daterecurrente"), rs.getString("beneficiaire")));
            }

            // Fermeture des ressources
            rs.close();
            pst.close();
            con.close();

            return alPrelevements;
        } catch (SQLException e) {
            throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès\n", e);
        }
    }

    /**
     * Obtient un nouvel identifiant pour un prélèvement automatique.
     *
     * @return L'identifiant (int) à utiliser pour le nouveau prélèvement.
     * @throws DataAccessException        Si une erreur d'accès aux données survient.
     * @throws DatabaseConnexionException Si une erreur de connexion à la base de données survient.
     * @throws SQLException               En cas d'erreur SQL lors de l'exécution de la requête.
     * @author Yassir BOULOUIHA GNAOUI
     * @see LogToDatabase
     */
    public int getIdNouvPrelevement() throws DataAccessException, DatabaseConnexionException {
        try (Connection con = LogToDatabase.getConnexion()) {
            int idNouvePrelevement;

            // Requête SQL pour récupérer un id qui pourrait être utilisé pour l'insertion d'un nouveau prélèvement
            String query = """
                    SELECT MAX(idprelev)+1 AS idNouveauPrelev FROM PRELEVEMENTAUTOMATIQUE
                    """;

            try (PreparedStatement pst = con.prepareStatement(query)) {
                try (ResultSet rs = pst.executeQuery()) {
                    rs.next();

                    idNouvePrelevement = rs.getInt("idNouveauPrelev");

                    return idNouvePrelevement;
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès\n", e);
        }
    }

    /**
     * Insère un nouveau prélèvement automatique dans la base de données.
     *
     * @param prlv Le prélèvement à insérer.
     * @param cpt  Le compte courant associé au prélèvement.
     * @throws DataAccessException        Si une erreur d'accès aux données survient.
     * @throws DatabaseConnexionException Si une erreur de connexion à la base de données survient.
     * @throws SQLException               En cas d'erreur SQL lors de l'exécution de la requête.
     * @author Yassir BOULOUIHA GNAOUI
     * @see LogToDatabase
     */
    public void insererPrelevement(@NotNull Prelevement prlv, @NotNull CompteCourant cpt) throws DataAccessException,
            DatabaseConnexionException {
        try (Connection con = LogToDatabase.getConnexion()) {
            con.setAutoCommit(false);

            // Requête SQL pour insérer un nouveau prélèvement automatique
            String query = """
                        INSERT INTO PRELEVEMENTAUTOMATIQUE (idprelev, montant, daterecurrente, beneficiaire, 
                                    idnumcompte) VALUES (?, ?, ?, ?, ?)
                        """;
            try (PreparedStatement pst = con.prepareStatement(query)) {
                // Paramétrage de la requête
                pst.setInt(1, prlv.idprelev);
                pst.setDouble(2, prlv.montant);
                pst.setInt(3, prlv.dateRec);
                pst.setString(4, prlv.beneficiaire);
                pst.setInt(5, cpt.idNumCompte);

                // Exécution de la requête
                if (pst.executeUpdate() > 0) {
                    con.commit();
                } else {
                    con.rollback();
                    throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Échec de l'insertion\n",
                            new Throwable("Échec de l'insertion du prélèvement dans la base de données"));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur d'insertion \n", e);
        }
    }

    public void deletePrelevement(Prelevement prl)throws DataAccessException, DatabaseConnexionException{
        try {
            ArrayList<Prelevement> alPrelevements = new ArrayList<>();

            // Obtention de la connexion à la base de données
            Connection con = LogToDatabase.getConnexion();

            // Requête SQL pour récupérer les prélèvements automatiques du compte spécifié
            String query = """
                    DELETE FROM PRELEVEMENTAUTOMATIQUE
                    WHERE idprelev= ? 
                    """;

            PreparedStatement pst = con.prepareStatement(query);

            // Paramétrage de la requête avec l'identifiant du compte
            pst.setInt(1, prl.idprelev);
            
            pst.executeQuery();


            // Fermeture des ressources
            pst.close();
            con.commit();
        } catch (SQLException e) {
            throw new DataAccessException(Table.Operation, Order.DELETE, "Erreur accès", e);
        } catch (DatabaseConnexionException e) {
            throw new DatabaseConnexionException("Erreur accès", e);
        }
    }


}
