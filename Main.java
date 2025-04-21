import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        List<Employee> xmlList = parseXML("data.xml");
        String jsonFromXml = listToJson(xmlList);
        writeString(jsonFromXml, "data2.json");
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> String listToJson(List<T> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {}.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                Employee emp = new Employee();
                emp.id = Long.parseLong(getElementValue(element, "id"));
                emp.firstName = getElementValue(element, "firstName");
                emp.lastName = getElementValue(element, "lastName");
                emp.country = getElementValue(element, "country");
                emp.age = Integer.parseInt(getElementValue(element, "age"));
                employees.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    private static String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0) return "";
        return nodeList.item(0).getTextContent();
    }
}
