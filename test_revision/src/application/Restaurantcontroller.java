package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;


public class Restaurantcontroller implements Initializable {
@FXML
private ComboBox<String> cbplat;
@FXML
private TextField txtqte;
@FXML
private RadioButton suppOk; // représente le bouton radio-oui
@FXML
private RadioButton SuppNo; // représente le bouton radio-non
@FXML
private ToggleGroup G1; // composant qui regroupe les boutons radio pour permettre une sélection unique
@FXML
private Button addhandle; // bouton ajouter
@FXML
private Button removehandle; // bouton supprimer
@FXML
private Button calculer; // bouton calculer
@FXML
private Label lblmontant; // label pour l’affichage du montant total
@FXML
private TableView<Plat_cmd> tableR;
@FXML
private TableColumn<Plat_cmd, String> collibelle; // colonne libelle
@FXML //
private TableColumn<Plat_cmd, Integer> colqte; // colonne quantite
@FXML
private TableColumn<Plat_cmd, Double> colsupp; // colonne supplément
@FXML
private TableColumn<Plat_cmd, Double> colpu; // colonne prix unitaire
@FXML
private TableColumn<Plat_cmd, Double> colmontant; // colonne montant
ObservableList<Plat_cmd> list1 = FXCollections.observableArrayList();
@Override
public void initialize(URL arg0, ResourceBundle arg1) {
ArrayList<String> list = new ArrayList<String>();

try {
	Connection cn = Connexion.getcn();
	String req="select libelle from plat";
	PreparedStatement stmt;
	stmt = cn.prepareStatement(req);


ResultSet rs = stmt.executeQuery(req);
while (rs.next()) {

	list.add(rs.getString("libelle"));
} }catch (SQLException e) {
	
	e.printStackTrace();
	}
ObservableList<String> cb = FXCollections.observableArrayList(list);
cbplat.setItems(cb);
collibelle.setCellValueFactory(new PropertyValueFactory<Plat_cmd,String>("libelle"));
colqte.setCellValueFactory(new PropertyValueFactory<Plat_cmd,Integer>("quantite"));
colsupp.setCellValueFactory(new PropertyValueFactory<Plat_cmd,Double>("supplement"));
colpu.setCellValueFactory(new PropertyValueFactory<Plat_cmd,Double>("PU"));
colmontant.setCellValueFactory(new PropertyValueFactory<Plat_cmd,Double>("montant"));




 tableR.setItems(list1);
}

@FXML
void addhandle(ActionEvent event) {
    String nom = cbplat.getValue();
    int quantite = Integer.parseInt(txtqte.getText());
    boolean supplementOK = suppOk.isSelected();
    boolean supplementNO = SuppNo.isSelected();
    double prixu = 0;

    try {
        String req = "SELECT prix FROM Plat WHERE libelle = ?";
        PreparedStatement stmt = Connexion.getcn().prepareStatement(req);
        stmt.setString(1, nom);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            prixu = rs.getDouble("prix");
        }

        double montantt = prixu * quantite;
        double supplement = (supplementOK) ? 2000 : 0;
        double m = prixu * quantite + supplement;

        Plat_cmd platCmd = new Plat_cmd(nom, quantite, supplement, montantt, prixu);
        list1.add(platCmd);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
@FXML
void removehandle(ActionEvent event) {
    // Get the selected item from the TableView
    Plat_cmd selectedItem = tableR.getSelectionModel().getSelectedItem();

    // Check if an item is selected
    if (selectedItem != null) {
        // Display a confirmation alert before removing the item
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le plat sélectionné ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce plat de la commande ?");

        // Show the confirmation dialog and wait for user response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User clicked OK, proceed with removing the item
                tableR.getItems().remove(selectedItem);
            }
        });
    } else {
        // If no item is selected, display an error alert
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur");
        errorAlert.setHeaderText("Aucun plat sélectionné");
        errorAlert.setContentText("Veuillez sélectionner un plat à supprimer de la commande.");
        errorAlert.showAndWait();
    }
}


@FXML
void calculer(ActionEvent event) {
	double mt = 0;
	for (Plat_cmd p : tableR.getItems()) {
		mt += p.getMontant();
    }
	lblmontant.setText(String.valueOf(mt));
	
	File outputFile = new File("C:\\fichers\\test.txt");
	BufferedWriter writer;
	try {
		writer = new BufferedWriter(new FileWriter(outputFile));
		writer.write(String.valueOf(mt));
		writer.close();

		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	

}

}
