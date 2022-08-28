import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Brain {


    ////////////////////////////////////////////////////// Simulator from here

    static ArrayList<Object> everything=new ArrayList<>();
    static double time,t=0,dt=-1,dV=-1,dI=-1,i=0;
    static int error=0;
    boolean errorCheck;


    // TODO: 20/07/19 check if it's temp
    public static void manageFile(){
        InputManager manager=InputManager.getInstance();
        everything.clear();
        dt=-1;dV=-1;dI=-1;i=0;
        Nodes.nodes.clear();
        Element.elements.clear();
        Ground ground=Ground.getInstance();
        ground.elements.clear();
        ground.neighbours.clear();
        Nodes.checkGraph=false;
        Scanner scanner=null;
        File inputFile=new File(Main.path);

        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line=scanner.nextLine(); //first line khonde nashe
        int nLine=1; //1
        Pattern pattern=Pattern.compile("\\.tran\\s+(\\d+\\.?\\d*)([pnumkMGx]?)$");
        Matcher matcher;
        boolean tran=false;
        while (scanner.hasNextLine()&&!tran){
            line=scanner.nextLine();
            ++nLine;
            matcher=pattern.matcher(line);
            if (matcher.find()) {
                setTime(matcher.group(1),matcher.group(2));
                tran=true;
                break;
            }
            if (line.charAt(0)!='*'){
                manager.setInput(line);
                if (!manager.checkInputFormat()) {
                    //Main.ErrorBox("ERROR -1"," line "+nLine );
                    //System.out.println("Error -1 ( line "+nLine+" )");
                }
            }
        }
    }

    public static void simulateFile(Label percentage,ProgressBar bar){
        error=0;
        InputManager manager=InputManager.getInstance();
        everything.clear();
        dt=-1;dV=-1;dI=-1;i=0;
        Nodes.nodes.clear();
        Element.elements.clear();
        Ground ground=Ground.getInstance();
        ground.elements.clear();
        ground.neighbours.clear();
        Nodes.checkGraph=false;
        Scanner scanner=null;
        File inputFile=new File(Main.path);

        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line=scanner.nextLine(); //first line khonde nashe
        int nLine=1; //1
        Pattern pattern=Pattern.compile("\\.tran\\s+(\\d+\\.?\\d*)([pnumkMGx]?)$");
        Matcher matcher;
        boolean tran=false;
        while (scanner.hasNextLine()&&!tran){
            line=scanner.nextLine();
            ++nLine;
            matcher=pattern.matcher(line);
            if (matcher.find()) {
                setTime(matcher.group(1),matcher.group(2));
                tran=true;
                break;
            }
            if (line.charAt(0)!='*'){
                manager.setInput(line);
                if (!manager.checkInputFormat()) {
                    Main.ErrorBox("ERROR -1"," line "+nLine );
                    error=1;
                    //System.out.println("Error -1 ( line "+nLine+" )");
                    //System.exit(0); // TODO: 20/07/19
                }
            }
        }
        if (dV==-1||dI==-1||dt==-1||!tran){
            Main.ErrorBox("ERROR -1"," dT, dV or dI has not been initialized ");
            error=1;
            //System.out.println("Error -1");
            //System.exit(0); // TODO: 20/07/19
        }
        //Nodes.updateNeighbourNodes();
        /*for (Object o : everything){
            if (o instanceof Node){
                for(Element e : ((Node) o).elements){
                    System.out.println(e.otherNode((Nodes) o).name);
                }
                System.out.println("---------------------------");
            }
        }*/
        t=0;
        if (error==0){
            checkExistenceOfGND();
            checkValueOfGND();
            checkISourceVSource();
            checkFloating();
            for (i =0 ; i<time  ; i+=dt){
                t=i;
                calculateVoltageAtT();
                checkValueOfGND();
                checkVSource();
                checkISourse();
                if (error==1){
                    Element.reset();
                    break;
                }
                for (Object o: Brain.everything){
                    if (o instanceof Nodes){
                        System.out.println(((Nodes) o).name+" : "+((Nodes) o).v);
                    }
                /*if (o instanceof Inductor){
                    System.out.println(((Inductor) o).name+" : "+((Inductor) o).getI(((Inductor) o).node[0]));
                }*/
                }
                System.out.println("T : "+i);
                if (((int)(i/dt)%25==0))
                {
                    bar.setVisible(true);
                    percentage.setVisible(true);
                    double p=   ((i)/time);
                    String settext = String.format("%.1f",p);
                    percentage.setText(settext+"%");
                    bar.setProgress(p);

                }
                System.out.println("--------------------------");

            }
        }
        if(error==0){
            printAll();
        }
    }

    public static void setTime(String t,String p){
        int power=0;
        if (p.equals("p"))
            power=-12;
        if (p.equals("n"))
            power=-9;
        if (p.equals("u"))
            power=-6;
        if (p.equals("m"))
            power=-3;
        if (p.equals("k"))
            power=3;
        if (p.equals("M")||p.equals("x"))
            power=6;
        if (p.equals("G"))
            power=9;
        time=Double.parseDouble(t)*Math.pow(10,power);
    }
    ////---------------   calculate voltage of nodes in t=T
    public static void calculateVoltageAtT(){
        double temp=0;
        double Itotal1=0,Itotal2=0,Itotal4=0;
        int counter=0;
        checkISourceVSource();
        while (true){
            ////-------------         reset all nodes
            for (Object o: Brain.everything){
                if (o instanceof Nodes){
                    Nodes.resetNodes();
                }
                if(o instanceof Diode1){
                    if(((Diode1) o).node[1].v>((Diode1) o).node[0].v){
                        if(((Diode1) o).node[1].name.equals("0")){
                            ((Diode1) o).node[0].v = ((Diode1) o).node[1].v;
                        }
                        else {
                            ((Diode1) o).node[1].v = ((Diode1) o).node[0].v;
                        }
                    }
                }
                if(o instanceof VSource){
                    if(((VSource) o).node[1].name.equals("0")){
                        ((VSource) o).node[0].v = ((VSource) o).node[1].v+((VSource) o).getV(((VSource) o).node[0]);
                    }
                    else {
                        int temporary=0;
                        for (Element element:((VSource) o).node[0].elements){
                            if(element instanceof ISource){
                                temporary++;
                            }
                        }
                        if(temporary==0){
                            ((VSource) o).node[1].v = ((VSource) o).node[0].v-((VSource) o).getV(((VSource) o).node[0]);
                        }
                        else {
                            ((VSource) o).node[0].v = ((VSource) o).node[1].v+((VSource) o).getV(((VSource) o).node[0]);
                        }
                    }
                }
            }
            for (Object o: Brain.everything){
                if (o instanceof Nodes){
                    if(((Nodes) o).name.equals("0")||((Nodes) o).name.equals("gnd")){
                        ((Nodes) o).visited=true;
                    }
                }
            }
            ////-------------         reset all nodes
            ////-------------         visiting all nodes
            counter++;
            for (Object o: Brain.everything) {
                if (o instanceof Nodes) {
                    if (!((Nodes) o).visited) {
                        Itotal1 = ((Nodes) o).getTotalI((Nodes) o);
                        //System.out.println("-----------------------------");
                        //System.out.println("V : "+((Nodes) o).v);
                        ((Nodes) o).v += Brain.dV;
                        Itotal2 = ((Nodes) o).getTotalI((Nodes) o);
                        ((Nodes) o).v -= Brain.dV;
                        //((Nodes) o).v -=2*Main.dV;
                        //Itotal4 = ((Nodes) o).getTotalI((Nodes) o);
                        //((Nodes) o).v += Main.dV;
                        //System.out.println("Node : "+((Nodes) o).name);
                        //System.out.println("I1 : "+Itotal1);
                        //System.out.println("I2 : "+Itotal2);
                        //System.out.println("I4 : "+Itotal4);
                        temp=((Nodes) o).v;
                        if(counter%10000==0||counter%10000==1){
                            //System.out.println("-----------------------------");
                            //System.out.println("V : "+((Nodes) o).v);
                            ((Nodes) o).v += Brain.dV;
                            //System.out.println("Node : "+((Nodes) o).name);
                            //System.out.println("I1 : "+Itotal1);
                            //System.out.println("I2 : "+Itotal2);
                            for (Object object : everything){
                                if (object instanceof Element){
                                   // System.out.println(((Element) object).name+" I : "+((Element) object).getI(((Element) object).node[0]));
                                }
                            }
                        }
                        ((Nodes) o).v = ((Nodes) o).v + (Math.abs(Itotal1) - Math.abs(Itotal2))/Brain.dI * Brain.dV;
                        if(counter%10000==0||counter%10000==1){
                            //System.out.println("V : "+((Nodes) o).v);
                        }
                        temp=((Nodes) o).v-temp;
                        int temporary=0,temporary1=0;
                        for (Element element:((Nodes) o).elements){
                            if(element instanceof Capacitor){
                                temporary++;
                            }
                            if (element instanceof Diode2){
                                temporary1++;
                            }
                        }
                        if (temporary>0){
                            Itotal4 = ((Nodes) o).getTotalI((Nodes) o);
                            //System.out.println("I4 : "+Itotal4);
                            while ((Math.abs(Itotal4)>Math.abs(Itotal1))&&Math.abs(Itotal4)>Brain.dI&&Math.abs(temp)>Math.pow(10,-13)){
                                //System.out.println("-----------------------------");
                                //System.out.println("V : "+((Nodes) o).v);
                                //System.out.println("Node : "+((Nodes) o).name);
                                //System.out.println("Temp : "+temp);
                                //System.out.println("I4 : "+Itotal4);
                                temp/=2;
                                ((Nodes) o).v-=temp;
                                Itotal4 = ((Nodes) o).getTotalI((Nodes) o);
                            }
                        }
                        if (temporary1>0){
                            int x,y=0;
                            double w;
                            if (temp>0)
                                x=1;
                            else
                                x=-1;
                            Itotal4 = ((Nodes) o).getTotalI((Nodes) o);
                            w=Itotal4;
                            if(Math.abs(Itotal4)>Math.abs(Itotal1)){
                                //System.out.println("I4 : "+Itotal4);
                                while ((Math.abs(Itotal4)>Math.abs(Itotal1))&&Math.abs(Itotal4)>Brain.dI&&Math.abs(temp)>dV){
                                    //System.out.println("-----------------------------");
                                    //System.out.println("V : "+((Nodes) o).v);
                                    //System.out.println("Node : "+((Nodes) o).name);
                                    //System.out.println("Temp : "+temp);
                                    //System.out.println("I4 : "+Itotal4);
                                    temp/=2;
                                    ((Nodes) o).v-=temp;
                                    Itotal4 = ((Nodes) o).getTotalI((Nodes) o);
                                }
                            }
                            else {
                                ((Nodes) o).v+=x*dV;
                                Itotal4 = ((Nodes) o).getTotalI((Nodes) o);
                                if (counter%10000==0||counter%10000==1){
                                    //System.out.println("I4 : "+Itotal4);
                                    //System.out.println("x : "+x);
                                }
                                while (Math.abs(Itotal4)>Brain.dI&&Math.abs(w)>Math.abs(Itotal4)){
                                    y++;
                                    w=Itotal4;
                                    ((Nodes) o).v+=x*dV;
                                    //System.out.println("-----------------------------");
                                    //System.out.println("V : "+((Nodes) o).v);
                                    //System.out.println("Node : "+((Nodes) o).name);
                                    //System.out.println("Temp : "+temp);
                                    if (y%10000==0){
                                        //System.out.println("I4 : "+Itotal4);
                                    }
                                    Itotal4 = ((Nodes) o).getTotalI((Nodes) o);
                                }
                                ((Nodes) o).v-=x*dV;
                            }
                        }
                        ((Nodes) o).visited = true;
                    }
                }
            }
            ////-------------         visiting all nodes
            ////--------------------------------------
            for (Object o: Brain.everything){
                if(o instanceof Diode1){
                    if(((Diode1) o).node[1].v>((Diode1) o).node[0].v){
                        if(((Diode1) o).node[1].name.equals("0")){
                            ((Diode1) o).node[0].v = ((Diode1) o).node[1].v;
                        }
                        else {
                            ((Diode1) o).node[1].v = ((Diode1) o).node[0].v;
                        }
                    }
                }
                if(o instanceof VSource){
                    if(((VSource) o).node[1].name.equals("0")){
                        ((VSource) o).node[0].v = ((VSource) o).node[1].v+((VSource) o).getV(((VSource) o).node[0]);
                    }
                    else {
                        int temporary=0;
                        for (Element element:((VSource) o).node[0].elements){
                            if(element instanceof ISource){
                                temporary++;
                            }
                        }
                        if(temporary==0){
                            ((VSource) o).node[1].v = ((VSource) o).node[0].v-((VSource) o).getV(((VSource) o).node[0]);
                        }
                        else {
                            ((VSource) o).node[0].v = ((VSource) o).node[1].v+((VSource) o).getV(((VSource) o).node[0]);
                        }
                    }
                }
            }
            ////--------------------------------------
            ////-------------     total input currents of all nodes is less than 0.01 so exits from the while
            temp =0;
            //System.out.println("temp : "+temp);
            for (Object o: Brain.everything){
                if (o instanceof Nodes){
                    double I3=((Nodes) o).getTotalI((Nodes) o);
                    //System.out.println("-----------------------------");
                    //System.out.println("V : "+((Nodes) o).v);
                    //System.out.println("Node : "+((Nodes) o).name);
                    //System.out.println("I3 : " +I3);
                    if(I3>Brain.dI||I3<-Brain.dI){
                        temp++;
                    }
                }
            }
            //System.out.println("temp : "+temp);
            if(temp==0){
                Element.update();
                Nodes.updateNodes();
                /*for (Object o: Main.everything){
                    if (o instanceof Element){
                        Element.update();
                    }
                }
                for (Object o: Main.everything){
                    if (o instanceof Nodes){
                        Nodes.updateNodes();
                    }
                }*/
                break;
            }
            ////-------------     total input currents of all nodes is less than 0.01 so exit from the while
        }
    }
    ////---------------   calculate voltage of nodes in t=T
    public static void printAll(){
        try{
            FileWriter fileout=new FileWriter("output.txt",false);
            int counter=0;
            for(Object object:everything){
                if (object instanceof Node)
                    counter++;
            }
                for (Object object1:everything) {
                    if (object1 instanceof Node) {

                        String nodeoutput = String.format("Node %s: ", ((Node) object1).name);
                        fileout.write(nodeoutput);
                        for (int i = 0; i < ((Node) object1).vs.size(); i++) {
                            fileout.write(((Node) object1).vs.get(i) + "\t");
                        }
                        fileout.write("\n");
                    }
                }
            fileout.write("------------\n");
            for (Object object:everything){
                if (object instanceof ISource){
                    String currentsourceoutput=String.format("ISource %s: ",((ISource) object).name);
                    fileout.write(currentsourceoutput);
                    for(int i=0;i<((ISource) object).vs.size();i++){
                        fileout.write("("+((ISource) object).vs.get(i)+" "+((ISource) object).is.get(i)+" "+((ISource) object).vs.get(i)*((ISource) object).is.get(i)+")\t");
                    }
                    fileout.write("\n");
                }
                if (object instanceof VSource){
                    String currentsourceoutput=String.format("VSource %s: ",((VSource) object).name);
                    fileout.write(currentsourceoutput);
                    for(int i=0;i<((VSource) object).vs.size();i++){
                        fileout.write("("+((VSource) object).vs.get(i)+" "+((VSource) object).is.get(i)+" "+((VSource) object).vs.get(i)*((VSource) object).is.get(i)+")\t");
                    }
                    fileout.write("\n");
                }
                if(object instanceof Resistor){
                    String  resistoroutput=String.format("Resistor %s: ",((Resistor) object).name);
                    fileout.write(resistoroutput);
                    for(int i=0;i<((Resistor) object).vs.size();i++){
                        fileout.write("("+((Resistor) object).vs.get(i)+" "+((Resistor) object).is.get(i)+" "+((Resistor) object).vs.get(i)*((Resistor) object).is.get(i)+")\t");
                    }
                    fileout.write("\n");
                }
                if(object instanceof Capacitor){
                    String capacitoroutput=String.format("Capacitor %s: ",((Capacitor) object).name);
                    fileout.write(capacitoroutput);
                    for(int i=0;i<((Capacitor) object).vs.size();i++){
                        fileout.write("("+((Capacitor) object).vs.get(i)+" "+((Capacitor) object).is.get(i)+" "+((Capacitor) object).vs.get(i)*((Capacitor) object).is.get(i)+")\t");
                    }
                    fileout.write("\n");
                }
                if (object instanceof Inductor ){
                    String inductoroutput=String.format("Inductor %s: ",((Inductor) object).name);
                    fileout.write(inductoroutput);
                    for(int i=0;i<((Inductor) object).vs.size();i++){
                        fileout.write("("+((Inductor) object).vs.get(i)+" "+((Inductor) object).is.get(i)+" "+((Inductor) object).vs.get(i)*((Inductor) object).is.get(i)+")\t");
                    }
                    fileout.write("\n");
                }
                if (object instanceof Diodes){
                    String diodeoutput=String.format("Diode %s: ",((Diodes) object).name);
                    fileout.write(diodeoutput);
                    for(int i=0;i<((Diodes) object).vs.size();i++){
                        fileout.write("("+((Diodes) object).vs.get(i)+" "+((Diodes) object).is.get(i)+" "+((Diodes) object).vs.get(i)*((Diodes) object).is.get(i)+")\t");
                    }
                    fileout.write("\n");
                }

            }
            fileout.write("------------\n");
            fileout.close();
        }
        catch (IOException e){

        }
    }

    public static void checkVSource() {
        for (Object object : Brain.everything) {
            if (object instanceof VSource) {
                for (Object object2 : Brain.everything){
                    if((object2 instanceof VSource)&&(((VSource) object2).node[0]==((VSource) object).node[0])&&(((VSource) object2).node[1]==((VSource) object).node[1])){
                        if(Math.abs(((VSource) object).getV(((VSource) object).node[0])-((VSource) object2).getV(((VSource) object2).node[0]))>dV){
                            Main.ErrorBox("ERROR -3","At least two VSources with different voltage are parallel at "+i+"th second" );
                            error=1;
                            //System.exit(0);
                        }
                    }
                    if((object2 instanceof VSource)&&(((VSource) object2).node[1]==((VSource) object).node[0])&&(((VSource) object2).node[0]==((VSource) object).node[1])){
                        if(Math.abs(((VSource) object).getV(((VSource) object).node[0])-((VSource) object2).getV(((VSource) object2).node[1]))>dV){
                            Main.ErrorBox("ERROR -3","At least two VSources with different voltage are parallel at "+i+"th second" );
                            error=1;
                            //System.exit(0);
                        }
                    }
                }
            }
        }
    }


    public static void checkISourse(){
        for (Object object : Brain.everything) {
            if (object instanceof ISource) {
                int k=0,tt=0;
                for (Object object2 : ((ISource) object).node[0].elements){
                    tt++;
                    if (object2 instanceof ISource){
                        k++;
                    }
                }
                if(tt==k){
                    if(Math.abs(((ISource) object).node[0].getTotalI(((ISource) object).node[0]))>dI){
                        Main.ErrorBox("ERROR -2"," ISources are series at "+i+"th second");
                        error=1;
                        //System.exit(0);
                    }
                }
                k=0;
                tt=0;
                for (Object object2 : ((ISource) object).node[1].elements){
                    tt++;
                    if (object2 instanceof ISource){
                        k++;
                    }
                }
                if(tt==k){
                    if(Math.abs(((ISource) object).node[1].getTotalI(((ISource) object).node[1]))>dI){
                        Main.ErrorBox("ERROR -2"," ISources are series at "+i+"th second");
                        error=1;
                        //System.exit(0);
                    }
                }
            }
        }
    }

    public static void checkISourceVSource(){
        for (Object object : Brain.everything) {
            if ((object instanceof VSource)&&!(object instanceof HSource)&&!(object instanceof ESource)) {
                for (Object object2 : Brain.everything){
                    if((object2 instanceof VSource)&&!(object2 instanceof HSource)&&!(object2 instanceof ESource)&&(((VSource) object2).node[0]==((VSource) object).node[0])&&(((VSource) object2).node[1]==((VSource) object).node[1])){
                        if(Math.abs(((VSource) object).getV(((VSource) object).node[0])-((VSource) object2).getV(((VSource) object2).node[0]))>dV){
                            Main.ErrorBox("ERROR -3","At least two VSources with different voltage are parallel ");
                            error=1;
                            //System.exit(0);
                        }
                    }
                    if((object2 instanceof VSource)&&!(object2 instanceof HSource)&&!(object2 instanceof ESource)&&(((VSource) object2).node[1]==((VSource) object).node[0])&&(((VSource) object2).node[0]==((VSource) object).node[1])){
                        if(Math.abs(((VSource) object).getV(((VSource) object).node[0])-((VSource) object2).getV(((VSource) object2).node[1]))>dV){
                            Main.ErrorBox("ERROR -3","At least two VSources with different voltage are parallel ");
                            error=1;
                            //System.exit(0);
                        }
                    }
                }
            }
            if ((object instanceof ISource)&&!(object instanceof GSource)&&!(object instanceof FSource)) {
                int k=0,tt=0;
                for (Object object2 : ((ISource) object).node[0].elements){
                    tt++;
                    if ((object2 instanceof ISource)&&!(object2 instanceof GSource)&&!(object2 instanceof FSource)){
                        k++;
                    }
                }
                if(tt==k){
                    if(Math.abs(((ISource) object).node[0].getTotalI(((ISource) object).node[0]))>dI){
                        Main.ErrorBox("ERROR -2"," ISources with different value are series ");
                        error=1;
                        //System.exit(0);
                    }
                }
                k=0;
                tt=0;
                for (Object object2 : ((ISource) object).node[1].elements){
                    tt++;
                    if ((object2 instanceof ISource)&&!(object2 instanceof GSource)&&!(object2 instanceof FSource)){
                        k++;
                    }
                }
                if(tt==k){
                    if(Math.abs(((ISource) object).node[1].getTotalI(((ISource) object).node[1]))>dI){
                        Main.ErrorBox("ERROR -2"," ISources with different value are series ");
                        error=1;
                        //System.exit(0);
                    }
                }
            }
        }
    }

    public static void checkExistenceOfGND(){
        int temp=0;
        for(Object o : everything){
            if (o instanceof Ground){
                temp++;
            }
        }
        if(temp==0){
            Main.ErrorBox("ERROR -4"," GND is missing ");
            error=1;
            //System.exit(0);
        }
    }

    public static void checkValueOfGND(){
        for(Object o : everything){
            if (o instanceof VSource){
                if((((VSource) o).node[0].name.equals(((VSource) o).node[1].name))||((((VSource) o).node[0].name.equals("0")))){
                    if(Math.abs(((VSource) o).getV(((VSource) o).node[0]))>dV){
                        Main.ErrorBox("ERROR -4"," You can't change the voltage of GND ");
                        error=1;
                        //System.exit(0);
                    }
                }
            }
        }
    }

    public static void checkFloating(){
        for (Object o : everything){
            if (o instanceof Nodes){
                if (((Nodes) o).name.equals("0")){
                    continue;
                }
                //System.out.println("Node : "+((Nodes) o).name);
                ArrayList<Nodes>  orderOfNodes = new ArrayList<Nodes>();
                ArrayList<Element> elements = new ArrayList<Element>();
                orderOfNodes.add((Nodes) o);
                if(!checkFloatingNode((Nodes) o, orderOfNodes, elements)){
                    Main.ErrorBox("ERROR -5","Node "+((Nodes) o).name+" is floating ");
                    error=1;
                    //System.exit(0);
                }
                //System.out.println("-------------------");
            }
        }
    }

    public static boolean checkFloatingNode(Nodes firstNode , ArrayList<Nodes> orderOfNodes , ArrayList<Element> element){
        int temp=0;
        //System.out.println("-------------------------");
        //System.out.print("[");
        for (int i=0;i<orderOfNodes.size();i++){
            //System.out.print(orderOfNodes.get(i).name+",");
        }
        //System.out.println("]");
        //System.out.print("[");
        for (int i=0;i<element.size();i++){
            //System.out.print(element.get(i).name+",");
        }
        //System.out.println("]");
        for (Element e:orderOfNodes.get(orderOfNodes.size()-1).elements){
            temp=0;
            for (Element value : element) {
                if (e.equals(value)) {
                   // System.out.println("omadam inja element : "+value.name);
                    temp++;
                }
            }
            if (temp>0){
                //System.out.println("omadam inja");
                continue;
            }
            else {
                //System.out.println(e.name);
                //System.out.println(element);
                //System.out.println(element.size());
                if (element.size()>0){
                    //System.out.println(element);
                    if(e.otherNode(orderOfNodes.get(orderOfNodes.size()-1)).equals(firstNode)){
                        for (Nodes n : orderOfNodes){
                            if (n.name.equals("0")){
                                return true;
                            }
                        }
                    }
                }
                orderOfNodes.add((Nodes) e.otherNode(orderOfNodes.get(orderOfNodes.size()-1)));
                element.add(e);
                if (checkFloatingNode(firstNode, orderOfNodes, element)){
                    return true;
                }
                orderOfNodes.remove(orderOfNodes.size()-1);
                element.remove(element.size()-1);
            }
        }
        return false;
    }
}