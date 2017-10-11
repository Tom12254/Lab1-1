package application;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
//this is nonsense
public class fxmlController {
	BuildPtr ptr = new BuildPtr();
    @FXML private WebView browser;
    @FXML private Button NewText;
    @FXML private Button AddText;
    @FXML private Button queryBridge;
    @FXML private Button Path;
    @FXML private Button saveImg;
    @FXML private Slider Slide;
    @FXML private Button Randomwalk;
    @FXML private TextArea Output;
    @FXML private TextField Src;
    @FXML private TextField Dst;
    @FXML private static AnchorPane myAnchorPane;
    @FXML private void initialize()
    {
    	WebEngine webEngine = browser.getEngine();
        webEngine.load(Main.class.getResource("index.html").toExternalForm());
        Slide.valueProperty().addListener((observable, oldValue, newValue) -> {
        	webEngine.executeScript(String.format("inputted(%f)",newValue));
        });

    }
    
    @FXML protected void handlesaveImg(ActionEvent event){
    	 File destFile = new File("graph.png");
    	   WritableImage snapshot = browser.snapshot(new SnapshotParameters(), null);
    	   RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
    	   try {
    	       ImageIO.write(renderedImage, "png", destFile);
    	   } catch (IOException ex) {
    	       Output.setText("something wrong when saveing the pages");
    	   }
    }
    @FXML protected void handleRandomwalk(ActionEvent event) {
    	Output.setText(ptr.randomWalk());
    	WebEngine webEngine = browser.getEngine();
    	webEngine.executeScript("cleanPath()");
    	int pre = 0;
    	if(ptr.element.size()>0){
    		for(int now = 1;now<ptr.element.size();now++){
        		webEngine.executeScript(String.format("walkPath('%s','%s')",
        				ptr.element.get(pre),ptr.element.get(now)));
        		pre = now;
        	}
    	}

    }
    @FXML protected void handleShortestPath(ActionEvent event) {
    	WebEngine webEngine = browser.getEngine();
    	webEngine.executeScript("cleanPath()");
    	String src = Src.getText();
    	String dst = Dst.getText();
    	Output.setText(ptr.calcShortestPath(src,dst));
    	Vector<String> pathelement = ptr.pathcollect(src,dst);
    	if(pathelement.size()!=0){
    		String pre = pathelement.get(0);
	    	for(String now:pathelement){
	    		webEngine.executeScript(String.format("walkPath('%s','%s')",
	    				pre,now));
	    		pre = now;
	    	}

    	}
    }
    @FXML protected void handleNewText(ActionEvent event) {
    	String settext = Output.getText();
    	Output.setText(ptr.generateNewText(settext));
    }
    @FXML protected void handleAddText(ActionEvent event) {
    	WebEngine webEngine = browser.getEngine();
    	webEngine.executeScript("clean()");
    	ptr = new BuildPtr();
    	ptr.init(ptr.getfile());
    	for(String name:ptr.myvec){
    		webEngine.executeScript(String.format("addNode('%s')",name));
    	}
    	for(int i = 0; i < ptr.myvec.size();i++){
    		for(int j = 0; j < ptr.myvec.size();j++){
    			if(ptr.myptr[i][j] <1000 )
    				webEngine.executeScript(String.format("addLink('%s','%s',%d)"
    				,ptr.myvec.get(i),ptr.myvec.get(j),ptr.myptr[i][j]));
    		}
    	}
    }
    @FXML protected void handleQuerybridge(ActionEvent event) {
    	WebEngine webEngine = browser.getEngine();
    	webEngine.executeScript("cleanPath()");
    	webEngine.executeScript("cleanNode()");
    	String src = Src.getText();
    	String dst = Dst.getText();
    	if(src != null && dst != null){
    		 for(int i:ptr.BridgeWords(src,dst)){
    			 webEngine.executeScript(String.format("focusNode('%s')",ptr.myvec.get(i)));
    		 }
    		 Output.setText(ptr.queryBridgeWords(src,dst));
    	}

    }
}
