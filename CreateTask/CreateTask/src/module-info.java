module CreateTask {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.media;
	requires java.desktop;
	
	opens application to javafx.graphics, javafx.fxml;
}
