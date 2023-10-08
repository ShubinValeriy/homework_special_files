import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException,
            SAXException, ParseException {
        // Задание 1
        csvCreate();
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        // Задание 2
        xmlCreate();
        List<Employee> listEmployee = parseXML("data.xml");
        String jsonEmployee = listToJson(listEmployee);
        writeString(jsonEmployee, "data2.json");
        //Задание 3
        String json1 = readString("data.json");
        List<Employee> employeeList = jsonToList(json1);
        employeeList.forEach(System.out::println);
    }

    private static List<Employee> jsonToList(String json) throws ParseException {
        List<Employee> employeeList = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            Employee employee = gson.fromJson(jsonArray.get(i).toString(), Employee.class);
            employeeList.add(employee);
        }
        return employeeList;
    }

    private static String readString(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder json = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                json.append(s);
            }
            return json.toString();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }


    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;
            Employee employee = new Employee();
            employee.id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
            employee.firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
            employee.lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
            employee.country = element.getElementsByTagName("country").item(0).getTextContent();
            employee.age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
            list.add(employee);
        }
        return list;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void csvCreate() {
        String[][] persons = {
                "1,John,Smith,USA,25".split(","),
                "2,Inav,Petrov,RU,23".split(",")};
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            for (String[] person : persons) {
                writer.writeNext(person);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void xmlCreate() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element staff = document.createElement("staff");
        document.appendChild(staff);

        Element employee = document.createElement("employee");
        staff.appendChild(employee);
        Element id = document.createElement("id");
        Element firstName = document.createElement("firstName");
        Element lastName = document.createElement("lastName");
        Element country = document.createElement("country");
        Element age = document.createElement("age");
        id.appendChild(document.createTextNode("1"));
        firstName.appendChild(document.createTextNode("John"));
        lastName.appendChild(document.createTextNode("Smith"));
        country.appendChild(document.createTextNode("USA"));
        age.appendChild(document.createTextNode("25"));
        employee.appendChild(id);
        employee.appendChild(firstName);
        employee.appendChild(lastName);
        employee.appendChild(country);
        employee.appendChild(age);

        Element employee2 = document.createElement("employee");
        staff.appendChild(employee2);
        Element id2 = document.createElement("id");
        Element firstName2 = document.createElement("firstName");
        Element lastName2 = document.createElement("lastName");
        Element country2 = document.createElement("country");
        Element age2 = document.createElement("age");
        id2.appendChild(document.createTextNode("2"));
        firstName2.appendChild(document.createTextNode("Ivan"));
        lastName2.appendChild(document.createTextNode("Petrov"));
        country2.appendChild(document.createTextNode("RU"));
        age2.appendChild(document.createTextNode("23"));
        employee2.appendChild(id2);
        employee2.appendChild(firstName2);
        employee2.appendChild(lastName2);
        employee2.appendChild(country2);
        employee2.appendChild(age2);

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> String listToJson(List<T> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        return gson.toJson(list, listType);
    }
}


