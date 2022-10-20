package application;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import cajon.Cheque;
import cajon.Cuaderno;
import cajon.Fecha;
import cajon.SaldoCheque;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class FXController implements Initializable {

	public DbController dbControl = new DbController(); // instancia de conexion a la db;
	// agregar el .jar file necesario con click derecho en JRE System Library del
	// projecto->build path->configure build path-> agregar jar file externo y
	// seleccionar el driver descargado del sqlite-jdbc-xx.jar

	public ObservableList<Cheque> cheques = FXCollections.observableArrayList(dbControl.setCuadernoConDB());
	public ObservableList<String> meses = FXCollections.observableArrayList("Enero", "Febrero", "Marzo", "Abril",
			"Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
	public ObservableList<String> opciones = FXCollections.observableArrayList("Agregar", "Eliminar", "Buscar",
			"Modifcar Saldo");
	public ObservableList<SaldoCheque> saldos = FXCollections
			.observableArrayList(dbControl.getCuaderno().setSaldoCheque());

	String nom = "Muestra de cheques emitidos";

	@FXML
	ComboBox<String> selecOpc;
	@FXML
	ComboBox<String> selecMes;
	@FXML
	Label lblDbStatus;
	@FXML
	TableView<Cheque> tableViewCheques = new TableView<>(cheques);
	@FXML
	TableColumn<Cheque, Void> id = new TableColumn<>();
	@FXML
	TableColumn<Cheque, Integer> num = new TableColumn<>("Numero de Cheque");
	@FXML
	TableColumn<Cheque, Fecha> emision = new TableColumn<>("Fecha de Emision");
	@FXML
	TableColumn<Cheque, Fecha> pago = new TableColumn<>("Fecha de Pago");
	@FXML
	TableColumn<Cheque, Double> valor = new TableColumn<>("Valor (pesos)");
	@FXML
	TableColumn<Cheque, String> desc = new TableColumn<>("Concepto");
	@FXML
	TableColumn<Cheque, CheckBox> pagado = new TableColumn<>("Pagado");
	
	@FXML
	TableView<SaldoCheque> tableViewSaldos = new TableView<>(saldos);
	@FXML
	TableColumn<SaldoCheque, Double> saldo;

	@FXML
	TextField ingNum;
	@FXML
	TextField ingValor;
	@FXML
	TextField concept;
	@FXML
	DatePicker dEmision = new DatePicker();
	@FXML
	DatePicker dPago = new DatePicker();
	@FXML
	TextField ingSaldo = new TextField();
	@FXML
	Button agregarBtn = new Button();
	@FXML
	Button addSaldoBtn = new Button();
	@FXML
	Button eliminarBtn = new Button();
	@FXML
	Button buscarBtn = new Button();
	@FXML
	Label saldoInicial;
	@FXML
	Label saldoActual;
	@FXML
	Label saldoUltPago;
	
	DateTimeFormatter formatoArg = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (dbControl.isDbConnected()) {
			lblDbStatus.setText("Conexión a la base de datos EXITOSA");
			lblDbStatus.setTextFill(Color.GREEN);
		} else {
			lblDbStatus.setText("No se logró conectar a la base de datos");
			lblDbStatus.setTextFill(Color.RED);
		}
		
		this.disableAll();
		this.selecMes.setItems(meses);
		this.selecOpc.setItems(opciones);
		this.mostrarCheque(this.cheques);
		this.setSaldoInicial();
		this.setSaldoActual();
		this.setSaldoUltPago();
		this.ordenarSaldo();

	}

	// Devuelve TRUE si hay campos vacios
	private boolean controlCampos(TextField num, TextField valor, TextField concept, DatePicker emision,
			DatePicker pago) {
		boolean estaVacio = true;
		if (num.getText().isEmpty() || valor.getText().isEmpty() || concept.getText().isEmpty()
				|| emision.getValue() == null || pago.getValue() == null) {
			errorMsg("Campos vacios");

		} else {
			estaVacio = false;
		}

		return estaVacio;
	}

	private void errorMsg(String msg) {
		this.lblDbStatus.setText(msg);
		this.lblDbStatus.setTextFill(Color.RED);
	}

	public void mostrarCheque(ObservableList<Cheque> cheques) {// CONTROLADOR TABLEVIEW ACTLZ. LISTADO CHEQUES

		id.setCellFactory(col -> {//adds index number to each cell

			// just a default table cell:
			TableCell<Cheque, Void> cell = new TableCell<>();

			cell.textProperty().bind(Bindings.createStringBinding(() -> {
				if (cell.isEmpty()) {
					return null;
				} else {
					return Integer.toString(cell.getIndex() + 1);
				}
			}, cell.emptyProperty(), cell.indexProperty()));

			return cell;
		});

		num.setCellValueFactory(new PropertyValueFactory<Cheque, Integer>("num"));
		emision.setCellValueFactory(new PropertyValueFactory<Cheque, Fecha>("emision"));
		pago.setCellValueFactory(new PropertyValueFactory<Cheque, Fecha>("pago"));// *SOLUCIONADO*No muestra los
																					// elementos que se encuentran en la
																					// lista PORQUE HABIAS ESCRITO
																					// PAGADO EN VEZ DE PAGO (no referia
																					// al .fxml si no al Objeto)
		valor.setCellValueFactory(new PropertyValueFactory<Cheque, Double>("valor"));
		desc.setCellValueFactory(new PropertyValueFactory<Cheque, String>("desc"));
		
//		pagado.setCellValueFactory(new PropertyValueFactory<Cheque, CheckBox>("pagado"));
		
		//Thank you stackOverflow
		pagado.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Cheque, CheckBox>, ObservableValue<CheckBox>>() {
            @Override
            public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Cheque, CheckBox> arg0) {
                
            	Cheque pagado = arg0.getValue();

                CheckBox checkBox = new CheckBox();

                checkBox.selectedProperty().setValue(pagado.isPagado());
                System.out.println("CBValue = " + checkBox.selectedProperty().get() + " PagadoATRB Value = " + pagado.isPagado());
                checkBox.setDisable(false);

                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    
                	public void changed(ObservableValue<? extends Boolean> ov,Boolean old_val, Boolean new_val) {

                        pagado.setPagado(new_val);
                        dbControl.modificarPagado(new_val, pagado.getNum());
                       
                    }
                });
                return new SimpleObjectProperty<CheckBox>(checkBox);
            }
            
        });
				
		tableViewCheques.setItems(cheques);

	}

	public void ordenarSaldo() {
		this.saldo.setCellValueFactory(new PropertyValueFactory<SaldoCheque, Double>("saldo"));
		this.tableViewSaldos.setItems(saldos);
	}

	public void agregarCheque(ActionEvent click) {// CONTROLADOR BOTON
		if (!controlCampos(ingNum, ingValor, concept, dEmision, dPago)) {
			int num = Integer.parseInt(ingNum.getText());
			double valor = Double.parseDouble(ingValor.getText());
			// int diaE = dEmision.getValue().getDayOfMonth();
			// int mesE = dEmision.getValue().getMonthValue();
			// int anoE = dEmision.getValue().getYear();
			// int diaP = dPago.getValue().getDayOfMonth();
			// int mesP = dPago.getValue().getMonthValue();
			// int anoP = dPago.getValue().getYear();
			String emision = dEmision.getValue().format(formatoArg);
			String pago = dPago.getValue().format(formatoArg);
			String concepto = concept.getText();
			if (!dbControl.buscarNum(num)) { // agrega correctamente pero no actualiza la lista mostrada*SOLUCIONADO=VER
				// SIGUIENTES LINES
				dbControl.agregarCheque(num, emision, pago, concepto, valor, false);
				this.cheques = FXCollections.observableArrayList(dbControl.setCuadernoConDB());// FALTABA
				this.mostrarCheque(this.cheques);// ESTO
				this.lblDbStatus.setText("Agregado CORRECTAMENTE");
				this.lblDbStatus.setTextFill(Color.GREEN);
				this.cleanFields();
				this.setSaldoUltPago();
				// para actualizar lista de saldos
				this.saldos = FXCollections.observableArrayList(dbControl.getCuaderno().setSaldoCheque());
				this.ordenarSaldo();
			} else {
				errorMsg("Ya existe un cheque con el numero ingresado");
			}

		}
	}

	public void deleteCheque(ActionEvent click) { // CONTROLADOR BOTON
		if (ingNum.getText() != null) {

			int num = Integer.parseInt(ingNum.getText());
			if (dbControl.buscarNum(num)) {
				this.dbControl.deleteCheque(num);
				// ACTUALIZA LA LISTA OBSERVABLE
				this.cheques = FXCollections.observableArrayList(dbControl.setCuadernoConDB());
				this.mostrarCheque(this.cheques);
				this.lblDbStatus.setText("Eliminacion EXITOSA");
				this.lblDbStatus.setTextFill(Color.GREEN);
				this.cleanFields();
				this.setSaldoUltPago();// NO ACTUALIZA EL SALDO DESDE ESTE LADO
				// para actualizar lista de saldos
				this.saldos = FXCollections.observableArrayList(dbControl.getCuaderno().setSaldoCheque());
				this.ordenarSaldo();
			} else {
				errorMsg("No se encontro el cheque");
			}
		} else {
			errorMsg("Ingrese un numero");
		}
	}

	public void buscarCheque(ActionEvent click) { //
		ObservableList<Cheque> miCheque = FXCollections.observableArrayList();
		ObservableList<SaldoCheque> miSaldo = FXCollections.observableArrayList();

		int numCheque = Integer.parseInt(ingNum.getText());
		if (dbControl.buscarNum(numCheque)) {
			SaldoCheque sC = null;// para lista de saldo
			Cheque c = null;
			int i = 0;
			double saldo = dbControl.getCuaderno().getValorIngreso(); // para lista de saldo
			while (this.cheques.size() > i && c == null) {
				saldo -= cheques.get(i).getValor(); // para lista de saldo
				if (cheques.get(i).getNum() == numCheque) {
					c = cheques.get(i);
					sC = new SaldoCheque(c.getNum(), saldo); // para lista de saldo
				} else {
					i++;
				}
			}
			miCheque.add(c);
			miSaldo.add(sC);// para lista de saldo
			tableViewCheques.getItems().clear();
			this.mostrarCheque(miCheque);
			// para actualizar lista de saldos
			tableViewSaldos.getItems().clear();
			this.saldo.setCellValueFactory(new PropertyValueFactory<SaldoCheque, Double>("saldo"));
			tableViewSaldos.setItems(miSaldo);

		} else {
			errorMsg("No se encontro el cheque");
		}

	}

	public void agregarSaldo(ActionEvent click) { // CONTROLADOR BOTON c/ingSaldo: TextField
		//TODO: Modularizar validacion
		if(!ingSaldo.getText().isEmpty()) {
			double saldoAg = Double.parseDouble(ingSaldo.getText());
			dbControl.getCuaderno().agregarSaldo(saldoAg);
			setSaldoActual();
			setSaldoInicial();
			setSaldoUltPago();
		} else {
			errorMsg("Campo vacio");
		}
			
		}

	public void mostrarTodo(ActionEvent click) {// CONTROLADOR BOTON
		cheques = FXCollections.observableArrayList(dbControl.setCuadernoConDB());
		saldos = FXCollections.observableArrayList(dbControl.getCuaderno().setSaldoCheque());
		mostrarCheque(this.cheques);
		ordenarSaldo();
	}

	public void mostrarCheqSegunMes(ActionEvent click) {// REVISAR*SOLC*
		cheques = FXCollections.observableArrayList(dbControl.setCuadernoConDB());
		ObservableList<Cheque> misCheques = FXCollections.observableArrayList();
		ObservableList<SaldoCheque> miSaldo = FXCollections.observableArrayList();
		SaldoCheque sC = null;
		int mesSelec = selecMesToInt(this.selecMes.getValue());
		double saldo = dbControl.getCuaderno().getValorIngreso();

		for (Cheque c : this.cheques) {
			if (c.getPago().getMes() == mesSelec) {
				misCheques.add(c);
				saldo -= c.getValor();
				sC = new SaldoCheque(c.getNum(), saldo);
				miSaldo.add(sC);
			}
		} // FUNCIONA_PERO QUE FEEO!

		// ACTUALIZA LISTA? SIII
		tableViewCheques.getItems().clear();
		this.mostrarCheque(misCheques);
		// para actualizar lista de saldos
		tableViewSaldos.getItems().clear();
		this.saldo.setCellValueFactory(new PropertyValueFactory<SaldoCheque, Double>("saldo"));
		tableViewSaldos.setItems(miSaldo);

	}

	public void selecOpcHandler(ActionEvent click) { // CONTROLADOR BOTON OPCIONES
		switch (selecOpc.getValue()) {
		case "Agregar":
			this.agregarBtn.setDisable(false);
			this.eliminarBtn.setDisable(true);
			this.buscarBtn.setDisable(true);
			this.ingNum.setDisable(false);
			this.dEmision.setDisable(false);
			this.dPago.setDisable(false);
			this.concept.setDisable(false);
			this.ingValor.setDisable(false);
			break;
		case "Buscar":
			this.agregarBtn.setDisable(true);
			this.eliminarBtn.setDisable(true);
			this.buscarBtn.setDisable(false);
			this.ingNum.setDisable(false);
			this.dEmision.setDisable(true);
			this.dPago.setDisable(true);
			this.concept.setDisable(true);
			this.ingValor.setDisable(true);
			break;
		case "Eliminar":
			this.agregarBtn.setDisable(true);
			this.eliminarBtn.setDisable(false);
			this.buscarBtn.setDisable(true);
			this.ingNum.setDisable(false);
			this.dEmision.setDisable(true);
			this.dPago.setDisable(true);
			this.concept.setDisable(true);
			this.ingValor.setDisable(true);
			break;
			
		default:
			disableAll();

		}
	}

	private void disableAll() { // DESACTIVA INPUT CHEQUE + OPCIONES
		this.ingNum.setDisable(true);
		this.dEmision.setDisable(true);
		this.dPago.setDisable(true);
		this.concept.setDisable(true);
		this.ingValor.setDisable(true);
		this.agregarBtn.setDisable(true);
		this.eliminarBtn.setDisable(true);
		this.buscarBtn.setDisable(true);

	}

	private void cleanFields() { // VACIA INPUTS TEXTFIELD PARA DESPUES DE UN INGRESO
		this.ingNum.clear();
		this.dEmision.setValue(null);
		;
		this.dPago.setValue(null);
		this.concept.clear();
		this.ingValor.clear();
	}

	private void setSaldoInicial() {
		String msj = "Saldo Inicial: $";
		String saldo = dbControl.getCuaderno().getValorIngresoString();
		this.saldoInicial.setText(msj + saldo);
	}

	private void setSaldoActual() {
		this.saldoActual.setText("Saldo Actual $" + dbControl.getCuaderno().getValorIngresoString());
	}

	private void setSaldoUltPago() {
		String msj = "Saldo hasta Ult. Movimiento: $";
		String saldo = String.valueOf(dbControl.getCuaderno().getSaldo());
		this.saldoUltPago.setText(msj + saldo);
	}

	private int selecMesToInt(String selecMes) {
		int miMes = 0;// agregar el año despues?

		switch (selecMes) {
		case "Enero":
			miMes = 1;
			break;
		case "Febrero":
			miMes = 2;
			break;
		case "Marzo":
			miMes = 3;
			break;
		case "Abril":
			miMes = 4;
			break;
		case "Mayo":
			miMes = 5;
			break;
		case "Junio":
			miMes = 6;
			break;
		case "Julio":
			miMes = 7;
			break;
		case "Agosto":
			miMes = 8;
			break;
		case "Septiembre":
			miMes = 9;
			break;
		case "Octubre":
			miMes = 10;
			break;
		case "Noviembre":
			miMes = 11;
			break;
		case "Diciembre":
			miMes = 12;
			break;

		}

		return miMes;
	}



}// THIS IS THE END T_T
