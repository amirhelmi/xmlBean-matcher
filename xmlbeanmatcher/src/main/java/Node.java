import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

class Node
{
    private final String name;
    private final Node parent;
    private final Map<String, String> map = new TreeMap<>();
    private Object object;

    Node(Object object, Node parent, String name)
    {
        this.name = name;
        this.object = object;
        this.parent = parent;
    }

    String getName()
    {
        return name;
    }

    Node parent()
    {
        return parent;
    }

    public Map<String, String> getMap()
    {
        return map;
    }

    Object object()
    {
        return object;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Node node = (Node) o;
        return Objects.equals(name, node.name) &&
            Objects.equals(parent, node.parent) &&
            Objects.equals(map, node.map);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, parent, map);
    }

    @Override
    public String toString()
    {
        return printNode(parent) + name + map;
    }

    static String printNode(Node node)
    {
        return node != null ? node.toString() : "";
    }

    public Node withDuplicateCount(int i)
    {
        map.put("Instances", String.valueOf(i));
        return this;
    }
}
