package application;

import java.sql.*;

public class SqliteConnection {

	//conector para con la base de datos de sqlite
	
	public static Connection connector() {
		try {
			Class.forName("org.sqlite.JDBC"); //obligatorio para una correcta conexion con la base de datos de SQL haciendolo a traves de un 'llamado' a la clase JDBC
			Connection conn = DriverManager.getConnection("jdbc:sqlite:cheques.db"); // escrito asi ya que se encuentra dentro de la carpeta del proyecto
			return conn;								    //IMPORTANTE ^ si se escribe otro nombre(ChEquss.db)/no encuentra la nombrada, se creara una db nueva en el path donde se encuentra el proyecto, por tanto la conexion SERA EXITOSO IGUAL, pero sera neuva y vacia.
		} catch (Exception e) {
			System.out.println(e); // muestra el error si se dio el path incorrecto de la base de datos, necesario ya que si no hay conexion correcta la app se cierra automaticamente.
			return null;
		}
		
	} 
	
	
}
