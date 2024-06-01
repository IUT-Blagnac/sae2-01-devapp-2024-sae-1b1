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
 */
public class Access_BD_Prelevement {

    public Access_BD_Prelevement() {
    }

    /**
     * Recherche de tous les prélèvements automatiques établis sur un compte.
     *
     * @param idNumCompte id du compte dont on cherche les prélèvements
     * @return Tous les prélèvements automatiques sur le compte, liste vide si pas de prélèvements
     * @throws DataAccessException        Erreur d'accès aux données (requête mal
     *                                    formée ou autre)
     * @throws DatabaseConnexionException Erreur de connexion
     */

    public ArrayList<Prelevement> getPrelevements(CompteCourant cpt) throws DataAccessException, DatabaseConnexionException {
        try {

            ArrayList<Prelevement> alPrelevements = new ArrayList<>();

            Connection con = LogToDatabase.getConnexion();

            String query = """
                    SELECT idprelev, montant, daterecurrente, beneficiaire
                    FROM PRELEVEMENTAUTOMATIQUE
                    WHERE idnumcompte = ?
                    """;

            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, cpt.idNumCompte);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                alPrelevements.add(
                        new Prelevement(rs.getInt("idprelev"), rs.getDouble("montant"),
                                rs.getInt("daterecurrente"), rs.getString("beneficiaire")));
            }

            rs.close();
            pst.close();

            return alPrelevements;
        } catch (SQLException e) {
            throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
        }


    }

}
