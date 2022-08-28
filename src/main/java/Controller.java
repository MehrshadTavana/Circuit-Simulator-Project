import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {
    public static Scene scene;
    Stage stage=new Stage();
    String selecteditem=new String();
    static double order=1;
    static double order1=1;
    static double order2=1;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private NumberAxis xAxis1;
    @FXML private NumberAxis yAxis1;
    @FXML private NumberAxis xAxis2;
    @FXML private NumberAxis yAxis2;
    @FXML LineChart<Number,Number> lineChart1;
    @FXML LineChart<Number,Number> lineChart2;
    @FXML LineChart<Number,Number> lineChart3;
    public void deletetraces1(){
        lineChart1.getData().clear();
        lineChart3.getData().clear();
        lineChart2.getData().clear();
    }

    public void addExtratrace(){

        ObservableList<String> data= FXCollections.observableArrayList();
        ListView<String> listView=new ListView<String>(data);
        for (int i=0;i<Brain.everything.size();i++){
            if (Brain.everything.get(i) instanceof Node){
                data.add("Node "+((Node) Brain.everything.get(i)).name+":V");
            }
            if(Brain.everything.get(i) instanceof ISource){
                data.add("ISource "+((ISource) Brain.everything.get(i)).name+":V");
                data.add("ISource "+((ISource) Brain.everything.get(i)).name+":I");
                data.add("ISource "+((ISource) Brain.everything.get(i)).name+":P");
            }
            if(Brain.everything.get(i) instanceof VSource){
                data.add("VSource "+((VSource) Brain.everything.get(i)).name+":V");
                data.add("VSource "+((VSource) Brain.everything.get(i)).name+":I");
                data.add("VSource "+((VSource) Brain.everything.get(i)).name+":P");
            }
            if(Brain.everything.get(i) instanceof Resistor){
                data.add("Resistor "+((Resistor) Brain.everything.get(i)).name+":V");
                data.add("Resistor "+((Resistor) Brain.everything.get(i)).name+":I");
                data.add("Resistor "+((Resistor) Brain.everything.get(i)).name+":P");
            }
            if(Brain.everything.get(i) instanceof Capacitor){
                data.add("Capacitor "+((Capacitor) Brain.everything.get(i)).name+":V");
                data.add("Capacitor "+((Capacitor) Brain.everything.get(i)).name+":I");
                data.add("Capacitor "+((Capacitor) Brain.everything.get(i)).name+":P");
            }
            if(Brain.everything.get(i) instanceof Inductor){
                data.add("Inductor "+((Inductor) Brain.everything.get(i)).name+":V");
                data.add("Inductor "+((Inductor) Brain.everything.get(i)).name+":I");
                data.add("Inductor "+((Inductor) Brain.everything.get(i)).name+":P");
            }
        }
        listView.setItems(data);

        StackPane root=new StackPane();
        root.getChildren().add(listView);
        stage.setScene(new Scene(root,300,250));
        stage.show();
        initActions(listView,stage);
    }
    public void initActions(ListView<String> listView, Stage stage){
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount()>1){
                    selecteditem=listView.getSelectionModel().getSelectedItem();
                    stage.close();
                    String[] tokens = selecteditem.split(":");
                    if(tokens[1].equals("V")){
                        addExtraVoltagetrace();

                    }
                    else if(tokens[1].equals("I")){
                        addExtraCuurenttrace();
                    }
                    else if(tokens[1].equals("P")){
                        addExtraPowertrace();
                    }
                }
            }
        });
    }

    public void addExtraVoltagetrace(){
        String[] token=selecteditem.split("[: ]+");
        String unit=new String();
        reverser1();
        XYChart.Series<Number,Number> series=new XYChart.Series<Number, Number>();
        if(token[0].equals("Node")){

            for(Object object:Brain.everything){

                if(object instanceof Nodes){

                    if(((Nodes) object).name.equals(token[1])){
                        if(order<orderfinder(maximumfinder(((Nodes) object).vs))) {
                            order = orderfinder(maximumfinder(((Nodes) object).vs));
                            antireverser1();
                        }
                        else {
                            antireverser1();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Nodes) object).vs.get(((int)i))/order));


                        }
                        unit=unitfinder(order);
                    }
                }
            }
        }
        if(token[0].equals("Resistor")){
            for(Object object:Brain.everything){
                if(object instanceof Resistor){

                    if(((Resistor) object).name.equals(token[1])){
                        if(order<orderfinder(maximumfinder(((Resistor) object).vs))){
                            order=orderfinder(maximumfinder(((Resistor) object).vs));
                            antireverser1();
                        }
                        else {
                            antireverser1();
                        }

                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Resistor) object).vs.get(((int)i))/order));


                        }
                        unit=unitfinder(order);
                    }
                }
            }
        }
        else if(token[0].equals("Capacitor")){
            for(Object object:Brain.everything){
                if(object instanceof Capacitor){

                    if(((Capacitor) object).name.equals(token[1])){
                        if(order<orderfinder(maximumfinder(((Capacitor) object).vs))){
                            order=orderfinder(maximumfinder(((Capacitor) object).vs));
                            antireverser1();
                        }
                        else {
                            antireverser1();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Capacitor) object).vs.get(((int)i))/order));


                        }

                        unit=unitfinder(order);
                    }
                }
            }
        }
        else if(token[0].equals("ISource")){
            for(Object object:Brain.everything){
                if(object instanceof ISource){

                    if(((ISource) object).name.equals(token[1])){
                        if(order<orderfinder(maximumfinder(((ISource) object).vs))){
                            order=orderfinder(maximumfinder(((ISource) object).vs));
                            antireverser1();
                        }
                        else {
                            antireverser1();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((ISource) object).vs.get(((int)i))/order));


                        }

                        unit=unitfinder(order);
                    }
                }
            }
        }
        else if(token[0].equals("VSource")){
            for(Object object:Brain.everything){
                if(object instanceof VSource){

                    if(((VSource) object).name.equals(token[1])){
                        if(order<orderfinder(maximumfinder(((VSource) object).vs))){
                            order=orderfinder(maximumfinder(((VSource) object).vs));
                            antireverser1();
                        }
                        else {
                            antireverser1();
                        }

                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((VSource) object).vs.get(((int)i))/order));


                        }
                        unit=unitfinder(order);
                    }
                }
            }
        }
        else if(token[0].equals("Inductor")){
            for(Object object:Brain.everything){
                if(object instanceof Inductor){

                    if(((Inductor) object).name.equals(token[1])){
                        if(order<orderfinder(maximumfinder(((Inductor) object).vs))) {
                            order = orderfinder(maximumfinder(((Inductor) object).vs));
                            antireverser1();
                        }
                        else {
                            antireverser1();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Inductor) object).vs.get(((int)i))/order));


                        }

                        unit=unitfinder(order);
                    }
                }
            }
        }
        series.setName("  V:"+token[1]+"  ");
        lineChart1.getData().add(series);
        yAxis.setLabel("Voltage("+unit+"V)");
    }
    public void reverser1(){
        for(XYChart.Series<Number,Number> series:lineChart1.getData()){
            for (int i=0;i<series.getData().size();i++){
                series.getData().get(i).setYValue((double)series.getData().get(i).getYValue()*order);
            }
        }
    }
    public void antireverser1(){
        for(XYChart.Series<Number,Number> series:lineChart1.getData()){
            for (int i=0;i<series.getData().size();i++){
                series.getData().get(i).setYValue((double)series.getData().get(i).getYValue()/order);
            }
        }
    }
    public void addExtraCuurenttrace(){
        String[] token=selecteditem.split("[: ]+");
        String unit=new String();
        reverser2();
        XYChart.Series<Number,Number> series=new XYChart.Series<Number, Number>();

        if(token[0].equals("Resistor")){
            for(Object object:Brain.everything){
                if(object instanceof Resistor){

                    if(((Resistor) object).name.equals(token[1])){
                        if(order1<orderfinder(maximumfinder(((Resistor) object).is))){
                            order1=orderfinder(maximumfinder(((Resistor) object).is));
                            antireverser2();
                        }
                        else {
                            antireverser2();
                        }

                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Resistor) object).is.get(((int)i))/order1));


                        }
                        unit=unitfinder(order1);
                    }
                }
            }
        }
        else if(token[0].equals("Capacitor")){
            for(Object object:Brain.everything){
                if(object instanceof Capacitor){

                    if(((Capacitor) object).name.equals(token[1])){
                        if(order1<orderfinder(maximumfinder(((Capacitor) object).is))){
                            order1=orderfinder(maximumfinder(((Capacitor) object).is));
                            antireverser2();
                        }
                        else {
                            antireverser2();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Capacitor) object).is.get(((int)i))/order1));


                        }
                        unit=unitfinder(order1);
                    }
                }
            }
        }
        else if(token[0].equals("ISource")){
            for(Object object:Brain.everything){
                if(object instanceof ISource){

                    if(((ISource) object).name.equals(token[1])){
                        if(order1<orderfinder(maximumfinder(((ISource) object).is))){
                            order1=orderfinder(maximumfinder(((ISource) object).is));
                            antireverser2();
                        }
                        else {
                            antireverser2();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((ISource) object).is.get(((int)i))/order1));


                        }
                        unit=unitfinder(order1);
                    }
                }
            }
        }
        else if(token[0].equals("VSource")){
            for(Object object:Brain.everything){
                if(object instanceof VSource){

                    if(((VSource) object).name.equals(token[1])){
                        if(order1<orderfinder(maximumfinder(((VSource) object).is))){
                            order1=orderfinder(maximumfinder(((VSource) object).is));
                            antireverser2();
                        }
                        else {
                            antireverser2();
                        }

                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((VSource) object).is.get(((int)i))/order1));


                        }
                        unit=unitfinder(order1);
                    }
                }
            }
        }
        else if(token[0].equals("Inductor")){
            for(Object object:Brain.everything){
                if(object instanceof Inductor){

                    if(((Inductor) object).name.equals(token[1])){
                        if(order<orderfinder(maximumfinder(((Inductor) object).is))) {
                            order = orderfinder(maximumfinder(((Inductor) object).is));
                            antireverser2();
                        }
                        else {
                            antireverser2();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Inductor) object).is.get(((int)i))/order1));


                        }
                        unit=unitfinder(order1);
                    }
                }
            }
        }
        series.setName("  I:"+token[1]+"  ");
        lineChart2.getData().add(series);
        yAxis1.setLabel("Current("+unit+"A)");
    }
    public void reverser2(){
        for(XYChart.Series<Number,Number> series:lineChart2.getData()){
            for (int i=0;i<series.getData().size();i++){
                series.getData().get(i).setYValue((double)series.getData().get(i).getYValue()*order1);
            }
        }
    }
    public void antireverser2(){
        for(XYChart.Series<Number,Number> series:lineChart2.getData()){
            for (int i=0;i<series.getData().size();i++){
                series.getData().get(i).setYValue((double)series.getData().get(i).getYValue()/order1);
            }
        }
    }
    public void addExtraPowertrace(){
        String[] token=selecteditem.split("[: ]+");
        String unit=new String();
        reverser3();
        XYChart.Series<Number,Number> series=new XYChart.Series<Number, Number>();

            for(Object object:Brain.everything){
                if(object instanceof Element){

                    if(((Element) object).name.equals(token[1])){
                        if(order2<orderfinder(maximumfinder(((Element) object).ps))) {
                            order2 = orderfinder(maximumfinder(((Element) object).ps));
                            antireverser3();
                        }
                        else {
                            antireverser3();
                        }
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Element) object).ps.get((int) i)/order2));


                        }
                        unit=unitfinder(order2);
                    }
                }
            }


        series.setName("  P:"+token[1]+"  ");
        lineChart3.getData().add(series);
        yAxis2.setLabel("Power("+unit+"W)");
    }
    public void reverser3(){
        for(XYChart.Series<Number,Number> series:lineChart3.getData()){
            for (int i=0;i<series.getData().size();i++){
                series.getData().get(i).setYValue((double)series.getData().get(i).getYValue()*order2);
            }
        }
    }
    public void antireverser3(){
        for(XYChart.Series<Number,Number> series:lineChart3.getData()){
            for (int i=0;i<series.getData().size();i++){
                series.getData().get(i).setYValue((double)series.getData().get(i).getYValue()/order2);
            }
        }
    }
    public void btn() throws IOException {
        lineChart1.getData().clear();
        xAxis.setAutoRanging(false);
        XYChart.Series<Number,Number> series=new XYChart.Series<Number, Number>();
//        for (double i=0;i<Brain.time/Brain.dt;i++){
//            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt,((Resistor)Brain.everything.get(5)).vs.get(((int)i))));
//        }
        addVoltage(ControllerMainPage.voltagename,series);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Brain.time/orderfinder(Brain.time));
        xAxis.setTickUnit(Brain.time/orderfinder(Brain.time)/10);
        lineChart1.getData().add(series);

    }


    public void btn1(){
        lineChart2.getData().clear();
        xAxis1.setAutoRanging(false);
        XYChart.Series<Number,Number> series=new XYChart.Series<Number, Number>();
//        for (double i=0;i<Brain.time/Brain.dt;i++){
//            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt,((Resistor)Brain.everything.get(5)).is.get(((int)i))));
//        }
        addCurrent(ControllerMainPage.currentname,series);
        xAxis1.setLowerBound(0);
        xAxis1.setUpperBound(Brain.time/orderfinder(Brain.time));
        xAxis1.setTickUnit(Brain.time/orderfinder(Brain.time)/10);
        lineChart2.getData().add(series);
    }

    public void btn2(){
        lineChart3.getData().clear();
        xAxis2.setAutoRanging(false);
        XYChart.Series<Number,Number> series=new XYChart.Series<Number, Number>();
//        for (double i=0;i<Brain.time/Brain.dt;i++){
//            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt,((Resistor)Brain.everything.get(5)).vs.get(((int)i))*((Resistor)Brain.everything.get(5)).is.get(((int)i))));
//        }
        addPower(ControllerMainPage.powername,series);
        xAxis2.setLowerBound(0);
        xAxis2.setUpperBound(Brain.time/orderfinder(Brain.time));
        xAxis2.setTickUnit(Brain.time/orderfinder(Brain.time)/10);
        lineChart3.getData().add(series);
    }
    public void addVoltage(String voltageorder,XYChart.Series<Number,Number> series){
        String[] token = voltageorder.split("[: ]+");
        String unit="";
        if(token[0].equals("Node")){
            for(Object object:Brain.everything){
                if(object instanceof Nodes){
                    if(((Nodes) object).name.equals(token[1])){
                        order= orderfinder(maximumfinder(((Nodes) object).vs));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Nodes) object).vs.get(((int)i))/orderfinder(maximumfinder(((Nodes) object).vs))));
                            unit=unitfinder(orderfinder(maximumfinder(((Nodes) object).vs)));
                        }
                    }
                }
            }
        }
        if(token[0].equals("Resistor")){
            for(Object object:Brain.everything){
                if(object instanceof Resistor){
                    if(((Resistor) object).name.equals(token[1])){
                        order=orderfinder(maximumfinder(((Resistor) object).vs));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Resistor) object).vs.get(((int)i))/orderfinder(maximumfinder(((Resistor) object).vs))));
                            unit=unitfinder(orderfinder(maximumfinder(((Resistor) object).vs)));
                        }
                    }
                }
            }
        }
        else if(token[0].equals("Capacitor")){
            for(Object object:Brain.everything){
                if(object instanceof Capacitor){
                    order=orderfinder(maximumfinder(((Capacitor) object).vs));
                    if(((Capacitor) object).name.equals(token[1])){
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Capacitor) object).vs.get(((int)i))/orderfinder(maximumfinder(((Capacitor) object).vs))));
                            unit=unitfinder(orderfinder(maximumfinder(((Capacitor) object).vs)));
                        }
                    }
                }
            }
        }
        else if(token[0].equals("ISource")){

            for(Object object:Brain.everything){

                if(object instanceof ISource){
                    order=orderfinder(maximumfinder(((ISource) object).vs));
                    if(((ISource) object).name.equals(token[1])){
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((ISource) object).vs.get(((int)i))/orderfinder(maximumfinder(((ISource) object).vs))));
                            unit=unitfinder(orderfinder(maximumfinder(((ISource) object).vs)));
                        }
                    }
                }
            }
        }
        else if(token[0].equals("VSource")){
            for(Object object:Brain.everything){
                if(object instanceof VSource){
                    order=orderfinder(maximumfinder(((VSource) object).vs));
                    if(((VSource) object).name.equals(token[1])){
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((VSource) object).vs.get(((int)i))/orderfinder(maximumfinder(((VSource) object).vs))));
                            unit=unitfinder(orderfinder(maximumfinder(((VSource) object).vs)));
                        }
                    }
                }
            }
        }
        else if(token[0].equals("Inductor")){
            for(Object object:Brain.everything){
                if(object instanceof Inductor){
                    order=orderfinder(maximumfinder(((Inductor) object).vs));
                    if(((Inductor) object).name.equals(token[1])){
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Inductor) object).vs.get(((int)i))/orderfinder(maximumfinder(((Inductor) object).vs))));
                            unit=unitfinder(orderfinder(maximumfinder(((Inductor) object).vs)));
                        }
                    }
                }
            }
        }
        String unit1=unitfinder(orderfinder(Brain.time));
        series.setName("  V:"+token[1]+"  ");
        yAxis.setLabel("Voltage("+unit+"V)");
        xAxis1.setLabel("time("+unit1+"s)");
        xAxis.setLabel("time("+unit1+"s)");
        xAxis2.setLabel("time("+unit1+"s)");
    }
    public double orderfinder(double number){
        double order=1;
        if(number>=1){
            while (order<=Math.pow(10,12)&&!(number>=1&&number<1000)){
                number/=1000;
                order*=1000;
            }
        }
        else if (number<1) {
            while (order>=Math.pow(10,-15)&&!(number>=1&&number<1000)){
                number*=1000;
                order/=1000;
            }

        }
        return order;
    }
    public double maximumfinder(ArrayList<Double>  arrayList){
        double maximum=0;
        for(int i=0;i<arrayList.size();i++){
            if(Math.abs(arrayList.get(i))>maximum)
                maximum=Math.abs(arrayList.get(i));
        }
        return maximum;
    }
    public String unitfinder(double number){
        String unit=new String();
        if(number>=Math.pow(10,12)){
            unit="T";
        }
        else if(number==Math.pow(10,9)){
            unit="G";
        }
        else if(number==Math.pow(10,6)){
            unit="M";
        }
        else if(number==Math.pow(10,3)){
            unit="k";
        }
        else if(number>Math.pow(10,-6)&&number<=Math.pow(10,-3)){
            unit="m";
        }
        else if(number>Math.pow(10,-9)&&number<=Math.pow(10,-6)){
            unit="u";
        }
        else if(number>Math.pow(10,-12)&&number<=Math.pow(10,-9)){
            unit="n";
        }
        else if(number>Math.pow(10,-15)&&number<=Math.pow(10,-12)){
            unit="p";
        }
        else if(number<=Math.pow(10,-15)){
            unit="f";
        }

        return  unit;
    }
    public void addCurrent(String currentorder,XYChart.Series<Number,Number> series){
        String[] token = currentorder.split("[: ]+");
        String unit="";
        if(token[0].equals("Resistor")){
            for(Object object:Brain.everything){
                if(object instanceof Resistor){
                    if(((Resistor) object).name.equals(token[1])){
                        order1= orderfinder(maximumfinder(((Resistor) object).is));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Resistor) object).is.get(((int)i))/orderfinder(maximumfinder(((Resistor) object).is))));
                            unit=unitfinder(orderfinder(maximumfinder(((Resistor) object).is)));
                        }
                    }
                }
            }
        }
        if(token[0].equals("Capacitor")){
            for(Object object:Brain.everything){
                if(object instanceof Capacitor){
                    if(((Capacitor) object).name.equals(token[1])){
                        order1= orderfinder(maximumfinder(((Capacitor) object).is));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Capacitor) object).is.get(((int)i))/orderfinder(maximumfinder(((Capacitor) object).is))));
                            unit=unitfinder(orderfinder(maximumfinder(((Capacitor) object).is)));
                        }
                    }
                }
            }
        }
        if(token[0].equals("ISource")){
            for(Object object:Brain.everything){
                if(object instanceof ISource){
                    if(((ISource) object).name.equals(token[1])){
                        order1= orderfinder(maximumfinder(((ISource) object).is));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((ISource) object).is.get(((int)i))/orderfinder(maximumfinder(((ISource) object).is))));
                            unit=unitfinder(orderfinder(maximumfinder(((ISource) object).is)));
                        }
                    }
                }
            }
        }
        if(token[0].equals("VSource")){
            for(Object object:Brain.everything){
                if(object instanceof VSource){
                    if(((VSource) object).name.equals(token[1])){
                        order1= orderfinder(maximumfinder(((VSource) object).is));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((VSource) object).is.get(((int)i))/orderfinder(maximumfinder(((VSource) object).is))));
                            unit=unitfinder(orderfinder(maximumfinder(((VSource) object).is)));
                        }
                    }
                }
            }
        }
        if(token[0].equals("Inductor")){
            for(Object object:Brain.everything){
                if(object instanceof Inductor){
                    if(((Inductor) object).name.equals(token[1])){
                        order1= orderfinder(maximumfinder(((Inductor) object).is));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Inductor) object).is.get(((int)i))/orderfinder(maximumfinder(((Inductor) object).is))));
                            unit=unitfinder(orderfinder(maximumfinder(((Inductor) object).is)));
                        }
                    }
                }
            }
        }
        series.setName("  I:"+token[1]+"  ");
        yAxis1.setLabel("Current("+unit+"A)");
    }
    public void addPower(String powereorder,XYChart.Series<Number,Number> series){
        String[] token = powereorder.split("[: ]+");
        String unit="";

            for(Object object:Brain.everything){
                if(object instanceof Element){
                    if(((Element) object).name.equals(token[1])){
                        order2= orderfinder(maximumfinder(((Element) object).ps));
                        for (double i=0;i<(int)(Brain.time/Brain.dt);i++){
                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt/orderfinder(Brain.time),((Element) object).ps.get((int)i)/orderfinder(maximumfinder(((Element) object).ps))));
                            unit=unitfinder(orderfinder(maximumfinder(((Element) object).ps)));
                        }
                    }
                }
            }

