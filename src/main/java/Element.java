import javafx.scene.image.Image;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
public abstract class Element {
    static ArrayList<Element> elements=new ArrayList<>();
    String name;
    Nodes[] node=new Nodes[2];
    ArrayList<Double> vs=new ArrayList<>();
    ArrayList<Double> is=new ArrayList<>();
    ArrayList<Double> ps = new ArrayList<>();
    double integralV=0,integralI=0,dV=0,dI=0;
    FileInputStream imageAddress;


    public Element(String name,Nodes node1,Nodes node2,String address){

        try {
            imageAddress= new FileInputStream(System.getProperty("user.dir")+"\\elements\\"+address+".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        this.name=name;
        node[0]=node1;
        node[1]=node2;
    }

    public static void update(){ //ghable update nodes
        for (Element element:elements){
            double v=element.node[0].v-element.node[1].v,i=element.getI(element.node[0]);
            double v0=0,i0=0;
            if (element.vs.size()!=0){
                v0=element.vs.get(element.vs.size()-1);
                i0=element.is.get(element.is.size()-1);
            }
            else {
                v0=0;
                i0=0;
            }
            if((element instanceof ISource)||(element instanceof Diode2)){
                element.is.add(-i);
                element.ps.add(-i*v);
            }
            else {
                element.is.add(i);
                element.ps.add(i*v);
            }
            element.vs.add(v);
            element.dV=(v-v0)/Brain.dt;
            element.dI=(i-i0)/Brain.dt;
            element.integralV+=v*Brain.dt;
            element.integralI+=i*Brain.dt;
            if (element instanceof Capacitor)
                ((Capacitor) element).setDC();
            if (element instanceof Inductor){
                ((Inductor) element).I =-1* ((Inductor) element).getI(((Inductor) element).node[0]);
                ((Inductor) element).setDL();
            }
        }
    }

    public static void reset(){
        for (Element element:elements){
            if (element.vs.size() - +1 > 0) {
                element.vs.subList(0, element.vs.size() - +1).clear();
            }
            if (element.is.size() - +1 > 0) {
                element.is.subList(0, element.is.size() - +1).clear();
            }
            if (element.ps.size() - +1 > 0) {
                element.ps.subList(0, element.ps.size() - +1).clear();
            }
        }
    }

    public abstract double getI(Nodes thisNode);

    public Nodes otherNode(Nodes thisNode){
        if (thisNode.equals(node[0]))
            return node[1];
        else
            return node[0];
    }

}