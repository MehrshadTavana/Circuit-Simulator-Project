import java.io.FileInputStream;
import java.io.FileNotFoundException;

class ISource extends Source {
    public ISource(String name,Nodes node1, Nodes node2, double offset,double amplitude,double frequency,double phase){
        super(name,node1,node2,offset,amplitude,frequency,phase,"is");
    }
    public ISource(String name,Nodes node1, Nodes node2){
        super(name,node1,node2,0,0,0,0,"dis");
    }

    @Override
    public double getI(Nodes thisNode) {
        double i=value();
        if (thisNode.equals(node[0]))
            return i;
        else
            return -i;
    }

}

class FSource extends ISource {
    Element element;
    double a;
    public FSource(String name,Nodes node1,Nodes node2,Element element, double a){
        super(name,node1,node2);
        try {
            imageAddress= new FileInputStream(System.getProperty("user.dir")+"\\elements\\"+"dis"+".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.element=element;
        this.a=a;
    }

    @Override
    public double getI(Nodes thisNode) {
        //System.out.println(element.getI(element.node[1]));
        //System.out.println(a);
        double i=a*element.getI(element.node[1]);
        if(element instanceof ISource){
            i=-i;
        }
        //System.out.println("node0 : "+element.node[0].v);
        //System.out.println("node1 : "+element.node[1].v);
        //System.out.println("If : "+i);
        if (thisNode.equals(node[0]))
            return i;
        else
            return -i;
    }

}

class GSource extends ISource {
    Nodes node1,node2;
    double a;
    public GSource(String name,Nodes node1,Nodes node2,Nodes node21,Nodes node22, double a){
        super(name,node1,node2);
        this.node1=node21;
        this.node2=node22;
        this.a=a;
        try {
            imageAddress= new FileInputStream(System.getProperty("user.dir")+"\\elements\\"+"dis"+".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getI(Nodes thisNode) {
        double i=a*(node1.v-node2.v);
        //System.out.println("IG : "+i);
        if (thisNode.equals(node[0]))
            return i;
        else
            return -i;
    }

}