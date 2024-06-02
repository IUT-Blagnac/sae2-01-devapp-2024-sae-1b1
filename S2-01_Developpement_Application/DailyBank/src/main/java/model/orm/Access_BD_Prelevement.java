package model.orm;

import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

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
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    public ArrayList<Prelevement> getPrelevements(CompteCourant cpt) throws DataAccessException, DatabaseConnexionException {
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
                //Ajout des prélèvements à l'ArrayList
                alPrelevements.add(
                        new Prelevement(rs.getInt("idprelev"), rs.getDouble("montant"),
                                rs.getInt("daterecurrente"), rs.getString("beneficiaire")));
            }

            // Fermeture des ressources
            rs.close();
            pst.close();

            return alPrelevements;
        } catch (SQLException e) {
            throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
        }
    }
}
