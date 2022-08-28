

import java.util.ArrayList;
import java.util.HashMap;

public class Inductor extends Element {
    HashMap<Integer, ArrayList<double[]>> l;
    double dL;
    double I=0;
    public Inductor(String name, Nodes node1,Nodes node2, HashMap<Integer, ArrayList<double[]>> l){
        super(name, node1, node2,"inductor");
        this.l=l;
    }

    public double getL(){
        double hold=0;
        for (int deg:l.keySet()){
            double coeff=0;
            for (double[] cp:l.get(deg)){
                coeff+=cp[0]*Math.pow(10,cp[1]);
            }
            hold+=coeff*Math.pow(Brain.i,deg);
        }
        if (hold<=0&&ControllerMainPage.errorCheck) {
            //System.out.println("Resistance : "+hold);
            Main.ErrorBox("ERROR -1",name+" value is negative at "+Brain.i+" second" );
            Brain.error=1;
            //System.out.println("Negative Inductance");
            //System.exit(0);
        }
        return hold;
    }

    public void setDL(){
        double hold=getL();
        Brain.t-=Brain.dt;
        hold-=getL();
        Brain.t+=Brain.dt;
        hold=hold/Brain.dt;
        dL=hold;
    }

    @Override
    public double getI(Nodes thisNode) {
        double l=getL(),v=node[0].v-node[1].v;
        //System.out.println("----------------");
        //System.out.println("V inductor : "+v);
        //System.out.println("I inductor : "+I);
        //System.out.println("----------------");
        double i=I+v/l*Brain.dt;
        if (thisNode.equals(this.node[0]))
            return -i;
        else
            return i;
    }
}