//        if(token[0].equals("Capacitor")){
//            for(Object object:Brain.everything){
//                if(object instanceof Capacitor){
//                    if(((Capacitor) object).name.equals(token[1])){
//                        order2= orderfinder(maximumfinder(((Capacitor) object).is)*maximumfinder(((Capacitor) object).vs));
//                        for (double i=0;i<Brain.time/Brain.dt;i++){
//                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt,((Capacitor) object).vs.get(((int)i))*((Capacitor) object).is.get((int)i)/(orderfinder(maximumfinder(((Capacitor) object).is))*orderfinder(maximumfinder(((Capacitor) object).vs)))));
//                            unit=unitfinder(orderfinder(maximumfinder(((Capacitor) object).is))*orderfinder(maximumfinder(((Capacitor) object).vs)));}
//                    }
//                }
//            }
//        }
//        if(token[0].equals("ISource")){
//            for(Object object:Brain.everything){
//                if(object instanceof ISource){
//                    if(((ISource) object).name.equals(token[1])){
//                        order2= orderfinder(maximumfinder(((ISource) object).is)*maximumfinder(((ISource) object).vs));
//                        for (double i=0;i<Brain.time/Brain.dt;i++){
//                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt,((ISource) object).vs.get(((int)i))*((ISource) object).is.get((int)i)/(orderfinder(maximumfinder(((ISource) object).is))*orderfinder(maximumfinder(((ISource) object).vs)))));
//                            unit=unitfinder(orderfinder(maximumfinder(((ISource) object).is))*orderfinder(maximumfinder(((ISource) object).vs)));}
//                    }
//                }
//            }
//        }
//        if(token[0].equals("VSource")){
//            for(Object object:Brain.everything){
//                if(object instanceof VSource){
//                    if(((VSource) object).name.equals(token[1])){
//                        order2= orderfinder(maximumfinder(((VSource) object).is)*maximumfinder(((VSource) object).vs));
//                        for (double i=0;i<Brain.time/Brain.dt;i++){
//                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt,((VSource) object).vs.get(((int)i))*((VSource) object).is.get((int)i)/(orderfinder(maximumfinder(((VSource) object).is))*orderfinder(maximumfinder(((VSource) object).vs)))));
//                            unit=unitfinder(orderfinder(maximumfinder(((VSource) object).is))*orderfinder(maximumfinder(((VSource) object).vs)));}
//                    }
//                }
//            }
//        }
//        if(token[0].equals("Inductor")){
//            for(Object object:Brain.everything){
//                if(object instanceof Inductor){
//                    if(((Inductor) object).name.equals(token[1])){
//                        order2= orderfinder(maximumfinder(((Inductor) object).is)*maximumfinder(((Inductor) object).vs));
//                        for (double i=0;i<Brain.time/Brain.dt;i++){
//                            series.getData().add(new XYChart.Data<Number, Number>(i*Brain.dt,((Inductor) object).vs.get(((int)i))*((Inductor) object).is.get((int)i)/(orderfinder(maximumfinder(((Inductor) object).is))*orderfinder(maximumfinder(((Inductor) object).vs)))));
//                            unit=unitfinder(orderfinder(maximumfinder(((Inductor) object).is))*orderfinder(maximumfinder(((Inductor) object).vs)));}
//                    }
//                }
//            }

        series.setName("  P:"+token[1]+"  ");
        yAxis2.setLabel("Power("+unit+"W)");

    }



    public void saveAs(ActionEvent actionEvent) {
        Stage stage=new Stage();
        FileChooser fileChooser = new FileChooser();
        WritableImage image = lineChart1.snapshot(new SnapshotParameters(),new WritableImage(1300,700));
        WritableImage image1 = lineChart2.snapshot(new SnapshotParameters(),new WritableImage(1300,700));
        WritableImage image2 = lineChart3.snapshot(new SnapshotParameters(),new WritableImage(1300,700));
        fileChooser.setInitialDirectory(new File("C:\\Users\\228al\\Desktop"));
        fileChooser.setTitle("Select Voltage Trace Path To Save");
        File file = fileChooser.showSaveDialog(stage);
        try{

            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);


        }
        catch (IOException e){

        }
        fileChooser.setTitle("Select Current Trace Path To Save");
        file = fileChooser.showSaveDialog(stage);
        try{

            ImageIO.write(SwingFXUtils.fromFXImage(image1, null), "png", file);


        }
        catch (IOException e){

        }
        fileChooser.setTitle("Select Power Trace Path To Save");
        file = fileChooser.showSaveDialog(stage);
        try{

            ImageIO.write(SwingFXUtils.fromFXImage(image2, null), "png", file);


        }
        catch (IOException e){

        }




    }
    public void exit(){
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            btn();
            btn1();
            btn2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
