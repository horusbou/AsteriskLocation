package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import models.*;
import models.DAO.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label revenuTotal;
    @FXML
    private Label revenuSanction;
    @FXML
    private AnchorPane vehiculePane;
    @FXML
    private Label revenuFacture;
    @FXML
    private Rectangle rectangleReservation;

    @FXML
    private AnchorPane reservationPane;

    @FXML
    private PieChart pieChartReservation;
    @FXML
    private Rectangle rectangleParking;
    @FXML
    private AnchorPane parkingPane;

    @FXML
    private PieChart pieChartParking;

    @FXML
    private Label nombreParking;

    @FXML
    private Label totalCapacite;

    @FXML
    private Label totalOccupe;

    @FXML
    private Label nbrReservationNonValide;

    @FXML
    private Label nbrReservationValide;

    @FXML
    private Label nbrReservationAnnule;

    @FXML
    private Rectangle rectangleVehicule;
    @FXML
    private Rectangle rectangleStatistique;
    @FXML
    private Label nbrReservation;
    @FXML
    private ObservableList<PieChart.Data> vehciuleStatics = FXCollections.observableArrayList();
    @FXML
    private ObservableList<PieChart.Data> reservationStatics = FXCollections.observableArrayList();
    @FXML
    private ObservableList<PieChart.Data> parkingStatics = FXCollections.observableArrayList();
    @FXML
    private PieChart pieChartVehicule;
    @FXML
    private Label nbrVehicule;
    @FXML
    private Label nbrVehiculeDispo;
    @FXML
    private Label nbrVehiculeNonDispo;
    @FXML
    private AnchorPane statistiquePane;

    @FXML
    private Label nombreClient;

    @FXML
    private Label nombreFacture;

    @FXML
    private Label nombreSanction;

    @FXML
    private TableView<R??servation> tableReservation;

    @FXML
    private TableColumn<R??servation, Integer> col_codeReservation;

    @FXML
    private TableColumn<R??servation, LocalDate>  col_dateDepart;

    @FXML
    private TableColumn<R??servation, LocalDate>  col_dateRetour;

    @FXML
    private TableColumn<R??servation, Integer>  col_idClient;

    @FXML
    private TableColumn<R??servation, Integer> col_idVehicule;

    @FXML
    private TableView<Facture> tableFacture;

    @FXML
    private TableColumn<Facture, Integer> col_numeroFacture;
    @FXML
    private TableColumn<Facture, LocalDate> col_dateFacture;
    @FXML
    private TableColumn<Facture, Double> col_montantAPayer;
    @FXML
    private TableColumn<Facture, Integer> col_idContrat;

    R??servationDAO reservationDAO;
    {
        try {
            reservationDAO = new R??servationDAO(R??servationDAO.connect);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
        }
    }
    FactureDAO factureDAO;
    {
        try {
            factureDAO = new FactureDAO(FactureDAO.connect);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
        }
    }
    SanctionDAO sanctionDAO;
    {
        try {
            sanctionDAO = new SanctionDAO(SanctionDAO.connect);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
        }
    }
    V??hiculeDAO v??hiculeDAO;
    {
        try {
            v??hiculeDAO = new V??hiculeDAO(V??hiculeDAO.connect);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
        }
    }
    ParkingDAO parkingDAO;
    {
        try {
            parkingDAO = new ParkingDAO(ParkingDAO.connect);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
        }
    }
    ClientDAO clientDAO;
    {
        try {
            clientDAO = new ClientDAO(ClientDAO.connect);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboard();
    }
    //Afficher total revenue + total de sanction + nombre de reservation
    public void dashboard()
    {
        nbrReservation.setText(String.valueOf(reservationDAO.nombreReservation()));
        revenuFacture.setText(String.valueOf(factureDAO.totalFacture()));
        revenuSanction.setText(String.valueOf(sanctionDAO.totalSanction()));
        revenuTotal.setText(String.valueOf((factureDAO.totalFacture()+sanctionDAO.totalSanction())));
        setVehciuleStatics();
        setReservationStatics();
        setParkingStatics();
        btnStatics();
    }
    //Diagramme circulaire v??hicule par nombre de fois r??serv??
    public void setVehciuleStatics()
    {
        ObservableList<Integer> listVehicule = v??hiculeDAO.selectMatricule();

        for(int i=0;i<listVehicule.size();i++)
        {
            V??hicule vehicule = v??hiculeDAO.find(listVehicule.get(i));
            double stat =100*((double)reservationDAO.nbrVehiculeReserve(vehicule.getNImmatriculation())/reservationDAO.nombreReservation());
            vehciuleStatics.add(new PieChart.Data(vehicule.getMarque()+"-"+vehicule.getType(),stat));

        }
    }
    //Diagramme circulaire reservation par son ??tat
    public void setReservationStatics()
    {
        double annule = 100*((double)reservationDAO.nombreReservationParEtat("annuler")/reservationDAO.nombreReservation());
        double nonValide = 100*((double)reservationDAO.nombreReservationParEtat("non valid??")/reservationDAO.nombreReservation());
        double valide = 100*((double)reservationDAO.nombreReservationParEtat("valid??")/reservationDAO.nombreReservation());
        reservationStatics.addAll(
                new PieChart.Data("R??servation annul??e",annule),
                new PieChart.Data("R??servation non valid??e",nonValide),
                new PieChart.Data("R??servation valid??e",valide)
        );

    }
    //Diagramme circulaire parking par nombre de place occup??es
    public void setParkingStatics()
    {
        ObservableList<Integer> listParking = parkingDAO.selectIdParking();
        for(int i=0;i<parkingDAO.nombreParking();i++)
        {
            Parking parking = parkingDAO.find(listParking.get(i));
            double stat = 100*((double)parkingDAO.nombreVehicule(parking.getNParking())/parkingDAO.nbrDePlaceOccupe());
            parkingStatics.add(new PieChart.Data(parking.getRue()+"-"+parking.getArrondissement(),stat));
        }
    }
    //Afficher les statistique v??hicule par r??servation
    public void btnVehiculeStatics()
    {
        rectangleVehicule.setVisible(true);
        rectangleReservation.setVisible(false);
        rectangleStatistique.setVisible(false);
        rectangleParking.setVisible(false);
        vehiculePane.setVisible(true);
        reservationPane.setVisible(false);
        parkingPane.setVisible(false);
        statistiquePane.setVisible(false);
        vehiculePane.toFront();
        pieChartVehicule.setData(vehciuleStatics);
        nbrVehicule.setText(String.valueOf(v??hiculeDAO.nombreVehicule()));
        nbrVehiculeDispo.setText(String.valueOf(v??hiculeDAO.nbrVehiculedispo(true)));
        nbrVehiculeNonDispo.setText(String.valueOf(v??hiculeDAO.nbrVehiculedispo(false)));
    }
    //Afficher les statistiques r??servation par ??tat
    public void btnReservationStatics()
    {
        rectangleReservation.setVisible(true);
        rectangleVehicule.setVisible(false);
        rectangleStatistique.setVisible(false);
        rectangleParking.setVisible(false);
        statistiquePane.setVisible(false);
        reservationPane.setVisible(true);
        vehiculePane.setVisible(false);
        parkingPane.setVisible(false);
        reservationPane.toFront();
        pieChartReservation.setData(reservationStatics);
        nbrReservationAnnule.setText(String.valueOf(reservationDAO.nombreReservationParEtat("annuler")));
        nbrReservationValide.setText(String.valueOf(reservationDAO.nombreReservationParEtat("valid??")));
        nbrReservationNonValide.setText(String.valueOf(reservationDAO.nombreReservationParEtat("non valid??")));
    }
    //Afficher les statistique de parking
    public void btnPartkingStatics()
    {
        rectangleParking.setVisible(true);
        rectangleReservation.setVisible(false);
        rectangleVehicule.setVisible(false);
        rectangleStatistique.setVisible(false);
        statistiquePane.setVisible(false);
        parkingPane.toFront();
        parkingPane.setVisible(true);
        vehiculePane.setVisible(false);
        reservationPane.setVisible(false);
        totalCapacite.setText(String.valueOf(parkingDAO.capaciteTotal()));
        nombreParking.setText(String.valueOf(parkingDAO.nombreParking()));
        totalOccupe.setText(String.valueOf(parkingDAO.nbrDePlaceOccupe()));
        pieChartParking.setData(parkingStatics);

    }
    //Afficher la derni??re r??servation
    private void dataReservation()
    {
        ObservableList<R??servation> lastReservation = reservationDAO.listLastReservation();
        col_codeReservation.setCellValueFactory(new PropertyValueFactory<>("codeR??servation"));
        col_dateDepart.setCellValueFactory(new PropertyValueFactory<>("dateD??part"));
        col_dateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        col_idClient.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        col_idVehicule.setCellValueFactory(new PropertyValueFactory<>("idVehicule"));
        tableReservation.setItems(lastReservation);
    }
    //Afficher la derni??re facture
    private void dataFacture() {
        ObservableList<Facture> lastFacture = factureDAO.listLastFacture();
        col_numeroFacture.setCellValueFactory(new PropertyValueFactory<>("NFacture"));
        col_dateFacture.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));
        col_montantAPayer.setCellValueFactory(new PropertyValueFactory<>("MontantAPayer"));
        col_idContrat.setCellValueFactory(new PropertyValueFactory<>("idContrat"));
        tableFacture.setItems(lastFacture);
    }
    //Afficher les autres statistiques
    public void btnStatics()
    {
        rectangleVehicule.setVisible(false);
        rectangleReservation.setVisible(false);
        rectangleStatistique.setVisible(true);
        rectangleParking.setVisible(false);
        statistiquePane.setVisible(true);
        statistiquePane.toFront();
        parkingPane.setVisible(false);
        vehiculePane.setVisible(false);
        reservationPane.setVisible(false);
        nombreClient.setText(String.valueOf(clientDAO.nombreClient()));
        nombreSanction.setText(String.valueOf(sanctionDAO.nombreSanction()));
        nombreFacture.setText(String.valueOf(factureDAO.nombreFacture()));
        dataFacture();
        dataReservation();


    }
}
