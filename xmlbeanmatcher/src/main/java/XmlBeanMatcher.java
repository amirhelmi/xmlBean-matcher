
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class XmlBeanMatcher
{
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private List<Node> assertions = new ArrayList<>();
    private List<Node> actuals = new ArrayList<>();

    public <T extends XmlObject> void match(Class<T> rootXmlBeanClass, Object assertion, Object actual)
    {
        String name = rootXmlBeanClass.getSimpleName();
        Node assertNode = new Node(assertion, null, name);
        Node actualNode = new Node(actual, null, name);
        startMatch(rootXmlBeanClass, assertNode, actualNode);
        assertList(assertions, actuals);
    }

    private void startMatch(Class<?> xmlClass, Node assertion, Node actual)
    {
        getReadMethods(xmlClass).forEach(method -> {
            if (assertion.object() == null && actual.object() != null && unIgnorable(actual.object()))
            {
                actuals.add(actual);
            }
            else if (assertion.object() != null && actual.object() == null && unIgnorable(assertion.object()))
            {
                assertions.add(assertion);
            }
            else if (assertion.object() != null && unIgnorable(assertion.object()))
            {
                filterReturnType(method, assertion, actual);
            }
        });
        if (!assertion.getMap().isEmpty())
        {
            assertions.add(assertion);
        }
        if (!actual.getMap().isEmpty())
        {
            actuals.add(actual);
        }
    }

    private void filterReturnType(Method method, Node assertion, Node actual)
    {
        if (assertAble(method))
        {
            assertion.getMap().put(method.getName(), String.valueOf(invokeMethod(method, assertion)));
            actual.getMap().put(method.getName(), String.valueOf(invokeMethod(method, actual)));
        }
        else if (StringEnumAbstractBase.class.isAssignableFrom(assertion.object().getClass()))
        {
            assertion.getMap().put(assertion.object().getClass().getSimpleName(), assertion.object().toString());
            actual.getMap().put(actual.object().getClass().getSimpleName(), actual.object().toString());
        }
        else if (isAnArray(method))
        {
            ListMultimap<Method, Node> assertionMap = ArrayListMultimap.create();
            ListMultimap<Method, Node> actualMap = ArrayListMultimap.create();

            assertions.addAll(filterReturnTypeArray(method, assertion, assertionMap));
            actuals.addAll(filterReturnTypeArray(method, actual, actualMap));

            matchNextLevel(assertionMap, actualMap);
        }
        else if (recurseAble(method))
        {
            String name = getName(method);
            Node assertReturnNode = new Node(invokeMethod(method, assertion), assertion, name);
            Node actualReturnNode = new Node(invokeMethod(method, actual), actual, name);
            startMatch(method.getReturnType(), assertReturnNode, actualReturnNode);
        }
    }

    private List<Node> filterReturnTypeArray(Method method, Node node, Multimap<Method, Node> nextLevelMap)
    {
        List<Node> nodes = new ArrayList<>();
        Object[] objectArray = invokeMethod(method, node) != null ? (Object[]) invokeMethod(method, node) : EMPTY_ARRAY;
        for (Object elementObject : objectArray)
        {
            if (StringEnumAbstractBase.class.isAssignableFrom(elementObject.getClass()))
            {
                node.getMap().put(elementObject.getClass().getSimpleName(), elementObject.toString());
            }
            else
            {
                Node elementNode = new Node(elementObject, node, getName(method));
                getReadMethods(elementObject.getClass()).forEach(
                    elementMethod -> {
                        if (assertAble(elementMethod) && recurseAble(elementMethod))
                        {
                            elementNode.getMap().put(elementMethod.getName(), String.valueOf(invokeMethod(elementMethod, elementNode)));
                        }
                        if (isAnArray(elementMethod))
                        {
                            filterReturnTypeArray(elementMethod, elementNode, nextLevelMap);
                        }
                        else
                        {
                            nextLevelMap.put(elementMethod, elementNode);
                        }
                    }
                );
                if (!elementNode.getMap().isEmpty())
                {
                    nodes.add(elementNode);
                }
            }
        }
        return nodes;
    }

    private void matchNextLevel(ListMultimap<Method, Node> assertionMap, ListMultimap<Method, Node> actualMap)
    {
        assertionMap.asMap().keySet().forEach(method -> {
            if (recurseAble(method))
            {
                List<Node> assertionNodes = assertionMap.get(method);
                List<Node> actualNodes = actualMap.get(method);

                IntStream.range(0, assertionNodes.size() - actualNodes.size()).forEach(value -> actualNodes.add(new Node(null, null, getName(method))));
                IntStream.range(0, actualNodes.size() - assertionNodes.size()).forEach(value -> assertionNodes.add(new Node(null, null, getName(method))));

                for (int i = 0; i < assertionNodes.size(); i++)
                {
                    Node assertion = assertionNodes.get(i);
                    Node actual = actualNodes.get(i);

                    String name = getName(method);
                    Node assertReturnNode = new Node(invokeMethod(method, assertion), assertion, name);
                    Node actualReturnNode = new Node(invokeMethod(method, actual), actual, name);
                    startMatch(method.getReturnType(), assertReturnNode, actualReturnNode);
                }
            }
        });
    }

    private void assertList(List<Node> assertionList, List<Node> actualList)
    {
        List<Node> parentAssert = assertionList.stream().map(Node::parent).collect(Collectors.toList());
        List<Node> parentActual = actualList.stream().map(Node::parent).collect(Collectors.toList());
        Map<String, Node> assertionMap = listToIdentityMap(assertionList, parentAssert);
        Map<String, Node> actualMap = listToIdentityMap(actualList, parentActual);

        MapDifference<String, Node> difference = Maps.difference(assertionMap, actualMap);
        if (!difference.areEqual())
        {
            Collection<Node> left = difference.entriesOnlyOnLeft().values();
            Collection<Node> right = difference.entriesOnlyOnRight().values();
            throw new AssertionError("\nExpected:\n" + left.stream().map(Node::toString).collect(Collectors.joining("\n"))
                                         + "\nbut was:\n" + right.stream().map(Node::toString).collect(Collectors.joining("\n")));
        }
    }

    private Map<String, Node> listToIdentityMap(List<Node> nodes, List<Node> parenNodes)
    {
        return nodes.stream()
            .filter(node -> !parenNodes.contains(node))
            .map(node -> {
                int frequency = Collections.frequency(nodes, node);
                return frequency > 0 ? node.withDuplicateCount(frequency) : node;
            })
            .collect(Collectors.toMap(Node::toString, node -> node, (x, y) -> y));
    }

    private List<Method> getReadMethods(Class<?> clazz)
    {
        List<Method> methods = new ArrayList<>();
        try
        {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors())
            {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && !"get_wscanon_text".equals(readMethod.getName()) && !"getStringValue".equals(readMethod.getName()))
                {
                    methods.add(readMethod);
                }
            }
        }
        catch (IntrospectionException ignored)
        {
        }
        return methods;
    }

    private boolean unIgnorable(Object object)
    {
        return unIgnoreExcept.get(object.getClass());
    }

    private Map<Class<?>, Boolean> unIgnoreExcept = filterByDefault(true, Sets.newHashSet(XmlCalendar.class,
                                                                                          String.class,
                                                                                          byte[].class));

    private boolean recurseAble(Method method)
    {
        return shouldRecurseExcept.get(method.getDeclaringClass());
    }

    private Map<Class<?>, Boolean> shouldRecurseExcept = filterByDefault(true, Sets.newHashSet(XmlObjectBase.class,
                                                                                               Object.class));

    private boolean assertAble(Method method)
    {
        return assertOnly.get(method.getReturnType());
    }

    private Map<Class<?>, Boolean> assertOnly = filterByDefault(false, Sets.newHashSet(String.class,
                                                                                       Boolean.class,
                                                                                       Integer.class
    ));

    private static <T> Map<T, Boolean> filterByDefault(Boolean defaultBoolean, Iterable<T> valuesInOrder)
    {
        Map<T, Boolean> map = new HashMap<>();//todo ProvidesDefaultWithMissingEntryPutOnceHashMap
        for (T value : valuesInOrder)
        {
            map.put(value, !defaultBoolean);
        }
        return Collections.unmodifiableMap(map);
    }

    private boolean isAnArray(Method method)
    {
        return method.getReturnType().isArray() && !Objects.equals(method.getReturnType(), byte[].class);
    }

    private String getName(Method method)
    {
        return method.getName() + '=' + method.getReturnType().getSimpleName();
    }

    private Object invokeMethod(Method method, Node node)
    {
        try
        {
            if (node.object() != null)
            {
                return method.invoke(node.object());
            }
            else
            {
                return null;
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            return e;
        }
    }
}