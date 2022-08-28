import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class VSource extends Source {
    public VSource(String name,Nodes node1, Nodes node2, double offset,double amplitude,double frequency,double phase){
        super(name,node1,node2,offset,amplitude,frequency,phase,"vs");
    }
    public VSource(String name,Nodes node1, Nodes node2){
        super(name,node1,node2,0,0,0,0,"dvs");
    }

    public double getV(Nodes node){
        if (node.equals(this.node[0]))
            return value();
        else
            return -value();
    }

    @Override
    public double getI(Nodes thisNode) {
        double hold=0;
        if(thisNode.name.equals(node[0].name)){
            for (Element element:node[1].elements){
                if (!element.equals(this)){
                    hold+=element.getI(node[1]);
                }
            }
        }
        if(!thisNode.name.equals(node[0].name)){
            for (Element element:node[0].elements){
                if (!element.equals(this)){
                    hold+=element.getI(node[0]);
                }
            }
        }
        return hold;
    }
}

class HSource extends VSource {
    Element element;
    double a;

    public HSource(String name,Nodes node1,Nodes node2,Element element, double a){
        super(name,node1,node2,0,0,0,0);
        this.element=element;
        this.a=a;
        try {
            imageAddress= new FileInputStream(System.getProperty("user.dir")+"\\elements\\"+"dvs"+".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getV(Nodes node) {
        double v=a*element.getI(element.node[1]);
        //System.out.println("VH : "+v);
        if (node.equals(this.node[0]))
            return v;
        else
            return -v;
    }

}

class ESource extends VSource {
    Nodes node1,node2;
    double a;
    public ESource(String name,Nodes node1,Nodes node2,Nodes node21,Nodes node22, double a){
        super(name,node1,node2,0,0,0,0);
        this.node1=node21;
        this.node2=node22;
        this.a=a;
        try {
            imageAddress= new FileInputStream(System.getProperty("user.dir")+"\\elements\\"+"dvs"+".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getV(Nodes node) {
        double v=a * (node1.v-node2.v);
        if (node.equals(this.node[0]))
            return v;
        else
            return -v;
    }

}

