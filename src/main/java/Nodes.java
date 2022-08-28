import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class Nodes {
    //ArrayList<String> names=new ArrayList<String>();
    static ArrayList<Nodes> nodes=new ArrayList<>();
    String name;
    double v,finalV=0;
    boolean visited=false;
    static boolean checkGraph=false;
    ArrayList<Element> elements= new ArrayList<>();
    HashMap<Nodes,Integer> neighbours=new HashMap<>();
    ArrayList<Double> vs=new ArrayList<>();

    public static void updateNodes(){
        for (Nodes node: nodes){
            if (node instanceof Node) {
                node.finalV=node.v;
                node.vs.add(node.finalV);
            }
        }
    }

    public double getTotalI(Nodes node){
        double I=0;
        for (Element element:node.elements){
            I+=element.getI(node);
        }
        return I;
    }

    public static void resetNodes(){
        for (Nodes node: nodes){
            node.visited=false;
        }
    }

    public static void updateNeighbourNodes(){
        for (Nodes node: nodes){
            for (Element element: node.elements){
                if (element instanceof Diode1) if (element.getI(element.node[0])==0&&checkGraph)
                    continue;
                Nodes hold=element.otherNode(node);
                if (node.neighbours.containsKey(hold)){
                    int n=node.neighbours.get(hold);
                    node.neighbours.replace(hold,n+1);
                }
                else
                    node.neighbours.put(hold,1);
            }
        }
        if (!checkGraph){
            checkGraph=true;
            for (Nodes node: nodes){
                if (node.neighbours.size()==1){ //yek done
                    Nodes n=((ArrayList<Nodes>) node.neighbours.keySet()).get(0);
                    if (node.neighbours.get(n)==1){ //yek bar
                        System.out.println("Error -5");

                    }
                }
            } //halghe darim
            checkGND();
        }
        //error -2
        ArrayList<ISource> iSources=new ArrayList<>();
        for (Element element:Element.elements){
            if (element instanceof ISource)
                iSources.add((ISource) element);
        }
        for (ISource iSource:iSources){
            if (!(checkISource(iSource,iSource.getI(iSource.node[0]),iSource.node[0])&&checkISource(iSource,iSource.getI(iSource.node[1]),iSource.node[1]))){
                System.out.println("Error -2");

            }
        }
        //error -3
        ArrayList<VSource> vSources=new ArrayList<>();
        for (Element element:Element.elements){
            if (element instanceof VSource)
                vSources.add((VSource) element);
        }
        for (VSource vSource:vSources){
            if (!checkVSource(vSource)){
                System.out.println("Error -3");

            }
        }
    }

    public static boolean checkVSource(VSource vSource){
        Nodes node1=vSource.node[0];
        Nodes node2=vSource.node[1];
        double v1=vSource.getV(node1);
        ArrayList<Element> neighbours1=new ArrayList<>(node1.elements);
        ArrayList<Element> neighbours2=new ArrayList<>(node2.elements);
        neighbours1.remove(vSource);
        neighbours2.remove(vSource);
        ArrayList<VSource> sub=new ArrayList<>();
        for (Element e:neighbours1){
            if (e instanceof VSource) if (neighbours2.contains(e)) {
                sub.add((VSource) e);
                double v2=((VSource) e).getV(node1);
                if (Math.abs(v1-v2)>0.01)
                    return false;
            }
        }
        return true;
    }

    public static boolean checkISource(Element element,double i,Nodes node){
        ArrayList<Element> neighbours = new ArrayList<>(node.elements);
        neighbours.remove(element);
        if (neighbours.size()==1){
            Element neighbour=neighbours.get(0);
            if (neighbour instanceof ISource){
                if (Math.abs(i+neighbour.getI(node))<0.01)
                    return true;
                else
                    return false;
            }
            else{
                if (neighbour instanceof Diode1){
                    if ((i>0&&node.equals(neighbour.node[1]))||(i<0&&node.equals(neighbour.node[0])))
                        return false;
                }
            }
            return checkISource(neighbour,i,neighbour.otherNode(node));
        }
        // todo nazar? sade tarin form bod (ehtemalan hamino mikhan) halataye sakh bad bug mikhore
        double hold=i;
        ArrayList<Diode1> diodes=new ArrayList<>();
        for (Element e:neighbours){
            if (!((e instanceof ISource)||(e instanceof Diode1)))
                return true; // TODO: 20/06/18 anvae khataha eg: halghe, manbae jaryan onvar etc
            if (e instanceof ISource){
                hold+=e.getI(node);
            }
            if (e instanceof Diode1)
                diodes.add((Diode1) e);
        }
        if (Math.abs(hold)<0.01)
            return true;
        if (diodes.size()==0)
            return false;
        if (diodes.size()==1){
            if ((hold>0&&node.equals(diodes.get(0).node[1]))||(hold<0&&node.equals(diodes.get(0).node[0])))
                return false;
            else
                return checkISource(diodes.get(0),hold,diodes.get(0).otherNode(node));
        }
        return true;
        /* TODO: 20/06/18 anvae khataha @panahande:
            farz konim tazmini javab nist, masalan already ba hamon voltaja dadad ha dooran,
            na ba ziyad kardan na ba kam kardan khata kam nemishe */
    }

    public static void checkGND(){
        HashMap<Nodes,Boolean> connection=new HashMap<>();
        Nodes gnd=null;
        for (Nodes node: nodes){
            if (node instanceof Node)
                connection.put(node,false);
            if (node instanceof Ground) {
                gnd=node;
                connection.put(gnd,true);
            }
        }
        if (gnd==null){ // TODO: 20/06/13 Error???
            System.out.println("Error -4");

        }
        else {
            //directly connected to gnd
            connection=setConnection(connection,gnd);
            //indirect connections TODO: 20/06/13 why???
            for (Element element:Element.elements){
                if (!connection.get(element.node[0])){
                    if (isConnected(element.node[0],connection)){
                        connection.replace(element.node[0],true);
                        connection=setConnection(connection,element.node[0]);
                    }
                }
            }
        }


        for (Nodes node:nodes){
            if (!connection.get(node)){
                System.out.println("Error -5");

            }
        }

    }

    public static HashMap<Nodes,Boolean> setConnection(HashMap<Nodes,Boolean> connection,Nodes origin){
        for (Nodes node: origin.neighbours.keySet()){
            if (!connection.get(node)){
                connection.replace(node,true);
                connection=setConnection(connection,node);
            }
        }
        return connection;
    }

    public static boolean isConnected(Nodes origin,HashMap<Nodes,Boolean> connection){
        if (connection.get(origin))
            return true;
        boolean isConnected=false;
        ArrayList<Nodes> graph=new ArrayList<>();
        graph=getGraph(graph,origin);
        ArrayList<Element> elements=new ArrayList<>();
        for (Nodes node:graph){
            for (Element element:node.elements){
                if (!elements.contains(element))
                    elements.add(element);
            }
        }
        for (Element element: elements){
            if (element instanceof FSource){
                if (!elements.contains(((FSource) element).element))
                    isConnected=isConnected||isConnected(((FSource) element).element.node[0],connection);
            }
            if (element instanceof GSource){
                if (!graph.contains(((GSource) element).node1))
                    isConnected=isConnected||isConnected(((GSource) element).node1,connection);
            }
            if (element instanceof ESource){
                if (!graph.contains(((ESource) element).node1))
                    isConnected=isConnected||isConnected(((ESource) element).node1,connection);
            }
            if (element instanceof HSource){
                if (!elements.contains(((HSource) element).element))
                    isConnected=isConnected||isConnected(((HSource) element).element.node[0],connection);
            }
        } // TODO: 20/06/13 A to B, B to A+ GND -__-, an arraylist of graphs!?
        return isConnected;
    }

    public static HashMap<Nodes,Boolean> getGraph(Nodes node){
        HashMap<Nodes,Boolean> graph=new HashMap<>();
        ArrayList<Nodes> ns=new ArrayList<>();
        ns=getGraph(ns,node);
        for (Nodes n:ns){
            graph.put(n,false);
        }
        return graph;
    }

    public static ArrayList<Nodes> getGraph(ArrayList<Nodes> nodes, Nodes origin){
        for (Nodes node:origin.neighbours.keySet()){
            if (!nodes.contains(node)){
                nodes.add(node);
                nodes=getGraph(nodes,node);
            }
        }
        return nodes;
    }


    public static ArrayList<Element> getParallelElements(Nodes node1,Nodes node2){
        ArrayList<Element> parallels=new ArrayList<>();
        for (Element element: node1.elements){
            if (node2.elements.contains(element))
                parallels.add(element);
        }
        return parallels;
    }
}

class Node extends Nodes {

    public Node (String name){
        this.name=name;
        Nodes.nodes.add(this);
    }

}

class Ground extends Nodes {
    private static Ground ground;
    private Ground() {
        this.name="0";
        v=0;
    }

    public static Ground getInstance() {
        if (ground == null)
            ground = new Ground();
        Nodes.nodes.add(ground);
        return ground;
    }

}



