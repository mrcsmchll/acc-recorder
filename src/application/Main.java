package application;
 


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;



public class Main extends Application {

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
			//---Defining layout for buttons and containers
			Parent pane = FXMLLoader.load(getClass().getResource("/application/Login.fxml"));
		
			
//		VBox pane = new VBox(); 
			//---Defining Containers
			Scene scene = new Scene(pane);
			
			
			//---Defining Nodes (buttons)
//		Button btn = new Button("Click");
//		Button exit = new Button("Click for EXIT");
//		Button exitWMsg = new Button ("Message me, and exit.");
			
//		//---
//		btn.setOnAction(new EventHandler<ActionEvent>() { // ESTO MISMO EN UNA LN29
//			
//			@Override
//			public void handle(ActionEvent event) {
//				System.out.println("Vamo bien");
//			}
//		});
//		
//		exit.setOnAction(e -> System.exit(0)); // USANDO LAMBDA EXPRESSION SE REDUCE LA CANTIDAD DE LINEAS PARA EL LLAMADO AL METODO ABSTRACTO DENTRO DE SETONACTION
//		
//		exitWMsg.setOnAction(e -> { //"{}" PARA AGREGAR ACCIONES AL BOTON. 
//			System.out.println("Vamo super bien");
//			System.exit(0);
//		});
//		
//		//---Adding button to layout
//		pane.getChildren().addAll(btn, exit, exitWMsg);
			//---Displaying the fxs
			primaryStage.setTitle("MyFX");
			primaryStage.setScene(scene);
			primaryStage.show();
	

		}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
