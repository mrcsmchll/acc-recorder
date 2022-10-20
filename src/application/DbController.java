package application;

import java.sql.*;
import java.util.ArrayList;

import cajon.Cheque;
import cajon.Cuaderno;

public class DbController {

	Connection connection;
	private Cuaderno cuaderno;
	private ArrayList<Cheque> cheques;

	// --Constructor
	public DbController() {
		connection = SqliteConnection.connector();
		
		if (connection == null) { // cierra la app si la conexion no es exitosa
			System.exit(1);
		} else {
			this.cheques = this.setCuadernoConDB();
		}
	}

	// --Essential Method
	public boolean isDbConnected() {
		try {
			return !connection.isClosed(); // devuelve booleano true si la conexion esta cerrada, si es exitosa devuelve
											// false
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Cuaderno getCuaderno() {
		cuaderno.actualizarTotalCheques();
		cuaderno.actualizarSaldo();
		return cuaderno;
	}
	
	// --Functional Methods
	public boolean buscarNum(int num) {
		PreparedStatement pS = null; // ^ necesario para cerrar el statement y el result en finally.
		ResultSet result = null;
		String query = "SELECT * FROM cheques WHERE num = ?";
		try {
			pS = connection.prepareStatement(query);// Conecta a la db y envia el query en un statement
			pS.setInt(1, num);
			// ^ parameterIndex es el numero de orden de cada signo de pregunta a reemplazar
			// por el input, el segundo parametro es el input en si
			result = pS.executeQuery(); // ejecuta el query con el statement preparado y el resultado sera guardado en
										// este objeto
			if (result.next()) {
				// util para confirmar la existencia de una nueva fila creada como resultado del
				// query, es decir, que el query se ejecuto correctamente, si no = false;
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {// SIEMPRE CERRAR LOS PREPAREDSTATEMENT Y RESULTSET (similar a input.close())
			try {
				pS.close();
				result.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
		}
	}

	public void deleteCheque(int num) {
		

		PreparedStatement pS = null;
		String query = "DELETE FROM cheques WHERE num = ?";

		if (buscarNum(num)) {

			try {
				pS = connection.prepareStatement(query);
				pS.setInt(1, num);
				pS.executeUpdate();

			} catch (SQLException e) {
				System.out.println(e.toString());
			} finally {
				this.setChequesConDB();
				try {
					pS.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				cuaderno.actualizarTotalCheques();
				cuaderno.actualizarSaldo();
				System.out.println("en DbContro.delete: " + cuaderno);
			}
		}
	}

	public void agregarCheque(int num, String dEmision, String dPago, String conc, double valor, boolean isPagado) {
		// Tira nullPointerException
		PreparedStatement pS = null;
//		String query = "INSERT INTO cheques (num, emision, pago, concept, valor) VALUES (?, ?, ?, ?, ?)";
		String query = "INSERT INTO cheques (num, emision, pago, concept, valor, isPagado) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			pS = connection.prepareStatement(query);
			pS.setInt(1, num);
			pS.setString(2, dEmision);
			pS.setString(3, dPago);
			pS.setString(4, conc);
			pS.setDouble(5, valor);
			pS.setBoolean(6, isPagado);
			pS.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.toString());

		} finally {
			try {
				pS.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
			cuaderno.actualizarTotalCheques();
			cuaderno.actualizarSaldo();
			System.out.println("en DbContro.aagregar: " + cuaderno);
		}
	}

	public ArrayList <Cheque> setCuadernoConDB() { // importa la db al Cuaderno.chequesEmitidos: ArrayList;
		this.cuaderno  = new Cuaderno(2021, 100000);//TODO: EL CUADERNO DEBE SER RECIBIDO POR PARAMETRO, VALIDAR SI SE ENCUENTRA EN LA DB ? DEVOLVERLO : TIRAR ERROR Y PREGUNTAR SI DESEA CREAR UNA NUEVA O VOLVER A INTENTAR INGRESAR A LA APLICACION
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "SELECT * FROM cheques";

		try {
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				cuaderno.getChequesEmitidos().add( // TODO: esto esta mal, no se deberia exponer asi una lista, CORREGIR
						new Cheque(rs.getInt(2), rs.getDouble(6), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(7)));
			}

		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
			cuaderno.actualizarTotalCheques();
			cuaderno.actualizarSaldo();
		}
		return cuaderno.getChequesEmitidos();

	}
	
	public ArrayList<Cheque> setChequesConDB() { // importa la db al ArrayList
		this.cheques = new ArrayList<Cheque>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "SELECT * FROM cheques";

		try {
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				cheques.add(
						new Cheque(rs.getInt(2), rs.getDouble(6), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(7)));
			}

		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
		}

		return this.cheques;

	}

	public void modificarPagado(boolean pagado, int num) {
		PreparedStatement pS = null;
//		String query = "INSERT INTO cheques (num, emision, pago, concept, valor) VALUES (?, ?, ?, ?, ?)";
		String query = "UPDATE cheques SET isPagado = ? WHERE num = ?";
		try {
			pS = connection.prepareStatement(query);
			pS.setBoolean(1, pagado);
			pS.setInt(2, num);
			pS.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.toString());

		} finally {
			try {
				pS.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
			cuaderno.actualizarTotalCheques();
			cuaderno.actualizarSaldo();		
			System.out.println("Hubo mod? Arranca en 0 false, 1 true. Result = " + cuaderno.buscarCheque(num).isPagado());
			
		}
	}
	
	
//	public ArrayList<Cheque> chequeSegunMes(String mes){
//		ArrayList<Cheque> misCheques = new ArrayList<Cheque>();
//		String pre = null, post = null;
//		
//		switch(mes) {
//		case "Enero":
//			pre = "01/01/2013";
//			post = "31/01/2013";
//			break;
//		}
//		
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		String query = "SELECT * FROM cheques WHERE emision BETWEEN ? AND ?";
//		
//		try {
//			ps = connection.prepareStatement(query);
//			ps.setString(1, pre);
//			ps.setString(2, post);
//			rs = ps.executeQuery();
//			
//			while (rs.next()) {
//				misCheques.add(new Cheque(rs.getInt(2), rs.getDouble(6), rs.getString(3), rs.getString(4), rs.getString(5)));
//			}
//			
//		} catch (SQLException e) {
//			System.out.println(e.toString());
//		} finally {
//			try {
//				ps.close();
//				rs.close();
//			} catch (SQLException e) {
//			}
//		}
//		return misCheques;
//	}

}
