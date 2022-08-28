import java.util.ArrayList;
import java.util.HashMap;

public class Resistor extends Element {
    HashMap <Integer, ArrayList<double[]>> r;

    public Resistor(String name, Nodes node1,Nodes node2, HashMap <Integer,ArrayList<double[]>> r){
        super(name, node1, node2,"resistor");
        this.r=r;
    }

    public double getR(){
        double hold=0;

        for (int deg:r.keySet()){
            double coeff=0;
            for (double[] cp:r.get(deg)){
                coeff+=cp[0]*Math.pow(10,cp[1]);
                //System.out.println("coeff : "+coeff);
            }
            //System.out.println("time : "+Simulator.i);
            //System.out.println("test : "+Math.pow(Simulator.t,deg));
            //System.out.println("delta hold : "+coeff*Math.pow(Simulator.t,deg));
            hold+=coeff*Math.pow(Brain.i,deg);
        }
        if (hold<=0&&ControllerMainPage.errorCheck) {
            //System.out.println("Resistance : "+hold);
            Main.ErrorBox("ERROR -1",name+" value is negative at "+Brain.i+" second" );
            Brain.error=1;
            //System.out.println("Negative Resistance");
            //System.exit(0);
        }
        //System.out.println("Resistance : "+hold);
        return hold;
    }

    @Override
    public double getI(Nodes thisNode) {
        double r=getR();
        double i=(node[0].v-node[1].v)/r;
        //System.out.println("IR : "+i);
        if (thisNode.equals(this.node[0]))
            return -i;
        else
            return i;
    }

}